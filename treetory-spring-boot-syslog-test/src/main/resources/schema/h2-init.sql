SET SCHEMA test;

DROP TABLE IF EXISTS test.RAWLOG_TEST CASCADE;

CREATE TABLE test.RAWLOG_TEST (
  id int(11) NOT NULL AUTO_INCREMENT,
  log varchar(4000) NOT NULL,
  system_timestamp datetime NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS test.NORMALIZEDLOG_TEST CASCADE;

CREATE TABLE test.NORMALIZEDLOG_TEST (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  create_time datetime NOT NULL,
  dev_id bigint(20) unsigned NOT NULL,
  dev_class varchar(32) DEFAULT NULL,
  event_class varchar(32) DEFAULT NULL,
  event varchar(32) DEFAULT NULL,
  org_id int(11) DEFAULT NULL,
  proxy_id int(11) DEFAULT NULL,
  origin_ip bigint(20) unsigned DEFAULT NULL,
  host_name varchar(32) DEFAULT NULL,
  interface varchar(32) DEFAULT NULL,
  protocol_name varchar(32) DEFAULT NULL,
  protocol_number tinyint(3) unsigned DEFAULT NULL,
  service_name varchar(32) DEFAULT NULL,
  s_ip bigint(20) unsigned DEFAULT NULL,
  s_port smallint(5) unsigned DEFAULT NULL,
  d_ip bigint(20) unsigned DEFAULT NULL,
  d_port smallint(5) unsigned DEFAULT NULL,
  severity smallint(5) unsigned DEFAULT NULL,
  rule_id varchar(32) DEFAULT NULL,
  action varchar(32) DEFAULT NULL,
  sequence varchar(32) DEFAULT NULL,
  nat_s_ip bigint(20) unsigned DEFAULT NULL,
  nat_s_port smallint(5) unsigned DEFAULT NULL,
  nat_d_ip bigint(20) unsigned DEFAULT NULL,
  nat_d_port smallint(5) unsigned DEFAULT NULL,
  nat_rule varchar(32) DEFAULT NULL,
  start_time datetime DEFAULT NULL,
  end_time datetime DEFAULT NULL,
  duration int(10) unsigned DEFAULT NULL,
  event_count int(10) unsigned DEFAULT NULL,
  rule_group varchar(32) DEFAULT NULL,
  rule_name varchar(128) DEFAULT NULL,
  signature_name varchar(128) DEFAULT NULL,
  mac_addr varchar(20) DEFAULT NULL,
  direction int(11) DEFAULT NULL,
  tx_bytes int(11) DEFAULT NULL,
  rx_bytes int(11) DEFAULT NULL,
  user_name varchar(32) DEFAULT NULL,
  reserved varchar(32) DEFAULT NULL,
  etc varchar(128) DEFAULT NULL,
  PRIMARY KEY (id)
);