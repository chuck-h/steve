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
public class ClearChargingProfileTask extends CommunicationTask<ClearChargingProfileParams, String> {

    public ClearChargingProfileTask(OcppVersion ocppVersion, ClearChargingProfileParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp12Request() {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends RequestType> T getOcpp15Request() {
        throw new RuntimeException("Not supported");
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

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp12Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
    }

    @Deprecated
    @Override
    public <T extends ResponseType> AsyncHandler<T> getOcpp15Handler(String chargeBoxId) {
        throw new RuntimeException("Not supported");
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
