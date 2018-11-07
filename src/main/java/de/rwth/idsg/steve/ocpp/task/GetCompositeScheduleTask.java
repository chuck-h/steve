package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2015._10.ChargingRateUnitType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import org.joda.time.DateTime;

import javax.xml.ws.AsyncHandler;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
public class GetCompositeScheduleTask extends Ocpp16AndAboveTask<GetCompositeScheduleParams, GetCompositeScheduleTask.ResponseWrapper> {

    public GetCompositeScheduleTask(OcppVersion ocppVersion, GetCompositeScheduleParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new DefaultOcppCallback<GetCompositeScheduleTask.ResponseWrapper>() {
            @Override
            public void success(String chargeBoxId, GetCompositeScheduleTask.ResponseWrapper response) {
                String status = response.getStatus();
                Integer connectorId = response.getConnectorId();
                DateTime scheduleStart = response.getScheduleStart();
                ChargingSchedule chargingSchedule = response.getChargingSchedule();

                StringBuilder builder = new StringBuilder(status);
                builder.append("</br>");
                if (connectorId != null) {
                    builder.append("</br>Connector ID: ").append(connectorId);
                } if (scheduleStart != null) {
                    builder.append("</br>Schedule Start: ").append(scheduleStart);
                } if (chargingSchedule != null) {
                    builder.append("</br></br><b>Charging Schedule</b>").append(csPrint(chargingSchedule));
                }
                addNewResponse(chargeBoxId, builder.toString());
            }
        };
    }
    private String csPrint(ChargingSchedule cs) {
        String pcs = "</br>";
        if (cs.getDuration() != null)
            pcs += "Duration: " + cs.getDuration() + "</br>";
        if (cs.getStartSchedule() != null)
            pcs += "Start Schedule: " + cs.getStartSchedule() + "</br>";
        if (cs.getChargingRateUnit() != null)
            pcs += "Charging Rate Unit: " + cs.getChargingRateUnit() + "</br>";
        if (cs.getMinChargingRate() != null)
            pcs += "Min Charging Rate: " + cs.getMinChargingRate() + "</br>";
        if (cs.getChargingSchedulePeriod() != null) {
            pcs += "</br><b>Charging Schedule Periods</b></br>";
            List<ChargingSchedulePeriod> list = cs.getChargingSchedulePeriod();
            int i = 1;
            for (ChargingSchedulePeriod element : list) {
                /*prettycs += element.getStartPeriod() + "</br>" + element.getLimit()
                        + "</br>" + element.getNumberPhases() + "</br>";*/
                pcs += String.valueOf(i+ ": ") + element + "</br>";
                i++;
            }
        }
        return pcs;
    }

    @Override
    public ocpp.cp._2015._10.GetCompositeScheduleRequest getOcpp16Request() {
        if (params.isSetChargingRateUnit()) {
            return new ocpp.cp._2015._10.GetCompositeScheduleRequest()
                    .withConnectorId(params.getConnectorId())
                    .withDuration(params.getDuration())
                    .withChargingRateUnit(ChargingRateUnitType.fromValue(params.getChargingRateUnit().value()));
        } else {
            return new ocpp.cp._2015._10.GetCompositeScheduleRequest()
                    .withConnectorId(params.getConnectorId())
                    .withDuration(params.getDuration());
        }
    }


    @Override
    public AsyncHandler<ocpp.cp._2015._10.GetCompositeScheduleResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ocpp.cp._2015._10.GetCompositeScheduleResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(),
                                                        response.getConnectorId(),
                                                        response.getScheduleStart(),
                                                        response.getChargingSchedule()));
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Getter
    @RequiredArgsConstructor
    public static class ResponseWrapper {
        private final String status;
        private final Integer connectorId;
        private final DateTime scheduleStart;
        private final ChargingSchedule chargingSchedule;
    }
}
