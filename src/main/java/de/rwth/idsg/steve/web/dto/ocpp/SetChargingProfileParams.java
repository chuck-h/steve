package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfile;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 22.03.2018
 */
@Setter
@Getter
public class SetChargingProfileParams extends MultipleChargePointSelect {

    private Integer connectorId;

    private ChargingProfile csChargingProfiles;
    @NotNull(message = "Charging Profile ID is required.")
    private Integer chargingProfileId;
    @Min(value = 1, message = "Transaction ID cannot be set without TxProfile or on multiple Charge Points and </br> " +
                "Charging Profile Purpose cannot be 'TxProfile' outside transaction and can only be set at 1 Charge Point")
    private Integer transactionId;
    @NotNull(message = "Stack Level is required.")
    private Integer stackLevel;

    @NotNull(message = "Charging Profile Purpose cannot be 'TxProfile' outside transaction and can only be set at 1 Charge Point")
    private ChargingProfilePurposeTypeEnum chargingProfilePurpose;

    @NotNull(message = "Charging Profile Kind is required.")
    private ChargingProfileKindTypeEnum chargingProfileKind;
    private RecurrencyKindTypeEnum recurrencyKind;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    private ChargingSchedule chargingSchedule;
    private Integer duration;
    private LocalDateTime startSchedule;
    @NotNull(message = "Charging Rate Unit is required.")
    private ChargingRateUnitTypeEnum chargingRateUnit;
    private BigDecimal minChargingRate;

    private List<ChargingSchedulePeriod> chargingSchedulePeriod;

    private Integer[] startPeriod;
    private BigDecimal[] limit;
    private Integer[] numberPhases;

    public void setConnectorId(Integer connectorId) {
        if (connectorId == null) {
            this.connectorId = 0;
        } else {
            this.connectorId = connectorId;
        }
    }

    public void setStartPeriod(Integer[] startPeriod) {
        for (Integer i : startPeriod) {
            System.out.println("startPeriod: " + i);
        }
        System.out.println("startPeriod Size: " + startPeriod.length);
    }

    public void setLimit(BigDecimal[] limit) {
        for (BigDecimal i : limit) {
            System.out.println("limit: " + i);
        }
        System.out.println("limit Size: " + limit.length);
    }

    public void setNumberPhases(Integer[] numberPhases){
        for (Integer i : numberPhases) {
            System.out.println("numberPhases: " + i);
        }
        System.out.println("numberPhases Size: " + numberPhases.length);
    }

    // -----------------------------------------------------------------------------------------------------
    // Selecting 1 Charge Point and then clicking "Select All" with TxProfile will show the following error
    // "Property 'transactionId' threw exception; nested exception is java.lang.NullPointerException"
    // This isn't a problem because this is preventing users to set TxProfile/TransactionID on multiple CP's
    // It happens somewhere in this area.
    // -----------------------------------------------------------------------------------------------------
    public void setTransactionId(Integer transactionId) {
        if (getChargePointSelectList().toArray().length > 1 && transactionId != null) {
            this.transactionId = -1;
        } else if (transactionId != null && !chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            this.transactionId = -1;
        } else if (transactionId == null && chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            this.chargingProfilePurpose = null;
        } else {
            this.transactionId = transactionId;
        }
    }

    public void setChargingProfilePurpose(ChargingProfilePurposeTypeEnum chargingProfilePurpose) {
        if (getChargePointSelectList().toArray().length > 1 && chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            this.chargingProfilePurpose = null;
        } else {
            this.chargingProfilePurpose = chargingProfilePurpose;
        }
    }
    // -----------------------------------------------------------------------------------------------------
}