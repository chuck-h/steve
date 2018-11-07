package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 20.03.2018
 */
@Setter
@Getter
public class GetCompositeScheduleParams extends MultipleChargePointSelect {

    private Integer connectorId;

    @NotNull(message = "Duration is required.")
    private Integer duration;

    private ChargingRateUnitTypeEnum chargingRateUnit;

    public void setConnectorId(Integer connectorId) {
        if (connectorId == null) {
            this.connectorId = 0;
        } else {
            this.connectorId = connectorId;
        }
    }

    public boolean isSetChargingRateUnit() {
        return chargingRateUnit != null && !chargingRateUnit.equals("");
    }
}
