package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import ocpp.cp._2015._10.*;

import javax.xml.ws.AsyncHandler;
import java.math.RoundingMode;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;
import static de.rwth.idsg.steve.utils.CspUtils.setCsp;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
public class SetChargingProfileTask extends Ocpp16AndAboveTask<SetChargingProfileParams, String> {

    public SetChargingProfileTask(OcppVersion ocppVersion, SetChargingProfileParams params) {
        super(ocppVersion, params);
    }

    @Override
    public OcppCallback<String> defaultCallback() {
        return new StringOcppCallback();
    }


    @Deprecated
    @Override
    public ocpp.cp._2015._10.SetChargingProfileRequest  getOcpp16Request() {
        return new ocpp.cp._2015._10.SetChargingProfileRequest()
                .withConnectorId(params.getConnectorId())
                .withCsChargingProfiles(new ChargingProfile()
                        .withChargingProfileId(params.getChargingProfileId())
                        .withTransactionId(params.getTransactionId())
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
                                .withChargingSchedulePeriod(setCsp(params.getStartPeriod(), params.getLimit(), params.getNumberPhases()))
                        .withMinChargingRate(params.getMinChargingRate() != null ? params.getMinChargingRate().setScale(1, RoundingMode.HALF_UP) : null)));
    }


    @Override
    public AsyncHandler<ocpp.cp._2015._10.SetChargingProfileResponse> getOcpp16Handler(String chargeBoxId) {
        return res -> {
            try {
                success(chargeBoxId, res.get().getStatus().value());
            } catch (Exception e) {
                failed(chargeBoxId, e);
            }
        };
    }
}
