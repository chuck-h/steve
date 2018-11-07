package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import ocpp.cp._2015._10.ChargingProfilePurposeType;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
public class ClearChargingProfileTask extends Ocpp16AndAboveTask<ClearChargingProfileParams, String> {

    public ClearChargingProfileTask(OcppVersion ocppVersion, ClearChargingProfileParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2015._10.ClearChargingProfileRequest getOcpp16Request() {
        if (params.isSetChargingProfilePurpose()) {
            return new ocpp.cp._2015._10.ClearChargingProfileRequest()
                    .withStackLevel(params.getStackLevel())
                    .withId(params.getId())
                    .withConnectorId(params.getConnectorId())
                    .withChargingProfilePurpose(ChargingProfilePurposeType.fromValue(params.getChargingProfilePurpose().value()));
        } else {
            return new ocpp.cp._2015._10.ClearChargingProfileRequest()
                    .withStackLevel(params.getStackLevel())
                    .withId(params.getId())
                    .withConnectorId(params.getConnectorId());
        }
    }


    @Override
    public AsyncHandler<ocpp.cp._2015._10.ClearChargingProfileResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
