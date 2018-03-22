package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ocpp.cp._2015._10.ChargingRateUnitType;
import org.joda.time.DateTime;

import javax.xml.ws.AsyncHandler;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
public class GetCompositeScheduleTask extends CommunicationTask<GetCompositeScheduleParams, GetCompositeScheduleTask.ResponseWrapper> {

    public GetCompositeScheduleTask(OcppVersion ocppVersion, GetCompositeScheduleParams params) {
        super(ocppVersion, params);
    }
    //TODO handle ChargingSchedule in response
    @Override
    public OcppCallback<ResponseWrapper> defaultCallback() {
        return new DefaultOcppCallback<GetCompositeScheduleTask.ResponseWrapper>() {
            @Override
            public void success(String chargeBoxId, GetCompositeScheduleTask.ResponseWrapper response) {
                String status = response.getStatus();
                Integer connectorId = response.getConnectorId();
                DateTime scheduleStart = response.getScheduleStart();

                StringBuilder builder = new StringBuilder(status);
                if (connectorId != null) {
                    builder.append(" / Connector ID: ").append(connectorId);
                }
                if (scheduleStart != null) {
                    builder.append(" / Schedule Start: ").append(scheduleStart);
                }
                addNewResponse(chargeBoxId, builder.toString());
            }
        };
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
    public AsyncHandler<ocpp.cp._2015._10.GetCompositeScheduleResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                ocpp.cp._2015._10.GetCompositeScheduleResponse response = res.get();
                success(chargeBoxId, new ResponseWrapper(response.getStatus().value(),
                                                        response.getConnectorId(),
                                                        response.getScheduleStart()));
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
    }
}
