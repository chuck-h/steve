package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 22.03.2018
 */
public enum ChargingProfilePurposeTypeEnum {
    ChargePointMaxProfile("ChargePointMaxProfile"),
    TxDefaultProfile("TxDefaultProfile"),
    TxProfile("TxProfile");

    private final String value;

    ChargingProfilePurposeTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ChargingProfilePurposeTypeEnum fromValue(String v) {
        for (ChargingProfilePurposeTypeEnum c : ChargingProfilePurposeTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
