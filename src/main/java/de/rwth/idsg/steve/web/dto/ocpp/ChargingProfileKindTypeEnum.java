package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 22.03.2018
 */
public enum ChargingProfileKindTypeEnum {
    Absolute("Absolute"),
    Recurring("Recurring"),
    Relative("Relative");

    private final String value;

    ChargingProfileKindTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ChargingProfileKindTypeEnum fromValue(String v) {
        for (ChargingProfileKindTypeEnum c : ChargingProfileKindTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}