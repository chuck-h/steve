ALTER TABLE `connector_features`
ADD CONSTRAINT `FK_connector_features_autostart_tag`
FOREIGN KEY (`autostart_tag`) REFERENCES `ocpp_tag` (`id_tag`) ON DELETE CASCADE ON UPDATE NO ACTION;

