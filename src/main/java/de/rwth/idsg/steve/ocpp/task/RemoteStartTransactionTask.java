package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import ocpp.cp._2015._10.*;

import javax.xml.ws.AsyncHandler;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 09.03.2018
 */
public class RemoteStartTransactionTask extends CommunicationTask<RemoteStartTransactionParams, String> {

    public RemoteStartTransactionTask(OcppVersion ocppVersion, RemoteStartTransactionParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }

    @Override
    public ocpp.cp._2010._08.RemoteStartTransactionRequest getOcpp12Request() {
        return new ocpp.cp._2010._08.RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2012._06.RemoteStartTransactionRequest getOcpp15Request() {
        return new ocpp.cp._2012._06.RemoteStartTransactionRequest()
                .withIdTag(params.getIdTag())
                .withConnectorId(params.getConnectorId());
    }

    @Override
    public ocpp.cp._2015._10.RemoteStartTransactionRequest getOcpp16Request() {
        if (!params.useChargingProfile) {
            return new ocpp.cp._2015._10.RemoteStartTransactionRequest()
                    .withIdTag(params.getIdTag())
                    .withConnectorId(params.getConnectorId());
        } else {
            List<ChargingSchedulePeriod> cspList = new ArrayList<>();

            for (int i = 0; i < params.getStartPeriod().length; i++) {
                cspList.add(new ChargingSchedulePeriod()
                        .withStartPeriod(params.getStartPeriod()[i])
                        .withLimit(params.getLimit()[i].setScale(1, RoundingMode.HALF_UP))
                        .withNumberPhases(params.getNumberPhases().get(i)));
            }

            return new ocpp.cp._2015._10.RemoteStartTransactionRequest()
                    .withIdTag(params.getIdTag())
                    .withConnectorId(params.getConnectorId())
                    .withChargingProfile(new ChargingProfile()
                            .withChargingProfileId(params.getChargingProfileId())
                            .withStackLevel(params.getStackLevel())
                            .withChargingProfilePurpose(ChargingProfilePurposeType.fromValue(params.getChargingProfilePurpose().value()))
                            .withChargingProfileKind(ChargingProfileKindType.fromValue(params.getChargingProfileKind().value()))
                            .withRecurrencyKind(params.getRecurrencyKind() != null ? RecurrencyKindType.fromValue(params.getRecurrencyKind().value()) : null)
                            .withValidFrom(toDateTime(params.getValidFrom()))
                            .withValidTo(toDateTime(params.getValidTo()))
                            .withChargingSchedule(new ChargingSchedule()
                                    .withDuration(params.getDuration())
                                    .withStartSchedule(toDateTime(params.getStartSchedule()))
                                    .withChargingRateUnit(ChargingRateUnitType.fromValue(params.getChargingRateUnit().value()))
                                    .withChargingSchedulePeriod(cspList)
                            .withMinChargingRate(params.getMinChargingRate() != null ? params.getMinChargingRate().setScale(1, RoundingMode.HALF_UP) : null)));
        }
    }

    @Override
    public AsyncHandler<ocpp.cp._2010._08.RemoteStartTransactionResponse> getOcpp12Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }

    @Override
    public AsyncHandler<ocpp.cp._2012._06.RemoteStartTransactionResponse> getOcpp15Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }


    @Override
    public AsyncHandler<ocpp.cp._2015._10.RemoteStartTransactionResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
