/*
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.  
 *
 * SERVERS table for MySQL
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 */

CREATE TABLE IF NOT EXISTS SERVERS (
        sid                             tinyint unsigned not null,
        fqdn                            varchar(512) not null,
        status                          enum('Active', 'Inactive', 'Other') not null,
        replication_role                enum('Publisher', 'Subscriber', 'Both') not null,
        replication_status              enum('Active', 'Inactive', 'Other') not null,
        mask                            varchar(2048),
        notes                           varchar(512),
                primary key (sid),
                unique index (sid, fqdn)
        )
        engine=innodb;

CREATE TABLE IF NOT EXISTS REPLICATION (
        ssid            tinyint unsigned not null,
        rpid            bigint unsigned not null,
        tsid            tinyint unsigned not null,
        objectype       tinyint not null,
        objectop        tinyint not null,
        objectpk        varchar(64) not null,
        scheduled       datetime,
                primary key (ssid, rpid, tsid)
        )
        engine=innodb;

CREATE TABLE IF NOT EXISTS DOMAINS (
        did                             smallint unsigned primary key,
        name                            varchar(512) unique,
        status                          enum('Active', 'Inactive', 'Other') not null,
        replication_status              enum('Active', 'Inactive', 'Other') not null,
        encryption_certificate          varchar(4096),
        encryption_certificate_uuid     varchar(64),
        encryption_certificate_scheme   tinyint unsigned,
        signing_certificate             varchar(4096),
        signing_certificate_uuid        varchar(64),
        signing_certificate_scheme      tinyint unsigned,
        skce_signingdn                  varchar(512),
        skfe_appid                      varchar(256),
        notes                           varchar(512)
        )
        engine=innodb;

/****************************************************************************************************************************
8888888888 8888888 8888888b.   .d88888b.          888    d8P  8888888888 Y88b   d88P  .d8888b.  
888          888   888  "Y88b d88P" "Y88b         888   d8P   888         Y88b d88P  d88P  Y88b 
888          888   888    888 888     888         888  d8P    888          Y88o88P   Y88b.      
8888888      888   888    888 888     888         888d88K     8888888       Y888P     "Y888b.   
888          888   888    888 888     888         8888888b    888            888         "Y88b. 
888          888   888    888 888     888         888  Y88b   888            888           "888 
888          888   888  .d88P Y88b. .d88P         888   Y88b  888            888     Y88b  d88P 
888        8888888 8888888P"   "Y88888P" 88888888 888    Y88b 8888888888     888      "Y8888P" 
*****************************************************************************************************************************/
CREATE TABLE IF NOT EXISTS fido_keys (
        sid             		tinyint NOT NULL DEFAULT 1,
        did             		smallint(5) NOT NULL DEFAULT 1,
        username        		varchar(256) NOT NULL,
        fkid            		BIGINT(20) NOT NULL,
	userid				varchar(128) NULL,
        keyhandle       		VARCHAR(512) NOT NULL,
        appid           		VARCHAR(512) NULL,
        publickey       		VARCHAR(512) NULL,
        khdigest        		VARCHAR(512) NULL,
        khdigest_type   		ENUM('SHA256','SHA384','SHA512'),
        transports      		tinyint(4) UNSIGNED NULL,
	attsid				tinyint(4) NULL,
	attdid				smallint(5) NULL,
        attcid          		mediumint(20) NULL,
        counter         		INT NOT NULL,
        fido_version    		VARCHAR(45) NULL,
        fido_protocol   		ENUM('U2F','UAF','FIDO20') NULL,
	aaguid				varchar(36) NULL,
	registration_settings		LONGTEXT NULL,
	registration_settings_version 	INT(11) NULL,
        create_date     		DATETIME NOT NULL,
        create_location 		VARCHAR(256) NOT NULL,
        modify_date     		DATETIME NULL,
        modify_location 		VARCHAR(256),
        status          		ENUM('Active','Inactive') NOT NULL,
        signature       		VARCHAR(2048) NULL,
                PRIMARY KEY (sid,did,username,fkid),
                index (did, username, keyhandle)
        )
        ENGINE = InnoDB;

/********************************************************************************
8888888888 8888888 8888888b.   .d88888b.          888     888  .d8888b.  8888888888 8888888b.   .d8888b.  
888          888   888  "Y88b d88P" "Y88b         888     888 d88P  Y88b 888        888   Y88b d88P  Y88b 
888          888   888    888 888     888         888     888 Y88b.      888        888    888 Y88b.      
8888888      888   888    888 888     888         888     888  "Y888b.   8888888    888   d88P  "Y888b.   
888          888   888    888 888     888         888     888     "Y88b. 888        8888888P"      "Y88b. 
888          888   888    888 888     888         888     888       "888 888        888 T88b         "888 
888          888   888  .d88P Y88b. .d88P         Y88b. .d88P Y88b  d88P 888        888  T88b  Y88b  d88P 
888        8888888 8888888P"   "Y88888P" 88888888  "Y88888P"   "Y8888P"  8888888888 888   T88b  "Y8888P"
********************************************************************************/
/**
fuid : fido users table id that will be part of the fido keys table
**/
create table IF NOT EXISTS fido_users (
        sid                      tinyint NOT NULL DEFAULT 1,
        did                      tinyint NOT NULL DEFAULT 1,
        username                 varchar(256) NULL,
        userdn                   varchar(2048) NULL,
        fido_keys_enabled        ENUM('true','false') NULL,
        two_step_verification    ENUM('true','false') NULL,
        primary_email            varchar(256) NULL,
        registered_emails        varchar(2048) NULL,
        primary_phone_number     varchar(32) NULL,
        registered_phone_numbers varchar(2048) NULL,
        two_step_target          ENUM('email','phone') NULL,
        status                   ENUM('Active','Inactive') NOT NULL,
        signature                VARCHAR(2048) NULL,
                primary key(sid,did,username)
        )
        engine=innodb;

