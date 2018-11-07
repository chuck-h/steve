package de.rwth.idsg.steve.web.dto.ocpp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 20.03.2018
 */
@Setter
@Getter
public class ClearChargingProfileParams extends MultipleChargePointSelect {

    private Integer id;

    private Integer connectorId;

    private ChargingProfilePurposeTypeEnum chargingProfilePurpose;

    private Integer stackLevel;

    public boolean isSetChargingProfilePurpose() {
        return chargingProfilePurpose != null && !chargingProfilePurpose.equals("");
    }
}
