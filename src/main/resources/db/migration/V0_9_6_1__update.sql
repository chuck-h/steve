CREATE TABLE `comm_partners` (
    `partner_id` VARCHAR(40) NOT NULL PRIMARY KEY,
    `url` VARCHAR(40),
    `apikey` VARCHAR(40) DEFAULT NULL
);

INSERT INTO `comm_partners` (partner_id, url)
VALUES ('load manager', 'http://127.0.0.1:9092/loadmanager');
INSERT INTO `comm_partners` (partner_id, url, apikey)
VALUES ('openevse', 'https://data.openevse.com/emoncms', '8859ca3ddb151f961c0785703b99ed9f');

