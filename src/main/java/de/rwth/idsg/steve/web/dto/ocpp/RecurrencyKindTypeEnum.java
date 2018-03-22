package de.rwth.idsg.steve.web.dto.ocpp;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 22.03.2018
 */
public enum RecurrencyKindTypeEnum {
    Daily("Daily"),
    Weekly("Weekly");

    private final String value;

    RecurrencyKindTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static RecurrencyKindTypeEnum fromValue(String v) {
        for (RecurrencyKindTypeEnum c : RecurrencyKindTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}