package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.repository.dto.ChargingProfile;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ChargingProfileForm;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileFilterType;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import ocpp.cp._2015._10.ChargingProfileKindType;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingRateUnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javax.validation.Valid;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * Derived from SteVe project
 *
 * @author Chuck Harrison <cfharr@gmail.com>
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */

// respond to emoncms-style fulljson data posting,
//    steve/currentlimit/post?node=STA01-C1&fulljson={"amp":13}
// by
//    1. add new charging profile
//    2. issue 'clear all profiles' message to charge point
//    3. issue 'set charging profile' message to charge point

@Controller
@RequestMapping(value = "/currentlimit")
public class CurrentLimitController {

    @Autowired private ChargePointRepository chargePointRepository;
    @Autowired private ChargingProfileRepository chargingProfileRepository;

    @RequestMapping(value = "/post", method = RequestMethod.GET)
    @ResponseBody
    public String getInput(@RequestParam(value="node") String node,
                           @RequestParam(value="fulljson", required=false) String json) {

        Integer chargingProfilePk = null;
        String rv = "node " + node + "<br>";
        if (json != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);
                if (jsonNode.has("amp")) {
                    rv += "limit " + jsonNode.get("amp") + " amps <br>";
                    ChargingProfileForm f = defaultTxProfileForm(jsonNode.get("amp").asDouble(), 1);
                    chargingProfilePk = addProfile(f);
                    if (chargingProfilePk == null) {
                        rv += "failure adding charging profile <br>";
                        return rv;
                    }
                    else {
                        rv += "added charging profile " + chargingProfilePk + "<br>";
                    }
                }
            }
            catch (IOException e) {
                return e.getMessage();
            }
            String splitNode[] = node.split("-C(?=[0-9]{1,2}$)"); // split off final -Cnn connector number nn
            String chargeBoxId = splitNode[0];
            Map<String, Integer> idPkPair = chargePointRepository.getChargeBoxIdPkPair(List.of(chargeBoxId));
            Integer chargeBoxPk = idPkPair.get(chargeBoxId);
            if (chargeBoxPk == null) {
                rv += "charge point " + chargeBoxId + " is not registered <br>";
                return rv;
            }
            else {
               rv += "charge point " + chargeBoxId + " pk is " + chargeBoxPk + "<br>";
            }
            if (splitNode.length != 2) {
               rv += "node ID lacks connector suffix -Cnn<br>";
               return rv;
            }
            Integer connectorId = Integer.parseInt(splitNode[1]);
            if (clearConnectorChargingProfiles(chargeBoxId, connectorId)) {
                rv += "charging profile clear <br>";
            }
            else {
                rv += "failed to clear charging profile<br>";
            }
            if (setConnectorChargingProfile(chargeBoxId, connectorId, chargingProfilePk)) {
                rv += "charging profile set <br>";
            }
            else {
                rv += "failed to set charging profile<br>";
            }
        }
        return rv;
    }

    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    private Boolean setConnectorChargingProfile(String chargeBoxId, Integer connectorId, Integer chargingProfilePk) {
        Boolean success = false;
        SetChargingProfileParams params = new SetChargingProfileParams();
        params.setChargingProfilePk(chargingProfilePk);
        params.setConnectorId(connectorId);
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppTransport.JSON, chargeBoxId)));
        client16.setChargingProfile(params);
        // TODO get success/failure status; temporary hack: delay for reply and assume ok
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(InterruptedException ex) {
        } // never mind
        success = true;
        return success;
    }

    private Boolean clearConnectorChargingProfiles(String chargeBoxId, Integer connectorId) {
        Boolean success = false;
        ClearChargingProfileParams params = new ClearChargingProfileParams();
        params.setFilterType(ClearChargingProfileFilterType.OtherParameters);
        params.setChargingProfilePk(null);
        params.setConnectorId(connectorId);
        params.setChargingProfilePurpose(null);
        params.setStackLevel(null);
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppTransport.JSON, chargeBoxId)));
        client16.clearChargingProfile(params);
        // TODO get success/failure status; temporary hack: delay for reply and assume ok
        try {
            TimeUnit.SECONDS.sleep(2);
        }
        catch(InterruptedException ex) {
        } // never mind
        success = true;
        return success;
    }

    private Integer addProfile(ChargingProfileForm form) {
        //TODO: apply validation & return null if fails
        return chargingProfileRepository.add(form); // return profilePk
    }

    private ChargingProfileForm defaultTxProfileForm(Double limitInAmperes, Integer numberOfPhases) {
        ChargingProfileForm form = new ChargingProfileForm();
        form.setDescription("Created by CurrentLimitController");
        form.setNote("");
        form.setStackLevel(0);
        form.setChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE);
        form.setChargingProfileKind(ChargingProfileKindType.RELATIVE);
        form.setRecurrencyKind(null);
        form.setValidFrom(null);
        form.setValidTo(null);
        form.setDurationInSeconds(null);
        form.setStartSchedule(null);
        form.setChargingRateUnit(ChargingRateUnitType.A);
        form.setMinChargingRate(null);

        Map<String, ChargingProfileForm.SchedulePeriod> periodMap = new LinkedHashMap<>();
        ChargingProfileForm.SchedulePeriod p = new ChargingProfileForm.SchedulePeriod();
        p.setStartPeriodInSeconds(0);
        p.setPowerLimitInAmperes(BigDecimal.valueOf(limitInAmperes));
        p.setNumberPhases(numberOfPhases);
        periodMap.put(UUID.randomUUID().toString(), p);

        form.setSchedulePeriodMap(periodMap);

        return form;
    }
}


