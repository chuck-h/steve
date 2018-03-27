package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.service.BackgroundService;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfile;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 22.03.2018
 */
@Setter
@Getter
public class SetChargingProfileParams extends MultipleChargePointSelect {

    private Integer connectorId;

    private ChargingProfile csChargingProfiles;
    private Integer chargingProfileId;
    private Integer transactionId;
    private Integer stackLevel;

    @NotNull(message = "Charging Profile Purpose cannot be TxProfile outside transaction and can only be set at 1 Charge Point")
    private ChargingProfilePurposeTypeEnum chargingProfilePurpose;

    private ChargingProfileKindTypeEnum chargingProfileKind;
    private RecurrencyKindTypeEnum recurrencyKind;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    private ChargingSchedule chargingSchedule;
    private Integer duration;
    private LocalDateTime startSchedule;
    private ChargingRateUnitTypeEnum chargingRateUnit;
    private BigDecimal minChargingRate;

    private ChargingSchedulePeriod chargingSchedulePeriod;
    private Integer startPeriod;
    private BigDecimal limit;
    private Integer numberPhases;

    public void setConnectorId(Integer connectorId) {
        if (connectorId == null) {
            this.connectorId = 0;
        } else {
            this.connectorId = connectorId;
        }
    }

    public void setChargingProfilePurpose(ChargingProfilePurposeTypeEnum chargingProfilePurpose)
    {
        if (chargingProfilePurpose.value() == ChargingProfilePurposeType.TX_PROFILE.value() && transactionId == null)
            this.chargingProfilePurpose = null;
        else
            this.chargingProfilePurpose = chargingProfilePurpose;
    }
}
