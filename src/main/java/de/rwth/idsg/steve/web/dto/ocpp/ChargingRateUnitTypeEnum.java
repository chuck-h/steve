package de.rwth.idsg.steve.web.dto.ocpp;

public enum ChargingRateUnitTypeEnum {
    W("W"),
    A("A");

    private final String value;

    ChargingRateUnitTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ChargingRateUnitTypeEnum fromValue(String v) {
        for (ChargingRateUnitTypeEnum c : ChargingRateUnitTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
