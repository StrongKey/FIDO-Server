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
 * Script displays the number of records in the tables of the 
 * SKLES database
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 */

 SELECT "ansi_x9241_keys" AS table_name, COUNT(*) AS records FROM strongkeylite.ansi_x9241_keys UNION                                                       
 SELECT "batch_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.batch_requests UNION                                                       
 SELECT "configurations" AS table_name, COUNT(*) AS records FROM strongkeylite.configurations UNION                                                       
 SELECT "decryption_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.decryption_requests UNION                                             
 SELECT "deletion_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.deletion_requests UNION                                                 
 SELECT "domains" AS table_name, COUNT(*) AS records FROM strongkeylite.domains UNION                                                                     
 SELECT "encryption_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.encryption_requests UNION                                             
 SELECT "jobs" AS table_name, COUNT(*) AS records FROM strongkeylite.jobs UNION                                                                           
 SELECT "key_custodians" AS table_name, COUNT(*) AS records FROM strongkeylite.key_custodians UNION                                                       
 SELECT "relay_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.relay_requests UNION                                                       
 SELECT "replication" AS table_name, COUNT(*) AS records FROM strongkeylite.replication UNION                                                             
 SELECT "search_requests" AS table_name, COUNT(*) AS records FROM strongkeylite.search_requests UNION                                                     
 SELECT "servers" AS table_name, COUNT(*) AS records FROM strongkeylite.servers UNION
 SELECT "server_domains" AS table_name, COUNT(*) AS records FROM strongkeylite.server_domains UNION
 SELECT "symmetric_keys" AS table_name, COUNT(*) AS records FROM strongkeylite.symmetric_keys UNION                                                       
 SELECT "users" AS table_name, COUNT(*) AS records FROM strongkeylite.users; 
