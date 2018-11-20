package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveConfiguration;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;

import static java.lang.String.format;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Chuck Harrison <cfharr@gmail.com>
 * derived from SteVe project 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class MeasurementExportServiceImpl implements MeasurementExportService{
    @Override
    public void ocppMeasurement(String chargeBoxId, List<MeterValue> values, int connectorId) {
        // if emonpub is enabled: send HTTPS POST request to emon host
        SteveConfiguration.Emon emon = SteveConfiguration.CONFIG.getEmon();
        if (!emon.isEnabled()) {
            return;
        }
        // report only the first 'Current.Import' reading in list, assuming amperes
        // TODO generalize
        List<SampledValue> currents = values.get(0).getSampledValue().stream()
                                .filter(p -> p.getMeasurand().value() == "Current.Import")
                                .collect(Collectors.toList());
        Float v = Float.parseFloat(currents.get(0).getValue());
        String emonQuery = format("%s/input/post?node=%s&fulljson={\"amp\":%.2f,\"connectorid\":%d}&apikey=%s",
                                  emon.getUri(), chargeBoxId, v, connectorId, emon.getApikey());
        try {
            URL emonUrl = new URL(emonQuery);
            HttpsURLConnection conn = (HttpsURLConnection)emonUrl.openConnection();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                log.info("emonpub response: " + inputLine);
            }
            br.close();
        }
        catch (Exception e) {
            log.info("Exception occurred " + e.getMessage());
        }


    }
}
