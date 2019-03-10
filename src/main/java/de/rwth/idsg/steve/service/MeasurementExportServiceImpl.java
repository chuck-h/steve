package de.rwth.idsg.steve.service;

import com.google.common.base.Joiner;
import de.rwth.idsg.steve.SteveConfiguration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.Phase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.jooq.DSLContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jooq.steve.db.tables.CommPartners.COMM_PARTNERS;
import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorFeatures.CONNECTOR_FEATURES;
/**
 * Derived from SteVe project
 *
 * @author Chuck Harrison <cfharr@gmail.com>
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
public class MeasurementExportServiceImpl implements MeasurementExportService {

    @Autowired private DSLContext ctx;

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
        // if emonpub is enabled: send HTTPS POST request to emon host(s)
        SteveConfiguration.Emon emon = SteveConfiguration.CONFIG.getEmon();
        if (!emon.isEnabled()) {
            return;
        }

        List<PostEmonData> dataToPost = getData(chargeBoxId, values, connectorId);

        for (PostEmonData postData : dataToPost) {
            postSingleData(emon, postData, chargeBoxId, connectorId);
        }
    }

    private void postSingleData(SteveConfiguration.Emon emon, PostEmonData postData,
                                String chargeBoxId, int connectorId) {
        int connectorPk = getConnectorPkFromConnector(ctx, chargeBoxId, connectorId);
        List<Map<String, Object>> result = ctx.select(COMM_PARTNERS.URL, COMM_PARTNERS.APIKEY)
                                 .from(COMM_PARTNERS)
                                 .join(CONNECTOR_FEATURES)
                                 .on(CONNECTOR_FEATURES.REPORTING_PARTNER.equal(COMM_PARTNERS.PARTNER_ID))
                                 .where(CONNECTOR_FEATURES.CONNECTOR_PK.equal(connectorPk))
                                 .fetchMaps();

        for (Map<String, Object> r : result) {
            String uriString = r.get("url").toString();
            String apikey = "";
            if (r.get("apikey") != null) {
                apikey = r.get("apikey").toString();
            }
            try {
                URI uri = new URIBuilder(uriString+"/input/post").setParameter("time", Long.toString(postData.timeInSec))
                                                       .setParameter("node", postData.node)
                                                       .setParameter("fulljson", postData.json)
                                                       .setParameter("apikey", apikey)
                                                       .build();

            log.info("posting " + uri.toString());
            String responseBody = httpClient.execute(new HttpGet(uri), new BasicResponseHandler());
            log.info("emonpub {} response: {}", uriString, responseBody);
            } catch (IOException e) {
                log.error("emonpub call failed {}", e.toString());
            } catch (URISyntaxException e) {
                log.error("emonpub uri is not valid {}", e);
            }
        }

    }

    private static List<PostEmonData> getData(String chargeBoxId, List<MeterValue> values, int connectorId) {
        List<PostEmonData> postDataList = new ArrayList<>();
        // TODO: test with a variety of MeterValue messages (including multiple report)
        for (MeterValue value : values) {
            List<String> jsonValues = new ArrayList<>();
            jsonValues.addAll(
                    value.getSampledValue()
                         .stream()
                         .filter(sv -> sv.getMeasurand() == Measurand.CURRENT_IMPORT)
                         .filter(sv -> sv.getUnit() == UnitOfMeasure.A)
                         .map(sv -> "\"amp" + sv.getPhase().value() + "\":" + sv.getValue())
                         .collect(Collectors.toList())
                    );
            jsonValues.addAll(
                    value.getSampledValue()
                         .stream()
                         .filter(sv -> sv.getMeasurand() == Measurand.ENERGY_ACTIVE_IMPORT_REGISTER)
                         .filter(sv -> sv.getUnit() == UnitOfMeasure.K_WH)
                         .map(sv -> "\"kWh" + sv.getPhase().value() + "\":" + sv.getValue())
                         .collect(Collectors.toList())
                    );

            if (!jsonValues.isEmpty()) {
                PostEmonData data = PostEmonData.builder()
                                              .timeInSec(value.getTimestamp().getMillis()/1000)
                                              .node(chargeBoxId + "-C" + connectorId)
                                              .json("{" + String.join(",", jsonValues) + "}")
                                              .build();
                postDataList.add(data);
            }
        }

        return postDataList;
    }
    
    // copied from OcppServerRepositoryImpl.java - TBD refactor common code
    private int getConnectorPkFromConnector(DSLContext ctx, String chargeBoxIdentity, int connectorId) {
        return ctx.select(CONNECTOR.CONNECTOR_PK)
                  .from(CONNECTOR)
                  .where(CONNECTOR.CHARGE_BOX_ID.equal(chargeBoxIdentity))
                  .and(CONNECTOR.CONNECTOR_ID.equal(connectorId))
                  .fetchOne()
                  .value1();
    }

    @Builder
    private static class PostEmonData {
        private final long timeInSec;
        private final String node;
        private final String json;
    }

}
