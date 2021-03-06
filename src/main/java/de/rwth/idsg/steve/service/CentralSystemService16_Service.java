package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppTransport;
import de.rwth.idsg.steve.repository.ChargePointRepository;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import de.rwth.idsg.steve.web.dto.ocpp.RemoteStartTransactionParams;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.DataTransferRequest;
import ocpp.cs._2015._10.DataTransferResponse;
import ocpp.cs._2015._10.DataTransferStatus;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2015._10.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2015._10.FirmwareStatusNotificationRequest;
import ocpp.cs._2015._10.FirmwareStatusNotificationResponse;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.IdTagInfo;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.MeterValuesResponse;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StatusNotificationResponse;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static jooq.steve.db.tables.Connector.CONNECTOR;
import static jooq.steve.db.tables.ConnectorFeatures.CONNECTOR_FEATURES;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
@Service
public class CentralSystemService16_Service {

    @Autowired private OcppServerRepository ocppServerRepository;
    @Autowired private SettingsRepository settingsRepository;

    @Autowired private OcppTagService ocppTagService;
    @Autowired private NotificationService notificationService;
    @Autowired private ChargePointHelperService chargePointHelperService;
    @Autowired private MeasurementExportService measurementExportService;

    @Autowired private DSLContext ctx;
    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String chargeBoxIdentity,
                                                     OcppProtocol ocppProtocol) {

        boolean isRegistered = chargePointHelperService.isRegistered(chargeBoxIdentity);
        notificationService.ocppStationBooted(chargeBoxIdentity, isRegistered);
        DateTime now = DateTime.now();

        if (isRegistered) {
            log.info("The chargebox '{}' is registered and its boot acknowledged.", chargeBoxIdentity);
            UpdateChargeboxParams params =
                    UpdateChargeboxParams.builder()
                                         .ocppProtocol(ocppProtocol)
                                         .vendor(parameters.getChargePointVendor())
                                         .model(parameters.getChargePointModel())
                                         .pointSerial(parameters.getChargePointSerialNumber())
                                         .boxSerial(parameters.getChargeBoxSerialNumber())
                                         .fwVersion(parameters.getFirmwareVersion())
                                         .iccid(parameters.getIccid())
                                         .imsi(parameters.getImsi())
                                         .meterType(parameters.getMeterType())
                                         .meterSerial(parameters.getMeterSerialNumber())
                                         .chargeBoxId(chargeBoxIdentity)
                                         .heartbeatTimestamp(now)
                                         .build();

            ocppServerRepository.updateChargebox(params);
        } else {
            log.error("The chargebox '{}' is NOT registered and its boot NOT acknowledged.", chargeBoxIdentity);
        }

        return new BootNotificationResponse()
                .withStatus(isRegistered ? RegistrationStatus.ACCEPTED : RegistrationStatus.REJECTED)
                .withCurrentTime(now)
                .withInterval(settingsRepository.getHeartbeatIntervalInSeconds());
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(
            FirmwareStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(
            StatusNotificationRequest parameters, String chargeBoxIdentity) {
        // Optional field
        DateTime timestamp = parameters.isSetTimestamp() ? parameters.getTimestamp() : DateTime.now();

        InsertConnectorStatusParams params =
                InsertConnectorStatusParams.builder()
                                           .chargeBoxId(chargeBoxIdentity)
                                           .connectorId(parameters.getConnectorId())
                                           .status(parameters.getStatus().value())
                                           .errorCode(parameters.getErrorCode().value())
                                           .timestamp(timestamp)
                                           .errorInfo(parameters.getInfo())
                                           .vendorId(parameters.getVendorId())
                                           .vendorErrorCode(parameters.getVendorErrorCode())
                                           .build();

        ocppServerRepository.insertConnectorStatus(params);

        if (parameters.getStatus() == ChargePointStatus.PREPARING) {
            // test for autostart feature
            int connectorPk = getConnectorPkFromConnector(ctx, chargeBoxIdentity, parameters.getConnectorId());
            List<Map<String, Object>> result = ctx.select(CONNECTOR_FEATURES.AUTOSTART_TAG)
                                 .from(CONNECTOR_FEATURES)
                                 .where(CONNECTOR_FEATURES.CONNECTOR_PK.equal(connectorPk))
                                 .and(CONNECTOR_FEATURES.AUTOSTART_TAG.isNotNull())
                                 .fetchMaps();
            log.info("autostart check{}",result);
            if (result.size() >0) {
                String idTag = result.get(0).get("autostart_tag").toString();
                autostartTransaction(chargeBoxIdentity, parameters.getConnectorId(), idTag);
            }
        }

        if (parameters.getStatus() == ChargePointStatus.FAULTED) {
            notificationService.ocppStationStatusFailure(
                    chargeBoxIdentity, parameters.getConnectorId(), parameters.getErrorCode().value());
        }

        return new StatusNotificationResponse();
    }

    private Boolean autostartTransaction(String chargeBoxId, Integer connectorId, String idTag) {
        Boolean success = false;
        RemoteStartTransactionParams params = new RemoteStartTransactionParams();
        params.setConnectorId(connectorId);
        params.setIdTag(idTag);
        params.setChargePointSelectList(List.of(new ChargePointSelect(OcppTransport.JSON, chargeBoxId)));
        client16.remoteStartTransaction(params);
        // TODO get success/failure status; temporary hack: delay for reply and assume ok
        try {
            TimeUnit.SECONDS.sleep(2);
        }
        catch(InterruptedException ex) {
        } // never mind
        success = true;
        return success;
    }


    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String chargeBoxIdentity) {
        if (parameters.isSetMeterValue()) {
            ocppServerRepository.insertMeterValues(chargeBoxIdentity, parameters.getMeterValue(),
                                                   parameters.getConnectorId(), parameters.getTransactionId());
            measurementExportService.ocppMeasurement(chargeBoxIdentity, parameters.getMeterValue(),
                                                   parameters.getConnectorId());

        }
        return new MeterValuesResponse();
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(
            DiagnosticsStatusNotificationRequest parameters, String chargeBoxIdentity) {
        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String chargeBoxIdentity) {
        InsertTransactionParams params =
                InsertTransactionParams.builder()
                                       .chargeBoxId(chargeBoxIdentity)
                                       .connectorId(parameters.getConnectorId())
                                       .idTag(parameters.getIdTag())
                                       .startTimestamp(parameters.getTimestamp())
                                       .startMeterValue(Integer.toString(parameters.getMeterStart()))
                                       .reservationId(parameters.getReservationId())
                                       .build();

        Integer transactionId = ocppServerRepository.insertTransaction(params);
        IdTagInfo info = ocppTagService.getIdTagInfo(parameters.getIdTag(), chargeBoxIdentity);

        return new StartTransactionResponse()
                .withIdTagInfo(info)
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String chargeBoxIdentity) {
        int transactionId = parameters.getTransactionId();
        String stopReason = parameters.isSetReason() ? parameters.getReason().value() : null;

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                                       .chargeBoxId(chargeBoxIdentity)
                                       .transactionId(transactionId)
                                       .stopTimestamp(parameters.getTimestamp())
                                       .stopMeterValue(Integer.toString(parameters.getMeterStop()))
                                       .stopReason(stopReason)
                                       .build();

        ocppServerRepository.updateTransaction(params);

        if (parameters.isSetTransactionData()) {
            ocppServerRepository.insertMeterValues(chargeBoxIdentity, parameters.getTransactionData(), transactionId);
        }

        // Get the authorization info of the user
        if (parameters.isSetIdTag()) {
            IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(parameters.getIdTag(), chargeBoxIdentity);
            return new StopTransactionResponse().withIdTagInfo(idTagInfo);
        } else {
            return new StopTransactionResponse();
        }
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String chargeBoxIdentity) {
        DateTime now = DateTime.now();
        ocppServerRepository.updateChargeboxHeartbeat(chargeBoxIdentity, now);

        return new HeartbeatResponse().withCurrentTime(now);
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String chargeBoxIdentity) {
        // Get the authorization info of the user
        String idTag = parameters.getIdTag();
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(idTag, chargeBoxIdentity);

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
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

    /**
     * Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
     */
    public DataTransferResponse dataTransfer(DataTransferRequest parameters, String chargeBoxIdentity) {
        log.info("[Data Transfer] Charge point: {}, Vendor Id: {}", chargeBoxIdentity, parameters.getVendorId());
        if (parameters.isSetMessageId()) {
            log.info("[Data Transfer] Message Id: {}", parameters.getMessageId());
        }
        if (parameters.isSetData()) {
            log.info("[Data Transfer] Data: {}", parameters.getData());
        }

        // OCPP requires a status to be set. Since this is a dummy impl, set it to "Accepted".
        // https://github.com/RWTH-i5-IDSG/steve/pull/36
        return new DataTransferResponse().withStatus(DataTransferStatus.ACCEPTED);
    }
}
