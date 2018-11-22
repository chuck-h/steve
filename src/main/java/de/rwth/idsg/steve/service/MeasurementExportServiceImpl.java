package de.rwth.idsg.steve.service;

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.SteveConfiguration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.UnitOfMeasure;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Derived from SteVe project
 *
 * @author Chuck Harrison <cfharr@gmail.com>
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class MeasurementExportServiceImpl implements MeasurementExportService {

    private static final Joiner joiner = Joiner.on(",").skipNulls();

    private CloseableHttpClient httpClient;

    @PostConstruct
    private void init() {
        // "Reuse the HttpClient instance: Generally it is recommended to have a single instance of HttpClient
        // per communication component or even per application.";
        httpClient = HttpClients.createDefault();
    }

    @PreDestroy
    private void destroy() {
        try {
            httpClient.close();
        } catch (IOException e) {
            log.error("Failed to close HttpClient", e);
        }
    }

    @Override
    public void ocppMeasurement(String chargeBoxId, List<MeterValue> values, int connectorId) {
        // if emonpub is enabled: send HTTPS POST request to emon host
        SteveConfiguration.Emon emon = SteveConfiguration.CONFIG.getEmon();
        if (!emon.isEnabled()) {
            return;
        }

        List<PostCsvData> dataToPost = getData(chargeBoxId, values, connectorId);

        for (PostCsvData postData : dataToPost) {
            postSingleData(emon, postData);
        }
    }

    private void postSingleData(SteveConfiguration.Emon emon, PostCsvData postData) {
        try {
            URI uri = new URIBuilder(emon.getUri()+"/input/post").setParameter("time", Long.toString(postData.timeInSec))
                                                   .setParameter("node", postData.node)
                                                   .setParameter("csv", postData.csv)
                                                   .setParameter("apikey", emon.getApikey())
                                                   .build();

            String responseBody = httpClient.execute(new HttpGet(uri), new BasicResponseHandler());
            log.info("emonpub response: {}", responseBody);
        } catch (IOException e) {
            log.error("emonpub call failed", e);
        } catch (URISyntaxException e) {
            log.error("emonpub uri is not valid", e);
        }
    }

    private static List<PostCsvData> getData(String chargeBoxId, List<MeterValue> values, int connectorId) {
        List<PostCsvData> postDataList = new ArrayList<>();

        for (MeterValue value : values) {
            List<String> ampereValues =
                    value.getSampledValue()
                         .stream()
                         .filter(sv -> sv.getMeasurand() == Measurand.CURRENT_IMPORT)
                         .filter(sv -> sv.getUnit() == UnitOfMeasure.A)
                         .map(SampledValue::getValue)
                         .collect(Collectors.toList());

            if (!ampereValues.isEmpty()) {
                PostCsvData data = PostCsvData.builder()
                                              .timeInSec(value.getTimestamp().getMillis()/1000)
                                              .node(chargeBoxId + "-C" + connectorId)
                                              .csv(joiner.join(ampereValues))
                                              .build();
                postDataList.add(data);
            }
        }

        return postDataList;
    }
    
    @Builder
    private static class PostCsvData {
        private final long timeInSec;
        private final String node;
        private final String csv; // with comma delimiter
    }

}
