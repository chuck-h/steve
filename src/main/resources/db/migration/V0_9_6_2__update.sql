CREATE TABLE `connector_features` (
    `pk` INT AUTO_INCREMENT PRIMARY KEY,
    `connector_pk` INT UNSIGNED,
    `reporting_partner` VARCHAR(40)
);

ALTER TABLE `connector_features`
ADD CONSTRAINT `FK_connector_features_connector_pk`
FOREIGN KEY (`connector_pk`) REFERENCES `connector` (`connector_pk`) ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE `connector_features`
ADD CONSTRAINT `FK_connector_features_reporting_partner`
FOREIGN KEY (`reporting_partner`) REFERENCES `comm_partners` (`partner_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

