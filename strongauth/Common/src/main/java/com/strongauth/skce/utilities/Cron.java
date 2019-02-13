/**
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, as published by the Free
 * Software Foundation and available at
 * http://www.fsf.org/licensing/licenses/lgpl.html, version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$ $Revision$
 * $Author$ $URL:
 * https://svn.strongauth.com/repos/jade/trunk/skce/skcebeans/src/main/java/com/strongauth/skce/utilities/Cron.java
 * $
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 * This class implements a cron-like facility using the classes from the
 * java.util.concurrent package. The following jobs are scheduled to run at
 * configurable times (for each domain):
 *
 * 1) generateDefaultKeysJob A job to re-generate default encryption keys; each
 * pertaining to one saka domain configured. The saka domain could have been
 * pre-configured through the skce-configuration.properties file which is read
 * on skce application boot-up OR can be a saka domain that is configured using
 * skce admin servlet GUI.
 *
 * This job is scheduled to run right at the boot up (during which only the
 * domains from the properties file are considered for default encryption key
 * generation. After the first time, this job will get invoked every day at
 * 12.00 AM, but the key re-generation will happen only if that is the first day
 * of the week (in case of weekly key), if that day is the first day of the
 * month (in case of monthly key), if that day is the first day of the year (in
 * case of annual key) and on every run obviously (in case of a daily key).
 *
 * 2) flushUserSessionsJob A job to flush out FIDO user sessions from the map
 * after they are expired. As an overview of what a fido user session is; Both
 * fido registration and fido authentication are 2-step processes. Registration
 * starts with a preregister call during which a new session for the user is
 * generated and stored in the map. A register call for the same user session
 * has to occur with in a certain specified time limit, otherwise the user
 * session has to be flushed out from the map to avoid bloating up the memory
 * usage. The key in the map is the sessionid and the value is a user session
 * object that binds many pieces of information like the username, time of
 * session creation etc., The concept is pretty similar for authentication and
 * perhaps authorization.
 *
 * This job runs for the first time on boot up of the application. Then runs
 * every x seconds (specified by
 * skfe.cfg.property.usersession.flush.frequency.seconds property) and when ever
 * it runs, it deletes the user sessions that are older than y seconds
 * (specified by skfe.cfg.property.usersession.flush.cutofftime.seconds
 * property).
 *
 * 3) flushUserKeyPointersJob A job to flush out FIDO user key pointers from the
 * map after they are expired.
 *
 * As an overview of what a fido user key pointer is; Both fido getuserkeysinfo
 * and fido deregister processes are time linked. When a getuserkeysinfo
 * web-service request is received, all of the keys that are registered for the
 * user are retrieved from the database and a random number is generated as a
 * pointer for each of these to be acted as a reference for de-registration. All
 * of these registered key to the key pointer mappings are stored in memory and
 * do have an expiry after which they will be flushed out from memory. When the
 * client applications want to deregister a key, they need to pass in the
 * pointer to the key to be deregistered and the code looks up into the map to
 * get the registered key information to be deleted/marked from the database. If
 * it doesn't find the pointer in the map, it means that it has got expired and
 * got flushed out; so de-registration cannot proceed further.
 *
 * This job runs for the first time on boot up of the application. Then runs
 * every x seconds (specified by
 * skfe.cfg.property.userkeypointers.flush.frequency.seconds property) and when
 * ever it runs, it deletes the user sessions that are older than y seconds
 * (specified by skfe.cfg.property.userkeypointers.flush.cutofftime.seconds
 * property).
 * 
 * 4) checkSAKAUrlsJob; A job to periodically verify the functioning of all SAKA
 * urls configured for a SAKA cluster. The check includes encryption and decryption
 * using a well known PAN. The check is done per domain (did) of SAKA using the
 * user credentials specified.
 * 
 * After checking the functioning, this job updates the 'workingsakaurls' field
 * in SAKADomainDefaultKey which is stored inside Common.defaultkeys map.
 * 
 * If some of the SAKA appliances stop functioning in between two runs of this
 * job; the SKEE encryption transactions with unique key will continue to fail
 * because the encryption operation will not check for the SAKA functionality 
 * before escrowing the unique encryption key to SAKA. The SKEE decryption 
 * operation will check for the SAKA functionality before trying to decrypt the 
 * key from SAKA based on a property of retrial (skce.cfg.property.saka.cluster.
 * retryotherurl). Hence, decryption might succeed based on if other urls in the 
 * SAKA cluster are functioning or not.
 *
 */

package com.strongauth.skce.utilities;

//import com.strongauth.skfe.utilities.skfeLogger;

/**
 * Class that takes care of periodic jobs (multiple of them); each job can be
 * defined with a specific time delay for the first time start, and a different
 * regular interval between each run
 */
public class Cron {

}
