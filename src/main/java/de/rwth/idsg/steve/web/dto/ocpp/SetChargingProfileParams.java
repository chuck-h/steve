package de.rwth.idsg.steve.web.dto.ocpp;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.Getter;
import lombok.Setter;
import ocpp.cp._2015._10.ChargingProfile;
import ocpp.cp._2015._10.ChargingProfilePurposeType;
import ocpp.cp._2015._10.ChargingSchedule;
import ocpp.cp._2015._10.ChargingSchedulePeriod;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Min;
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
    @NotNull(message = "Charging Profile ID is required.")
    private Integer chargingProfileId;
    @Min(value = 1, message = "Transaction ID cannot be set without TxProfile or on multiple Charge Points")
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

    private ChargingSchedulePeriod chargingSchedulePeriod;
    @NotNull(message = "Start Period required.")
    private Integer startPeriod;
    @NotNull(message = "Limit required.")
    private BigDecimal limit;
    private Integer numberPhases;

    public void setConnectorId(Integer connectorId) {
        if (connectorId == null) {
            this.connectorId = 0;
        } else {
            this.connectorId = connectorId;
        }
    }
    // ---------------------------------------------------------------------------------------------------
    // Don't go beyond this line as it is dangerous, I'm not a cop, just a comment, but advise you not to.
    // ---------------------------------------------------------------------------------------------------
    // Code Graveyard...

    public void setTransactionId(Integer transactionId) {
        if (getChargePointSelectList().toArray().length > 1 && chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            this.chargingProfilePurpose = null;
        } else if (getChargePointSelectList().toArray().length == 1) {
            if (transactionId == null && getChargingProfilePurpose().value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
                this.chargingProfilePurpose = null;
            } else if (transactionId != null && !getChargingProfilePurpose().value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
                this.transactionId = -1;
            } else {
                this.transactionId = transactionId;
            }
            return;
        } else {
            this.transactionId = transactionId != null ? -1 : transactionId;
        }
    }

    /*public Integer setTransactionId(Integer transactionId) {
        if (getChargePointSelectList().toArray().length > 1 && transactionId != null) {
            return this.transactionId = -1;
        } else if (transactionId != null && !chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            return this.transactionId = null;
        } else {
            return this.transactionId = transactionId;
        }
    }

    public ChargingProfilePurposeTypeEnum setChargingProfilePurpose (ChargingProfilePurposeTypeEnum chargingProfilePurpose) {
        if (getChargePointSelectList().toArray().length > 1 && chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
            return this.chargingProfilePurpose = null;
        } else if (getChargePointSelectList().toArray().length == 1) {
            if (transactionId == null && chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
                return this.chargingProfilePurpose = null;
            } else {
                return this.chargingProfilePurpose = chargingProfilePurpose;
            }
        } else {
            return this.chargingProfilePurpose = chargingProfilePurpose;
        }
    }*/

    /*public Integer setTransactionId (Integer transactionId) {
        return this.transactionId = transactionId;
    }

    /*public void setChargingProfilePurpose(ChargingProfilePurposeTypeEnum chargingProfilePurpose) {
        System.out.println(chargingProfilePurpose);
        this.chargingProfilePurpose = chargingProfilePurpose;
    }*/

    //public void setTransactionId (Integer transactionId) {
        /*if (transactionId != null && getChargePointSelectList().toArray().length > 1 ||
                !chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && transactionId != null) {
            this.transactionId = null;
        } else {
            this.transactionId = transactionId;
        }*/
    //}
    /*public ChargingProfilePurposeTypeEnum setTransactionId(Integer transactionId) {
        System.out.println("CheckTestHelpMe list: " + getChargePointSelectList().toArray().length);
        System.out.println("CheckTestHelpMe chargingProfilePurpose: " + chargingProfilePurpose.value());
        System.out.println("CheckTestHelpMe transactionId: " + transactionId);
        System.out.println(" ");
        if (getChargePointSelectList().toArray().length > 1) {
            if (transactionId != null || chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value())) {
                this.transactionId = transactionId;
                return this.chargingProfilePurpose = null;
            }
        } else if (getChargePointSelectList().toArray().length < 2) {
            if (chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && transactionId == null) {
                this.transactionId = transactionId;
                return this.chargingProfilePurpose = null;
            } else if (!chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && transactionId != null) {
                this.transactionId = transactionId;
                return this.chargingProfilePurpose = null;
            }
        }
        this.transactionId = transactionId;
        return chargingProfilePurpose;
    }*/

    /*public void setChargingProfilePurpose(ChargingProfilePurposeTypeEnum chargingProfilePurpose) {
        System.out.println(transactionId);
        if  (chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && getChargePointSelectList().toArray().length > 1 ||
                transactionId != null && getChargePointSelectList().toArray().length > 1 ||
                chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && transactionId == null ||
                !chargingProfilePurpose.value().equals(ChargingProfilePurposeType.TX_PROFILE.value()) && transactionId != null) {
            this.chargingProfilePurpose = null;
        } else {
            this.chargingProfilePurpose = chargingProfilePurpose;
        }
    }*/


    // ---------------------------------------------------------------------------------------------------
}