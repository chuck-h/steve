package de.rwth.idsg.steve.utils;

import ocpp.cp._2015._10.ChargingSchedulePeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 19.04.2018
 */

// Charging Schedule Period Utils
public final class CspUtils {
    private CspUtils() { }

    public static List<ChargingSchedulePeriod> setCsp(Integer[] startPeriod, BigDecimal[] limit, List<Integer> numberPhases) {
        List<ChargingSchedulePeriod> cspList = new ArrayList<>();

        for (int i = 0; i < startPeriod.length; i++) {
            cspList.add(new ChargingSchedulePeriod()
                    .withStartPeriod(startPeriod[i])
                    .withLimit(limit[i].setScale(1, RoundingMode.HALF_UP))
                    .withNumberPhases(numberPhases.get(i)));
        }
        return cspList;
    }

    // startPeriods cannot be greater than the previous ones,
    // and can only have a maximum of 86399 (= 23:59:59)
    public static boolean checkStartPeriod(Integer[] startPeriod) {
        Integer last = -1;
        for (Integer current : startPeriod) {
            if (startPeriod != null) {
                if (last >= current) {
                    startPeriod = null;
                }
            }
            if (current > 86399) {
                startPeriod = null;
            }
            last = current;
        }
        // true = passes, false = give error message that values are incorrect
        return startPeriod != null;
    }

    // Check if numberPhases is set and fill the values if needed.
    public static List<Integer> setNumberPhases(Integer[] startPeriod, BigDecimal[] limit, List<Integer> numberPhases) {
        if ((numberPhases == null || numberPhases.size() == 0) && (startPeriod.length == limit.length)) {
            for (int i = 0; i < startPeriod.length; i++) {
                numberPhases.add(3);
            }
        } else if ((numberPhases.size() == startPeriod.length) && (startPeriod.length == limit.length)) {
            numberPhases = numberPhases;
        } else if ((numberPhases.size() < startPeriod.length) && (startPeriod.length == limit.length)) {
            // If numberPhases has less values than startPeriod, fill numberPhases with the default value,
            // "3" as this is the default value according to OCPP 1.6 Specifications
            for (int i = numberPhases.size(); i < startPeriod.length; i++) {
                numberPhases.add(3);
            }
        } else {
            // Dirty work around to give an error message that csp values do not have an equivalent amount of values
            // When comparing the size of startPeriod and numberPhases.
            numberPhases.add(3);
        }
        return numberPhases;
    }
}
