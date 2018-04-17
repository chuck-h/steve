package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.web.validation.IdTag;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl></david@rerimassie.nl>
 * @since 01.01.2015
 */
@Setter
@Getter
public class RemoteStartTransactionParams extends SingleChargePointSelect {

    @Min(value = 0, message = "Connector ID must be at least {value}")
    private Integer connectorId;

    @NotBlank(message = "User ID Tag is required")
    @IdTag
    @Setter private String idTag;

    @NotNull(message = "Charging Schedule Period parameters need an equivalent amount of values and </br>" +
            "Start Periods cannot be lower or equal to the one before, max startPeriod value = 86399 (23:59:59) </br>" +
            "If setting a Charging Profile in a Remote Transaction make sure to fill in every required field </br>" +
            "- Charging Profile ID</br>" +
            "- Stack Level</br>" +
            "- Charging Profile Kind</br>" +
            "- Charging Rate Unit</br>" +
            "- Start Period </br>" +
            "- Limit")
    public Boolean useChargingProfile;

    private Integer chargingProfileId;

    private Integer stackLevel;
    private ChargingProfilePurposeTypeEnum chargingProfilePurpose = ChargingProfilePurposeTypeEnum.TxProfile;;
    private ChargingProfileKindTypeEnum chargingProfileKind;
    private RecurrencyKindTypeEnum recurrencyKind;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    private Integer duration;
    private LocalDateTime startSchedule;
    private ChargingRateUnitTypeEnum chargingRateUnit;
    private BigDecimal minChargingRate;

    private Integer[] startPeriod;
    private BigDecimal[] limit;
    private List<Integer> numberPhases;

    /**
     * Not for a specific connector, when frontend sends the value 0.
     * This corresponds to not to include the connector id parameter in OCPP request.
     */
    public void setConnectorId(Integer connectorId) {
        if (connectorId == 0) {
            this.connectorId = null;
        } else {
            this.connectorId = connectorId;
        }
    }

    public void setUseChargingProfile(Boolean useChargingProfile) {
        if (!useChargingProfile) {
            this.useChargingProfile = useChargingProfile;
        } else {
            if (chargingProfileId == null || stackLevel == null || startPeriod == null || limit == null) {
                this.useChargingProfile = null;
            } else {
                startPeriod[0] = 0;
                Integer last = -1;
                for (Integer current : startPeriod) {
                    if (startPeriod != null) {
                        if (last >= current) {
                            this.useChargingProfile = null;
                        }
                    }
                    if (current > 86399) {
                        this.useChargingProfile = null;
                    }
                    last = current;
                }
                if (startPeriod != null) {
                    if ((numberPhases == null || numberPhases.size() == 0) && (startPeriod.length == limit.length)) {
                        for (int i = 0; i < startPeriod.length; i++) {
                            numberPhases.add(3);
                        }
                        this.useChargingProfile = useChargingProfile;
                    } else if ((numberPhases != null || numberPhases.size() != 0) && (startPeriod.length == limit.length) && (startPeriod.length == numberPhases.size())) {
                        this.useChargingProfile = useChargingProfile;
                    } else {
                        this.useChargingProfile = null;
                    }
                } else {
                    this.useChargingProfile = null;
                }
            }
        }
    }
}
