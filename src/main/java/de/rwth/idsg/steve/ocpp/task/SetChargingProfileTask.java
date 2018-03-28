package de.rwth.idsg.steve.ocpp.task;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import ocpp.cp._2015._10.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import javax.xml.ws.AsyncHandler;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static de.rwth.idsg.steve.utils.DateTimeUtils.toDateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
public class SetChargingProfileTask extends CommunicationTask<SetChargingProfileParams, String> {

    public SetChargingProfileTask(OcppVersion ocppVersion, SetChargingProfileParams params) {
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

    @Deprecated
    @Override
    public ocpp.cp._2015._10.SetChargingProfileRequest  getOcpp16Request() {
        /*ChargingSchedulePeriod[] cspList = new ChargingSchedulePeriod[] {
                    new ChargingSchedulePeriod()
                            .withStartPeriod(0) // = 00:00
                            .withLimit(new BigDecimal(11000).setScale(1, RoundingMode.HALF_UP))
                            .withNumberPhases(3),
                    new ChargingSchedulePeriod()
                            .withStartPeriod(28800) // = 08:00
                            .withLimit(new BigDecimal(6000).setScale(1, RoundingMode.HALF_UP))
                            .withNumberPhases(3),
                    new ChargingSchedulePeriod()
                            .withStartPeriod(72000) // = 20:00
                            .withLimit(new BigDecimal(11000).setScale(1, RoundingMode.HALF_UP))
                            .withNumberPhases(3)
                };

        return new ocpp.cp._2015._10.SetChargingProfileRequest()
                .withConnectorId(2)
                .withCsChargingProfiles(new ChargingProfile()
                        .withChargingProfileId(100)
                        .withStackLevel(0)
                        .withChargingProfilePurpose(ChargingProfilePurposeType.TX_DEFAULT_PROFILE)
                        .withChargingProfileKind(ChargingProfileKindType.RECURRING)
                        .withRecurrencyKind(RecurrencyKindType.DAILY)
                        .withChargingSchedule(new ChargingSchedule()
                                .withDuration(86400) //24 hours
                                .withStartSchedule(new DateTime(toDateTime(LocalDateTime.now())))
                                .withChargingRateUnit(ChargingRateUnitType.W)
                                .withChargingSchedulePeriod(cspList)));*/

        return new ocpp.cp._2015._10.SetChargingProfileRequest()
                .withConnectorId(params.getConnectorId())
                .withCsChargingProfiles(new ChargingProfile()
                        .withChargingProfileId(params.getChargingProfileId())
                        .withTransactionId(params.getTransactionId())
                        .withStackLevel(params.getStackLevel())
                        .withChargingProfilePurpose(ChargingProfilePurposeType.valueOf(params.getChargingProfilePurpose().value()))
                        .withChargingProfileKind(ChargingProfileKindType.fromValue(params.getChargingProfileKind().value()))
                        .withRecurrencyKind(RecurrencyKindType.fromValue(params.getRecurrencyKind().value()))
                        .withValidFrom(toDateTime(params.getValidFrom()))
                        .withValidTo(toDateTime(params.getValidTo()))
                        .withChargingSchedule(new ChargingSchedule()
                                .withDuration(params.getDuration())
                                .withStartSchedule(toDateTime(params.getStartSchedule()))
                                .withChargingRateUnit(ChargingRateUnitType.fromValue(params.getChargingRateUnit().value()))
                                .withChargingSchedulePeriod(params.getChargingSchedulePeriod())
                        .withMinChargingRate(params.getMinChargingRate())));
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
