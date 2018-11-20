package de.rwth.idsg.steve.service;

import ocpp.cs._2015._10.MeterValue;
import java.util.List;

/**
 * @author Chuck Harrison <cfharr@gmail.com>
 * derived from SteVe project 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
public interface MeasurementExportService {
    void ocppMeasurement(String chargeBoxId, List<MeterValue> values, int connectorId);
}