/********************************************************************************
       d8888 88888888888 88888888888 8888888888  .d8888b. 88888888888     d8888 88888888888 8888888  .d88888b.  888b    888          
      d88888     888         888     888        d88P  Y88b    888        d88888     888       888   d88P" "Y88b 8888b   888          
     d88P888     888         888     888        Y88b.         888       d88P888     888       888   888     888 88888b  888          
    d88P 888     888         888     8888888     "Y888b.      888      d88P 888     888       888   888     888 888Y88b 888          
   d88P  888     888         888     888            "Y88b.    888     d88P  888     888       888   888     888 888 Y88b888          
  d88P   888     888         888     888              "888    888    d88P   888     888       888   888     888 888  Y88888          
 d8888888888     888         888     888        Y88b  d88P    888   d8888888888     888       888   Y88b. .d88P 888   Y8888          
d88P     888     888         888     8888888888  "Y8888P"     888  d88P     888     888     8888888  "Y88888P"  888    Y888 88888888 
                                                                                                                                     
                                                                                                                                     
                                                                                                                                     
 .d8888b.  8888888888 8888888b. 88888888888 8888888 8888888888 8888888  .d8888b.         d8888 88888888888 8888888888  .d8888b.  
d88P  Y88b 888        888   Y88b    888       888   888          888   d88P  Y88b       d88888     888     888        d88P  Y88b 
888    888 888        888    888    888       888   888          888   888    888      d88P888     888     888        Y88b.      
888        8888888    888   d88P    888       888   8888888      888   888            d88P 888     888     8888888     "Y888b.   
888        888        8888888P"     888       888   888          888   888           d88P  888     888     888            "Y88b. 
888    888 888        888 T88b      888       888   888          888   888    888   d88P   888     888     888              "888 
Y88b  d88P 888        888  T88b     888       888   888          888   Y88b  d88P  d8888888888     888     888        Y88b  d88P 
 "Y8888P"  8888888888 888   T88b    888     8888888 888        8888888  "Y8888P"  d88P     888     888     8888888888  "Y8888P" 
********************************************************************************/
create table IF NOT EXISTS attestation_certificates (
	sid		tinyint(4) NOT NULL,
	did		smallint(5) NOT NULL,
        attcid          mediumint(20) NOT NULL,
	parent_sid      tinyint(4) NULL,
	parent_did      smallint(5) NULL,
	parent_attcid   mediumint(20) NULL,	
        certificate     LONGTEXT NOT NULL,
	issuer_dn	varchar(1024) NOT NULL,
	subject_dn	varchar(1024) NOT NULL,
	serial_number	varchar(512) NOT NULL,
	signature	varchar(2048) NULL,
                primary key(sid, did, attcid)
        )
        engine=innodb;

/********************************************************************************
8888888888 8888888 8888888b.   .d88888b.          8888888b.   .d88888b.  888      8888888  .d8888b. 8888888 8888888888  .d8888b.  
888          888   888  "Y88b d88P" "Y88b         888   Y88b d88P" "Y88b 888        888   d88P  Y88b  888   888        d88P  Y88b 
888          888   888    888 888     888         888    888 888     888 888        888   888    888  888   888        Y88b.      
8888888      888   888    888 888     888         888   d88P 888     888 888        888   888         888   8888888     "Y888b.   
888          888   888    888 888     888         8888888P"  888     888 888        888   888         888   888            "Y88b. 
888          888   888    888 888     888         888        888     888 888        888   888    888  888   888              "888 
888          888   888  .d88P Y88b. .d88P         888        Y88b. .d88P 888        888   Y88b  d88P  888   888        Y88b  d88P 
888        8888888 8888888P"   "Y88888P" 88888888 888         "Y88888P"  88888888 8888888  "Y8888P" 8888888 8888888888  "Y8888P"  
********************************************************************************/
create table IF NOT EXISTS FIDO_POLICIES (
        sid                             tinyint unsigned not null,
        did                             smallint unsigned not null,
        pid                             int unsigned not null,
        start_date                      DATETIME not null unique,
        end_date                        DATETIME,
        certificate_profile_name        varchar(64) not null,
        policy                          LONGTEXT not null,
        version                         int(11) not null,
        status                          enum('Active', 'Inactive') not null,
        notes                           varchar(512),
        create_date                     DATETIME not null,
        modify_date                     DATETIME,
        signature                       varchar(2048),
                primary key (sid, did, pid),
                unique index (sid, did, pid, start_date),
                unique index (did, certificate_profile_name)
        )
        engine=innodb;


/* EOF */
