**This document first explains the installation of the StrongKey FIDO Server using a script and default settings, then describes the [manual, more customizeable process below](#unscripted). 

-----------------------------------------------
SCRIPTED INSTALLATION
===============================================
## Prerequisites

A _fully qualified domain name (FQDN)_ for a hostname with either DNS or local hostfile entry in _/etc/hosts_ that can resolve the hostname. It is very important to have a hostname that is at least TLD+1 (i.e. [acme.com](http://acme.com), [example.org](http://example.org), etc) otherwise FIDO functionality may not work.

The installation process has been tested on CentOS 7 only. The installation script is untested on CentOS 5, CentOS 6, and other flavors of Linux but may work with slight modifications.

It is recommended to have at least 10GB of available disk space and 4GB of memory.

----------

## Downloads

The following must be installed and configured to run StrongKey FIDO Server:

-   **Download the following** binaries and copy them to the folder where the downloaded source resides:
    
    -   [FIDOServer-v#.#.tgz](https://github.com/StrongKey/FIDO-Server) **saved as _jade.tgz_**
    -   [payara-4.1.2.181.zip](http://repo1.maven.org/maven2/fish/payara/blue/distributions/payara/4.1.2.181/payara-4.1.2.181.zip)
    -   [mariadb-10.2.13-linux-x86_64.tar.gz](https://downloads.mariadb.org/mariadb/10.2.13/)
    -   [mariadb-java-client-2.2.2.jar](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar)
    -   [Jemalloc 3.6.0-1](https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm)
    
    StrongKey FIDO Server has been tested with these versions of the above software. StrongKey FIDO Server should work with any new minor versions released, but the installation script must be modified to accommodate the new filenames.

-   **Modify** the _COMPANY_ variable in _install-skfs.sh_. Otherwise, the default configuration should work on systems with at least 4 GB available.

-  **Download and Install** _Open Java Development Kit (JDK)_. As _root_, type the following command:

   `yum install java-1.8.0-openjdk`

## Run the Installation Script

The installation script must be run as _root_. The script will create a _strongkey_ user account with the home directory of _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the _/usr/local/strongkey_ directory and be run by _strongkey_.

**NOTE:** While the installation script allows for changing the default _strongkey_ home directory, the software has not be updated to recognize a non-default directory.

1.  **Execute** the _[install-skfs.sh](http://install-skfs.sh)_ script.

    `>  **<path to download directory>**/install-skfs.sh`

2.  If the script indicates a problem, **correct** the error and re-run the script.

3.  When the script finishes, all software will have been deployed and a _strongkey_ user has been created. **Log out** of _root_ and **login** to the _strongkey_ user for the next steps. The default password for the _strongkey_ user is _ShaZam123_.

**This ends the scripted install instructions.**
===============================================
<a name="test_fido_server"></a>TEST STRONGKEY FIDO SERVER
===============================================
PLACEHOLDER CHAPTER


**This ends the testing instructions.**
===============================================
<a name="unscripted"></a>MANUAL INSTALLATION
===============================================
## Download StrongKey FIDO Server

Make sure you have the following set up and/or ready to run before you begin installation.

Please create the following directories on the Linux server where you are about to install.

-   _/usr/local/workspace_
-   _/usr/local/strongkey_
-   _/usr/local/strongkey/skfs_
-   _/usr/local/strongkey/skfs/etc_
-   _/usr/local/strongkey/skfs/keystores_

Ensure the logged in account has _read/write/execute_ privileges on the _/strongkey_ directory. Failing to have privileges on _/strongkey_ directory will lead to many problems in the further steps of installation.

`chmod 755 /usr/local/strongkey/`

**NOTE:** It is highly recommended to perform the installation as a user other than _root_. Specific instructions in the installation process will need _root_ access and those instructions explicitly state so. Right after executing them, exit out of the _root_ session and continue.

1.  Download the binary distribution file [FIDOServer-v#.#.tgz](https://github.com/StrongKey/FIDO-Server).

2.  **Extract the downloaded file** to _/usr/local/strongkey_:

    `tar xvzf FIDOServer-v#.#.tgz -C /usr/local/strongkey/`

3.  **Verify the contents** of the _jade_ directory:

    `ls -l /usr/local/strongkey/jade/`
    
4.  Copy the following two files from [here](https://github.com/StrongKey/FIDO-Server/tree/master/fidoserver/fidoserverInstall/src) into _/usr/local/strongkey/skfs/keystores_:

    -   _signingkeystore.bcfks_
    -   _signingtruststore.bcfks_

----------
## Create strongkey User
These steps create a user with _/usr/local/strongkey/_ as the home folder. Type the following commands at a terminal prompt:

1.  `groupadd strongkey`
2.  `useradd -g strongkey -c"StrongKey" -d /usr/local/strongkey -m strongkey`
3.  `chcon -u user_u -t user_home_dir_t /usr/local/strongkey`
4.  `echo "ShaZam123" | passwd --stdin strongkey`

----------

## Download and Install _Open Java Development Kit (JDK)_

As _root_, type the following command:

`yum install java-1.8.0-openjdk`

## Download and Install MariaDB

As _root_, type the following commands:

1.  `yum install mariadb-server`
2.  `systemctl start mariadb`
3.  `systemctl enable mariadb`
4.  `systemctl status mariadb`
5.  `mysql_secure_installation`

Edit _/etc/my.cnf_ to add this under "mysqld," then restart the database:
1.  `lower_case_table_names = 1`
2.  `systemctl restart mariadb`

### Create a Database Schema for StrongKey FIDO Server

1.  **Login** to MariaDB as _root_ via terminal and use the _mysql_ database. This will open MariaDB access as _root_.
    
    `mysql -u root mysql -p<PASSWORD> mysql>`

2.  **Create a database** called _skfs_ and a MariaDB user called _skfsdbuser_ for the StrongKey FIDO Server application and grant privileges for the user on the new database. This document uses _AbracaDabra_ as the password for the _skfsdbuser_.
    
    `mysql> create database skfs; mysql> grant all on skfs.* to skfsdbuser@localhost identified by '<PASSWORD>'; mysql> flush privileges;`

3.  **Log out** of _root_ from MariaDB.
    
    `mysql> exit;`

4.  **Create tables** inside the _skfs_ database. **Login** to MariaDB via terminal as _skfsdbuser_ using the _skfs_ database.
    
    `mysql -u skfsdbuser -p<PASSWORD> skfs`

5.  **Source** the _create.txt_ file to create tables. The output should not have any errors.
    
    `source create.txt;`

6.  Use the **show tables** command in MariaDB to list the created tables.
    
    `show tables;`

7.  Add the default entries to the the SERVERS, DOMAINS, and FIDO_POLICIES tables:
    
    `insert into SERVERS values (1, '$(hostname)', 'Active', 'Both', 'Active', null, null);`
    
    `insert into DOMAINS values (1,'SKFS','Active','Active','-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'CN=SKFS Signing Key,OU=SAKA DID 1,OU=SKFS Signing Certificate 1,O=StrongKey Inc','https://localhost:8181',NULL);`
    
    `insert into FIDO_POLICIES values (1,1,1,NOW(),NULL,'Default Policy','eyJzdG9yZVNpZ25hdHVyZXMiOmZhbHNlLCJleHRlbnNpb25zIjp7ImV4YW1wbGUuZXh0ZW5zaW9uIjp0cnVlfSwidXNlclNldHRpbmdzIjp0cnVlLCJjcnlwdG9ncmFwaHkiOnsiYXR0ZXN0YXRpb25fZm9ybWF0cyI6WyJmaWRvLXUyZiIsInBhY2tlZCIsInRwbSIsImFuZHJvaWQta2V5IiwiYW5kcm9pZC1zYWZldHluZXQiLCJub25lIl0sImVsbGlwdGljX2N1cnZlcyI6WyJzZWNwMjU2cjEiLCJzZWNwMzg0cjEiLCJzZWNwNTIxcjEiLCJjdXJ2ZTI1NTE5Il0sImFsbG93ZWRfcnNhX3NpZ25hdHVyZXMiOlsicnNhc3NhLXBrY3MxLXYxXzUtc2hhMSIsInJzYXNzYS1wa2NzMS12MV81LXNoYTI1NiIsInJzYXNzYS1wa2NzMS12MV81LXNoYTM4NCIsInJzYXNzYS1wa2NzMS12MV81LXNoYTUxMiIsInJzYXNzYS1wc3Mtc2hhMjU2IiwicnNhc3NhLXBzcy1zaGEzODQiLCJyc2Fzc2EtcHNzLXNoYTUxMiJdLCJhbGxvd2VkX2VjX3NpZ25hdHVyZXMiOlsiZWNkc2EtcDI1Ni1zaGEyNTYiLCJlY2RzYS1wMzg0LXNoYTM4NCIsImVjZHNhLXA1MjEtc2hhNTEyIiwiZWRkc2EiLCJlY2RzYS1wMjU2ay1zaGEyNTYiXSwiYXR0ZXN0YXRpb25fdHlwZXMiOlsiYmFzaWMiLCJzZWxmIiwiYXR0Y2EiLCJlY2RhYSIsIm5vbmUiXX0sInJlZ2lzdHJhdGlvbiI6eyJhdHRlc3RhdGlvbiI6WyJub25lIiwiaW5kaXJlY3QiLCJkaXJlY3QiXSwiZGlzcGxheU5hbWUiOiJyZXF1aXJlZCIsImF1dGhlbnRpY2F0b3JTZWxlY3Rpb24iOnsiYXV0aGVudGljYXRvckF0dGFjaG1lbnQiOlsicGxhdGZvcm0iLCJjcm9zcy1wbGF0Zm9ybSJdLCJ1c2VyVmVyaWZpY2F0aW9uIjpbInJlcXVpcmVkIiwicHJlZmVycmVkIiwiZGlzY291cmFnZWQiXSwicmVxdWlyZVJlc2lkZW50S2V5IjpbdHJ1ZSxmYWxzZV19LCJleGNsdWRlQ3JlZGVudGlhbHMiOiJlbmFibGVkIn0sImNvdW50ZXIiOnsicmVxdWlyZUluY3JlYXNlIjp0cnVlLCJyZXF1aXJlQ291bnRlciI6ZmFsc2V9LCJycCI6eyJuYW1lIjoiZGVtby5zdHJvbmdhdXRoLmNvbTo4MTgxIn0sImF1dGhlbnRpY2F0aW9uIjp7InVzZXJWZXJpZmljYXRpb24iOlsicmVxdWlyZWQiLCJwcmVmZXJyZWQiLCJkaXNjb3VyYWdlZCJdLCJhbGxvd0NyZWRlbnRpYWxzIjoiZW5hYmxlZCJ9fQ',1,'Active','',NOW(),NULL,NULL);`
    
8.  Use the **show tables** command in MariaDB to list the created tables.
    
    `show tables;`

9.  **Exit MariaDB**.
    
    `exit`

10.  **Close** the terminal window.

MariaDB is now installed and configured for StrongKey FIDO Server.

----------

## Configure StrongKey FIDO Server

The StrongKey FIDO Server is completely configurable to suit a specific enterprise environment. These settings must be altered before the software is deployed and run.

### Configure StrongKey FIDO Server HOME Directory

In this section, we will configure the FIDOSERVER_HOME and set an environment variable to match.

1.  Open a **terminal window**.

2.  **Create a FIDOSERVER_HOME directory** called _skfs_. 
    _/usr/local/strongkey/skfs_ is the **FIDOSERVER_HOME** directory for this install.
    
    `mkdir -p /usr/local/strongkey/skfs/etc`

3.  You must be a _root_ user to do this step. Edit the _/etc/bashrc_ file and export the variables using the command below:
    
    `vi /etc/bashrc`
    
4.  **Add this line** at the end of the file:
    
    `export FIDOSERVER_HOME=/usr/local/strongkey/skfs`
    
5.  **Save** and **close** the file and **exit** out of _root_. 
    
    `:wq`

## Install and Configure Payara

The StrongKey FIDO Server is fully tested using Payara 4.1 application server.

### Download and Install Payara 4.1

1.  **Download** Payara 4.1 edition [_.ZIP file (_payara-4.1.2.181.zip_)](http://repo1.maven.org/maven2/fish/payara/blue/distributions/payara/4.1.2.181/payara-4.1.2.181.zip). **Save** the file.

2.  Open a  **terminal window** and extract the download using the following command:
    
    `unzip payara-4.1.2.181.zip -d /usr/local/strongkey`

3.  Create the _GLASSFISH_HOME_ **environment variable**:
    
    `GLASSFISH_HOME=/usr/local/strongkey/payara41/glassfish/ export GLASSFISH_HOME=/usr/local/strongkey/payara41/glassfish/ PATH=$GLASSFISH_HOME/bin:$PATH`

4.  **Download and copy** the [MariaDB JDBC driver _.JAR_](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar) file into the Payara _/lib_ directory.
    
    `cp {jar-location}/mariadb-java-client-2.2.2.jar /usr/local/strongkey/payara41/glassfish/lib`

5.  **Start Payara** application server using the command below and ensure that the server has started successfully.
    
    `/usr/local/strongkey/payara41/glassfish/bin/asadmin start-domain`

**NOTE:** To stop Payara, use the following command:

`/usr/local/strongkey/payara41/glassfish/bin/asadmin stop-domain`

### Configure Payara 4.1

1.  All configuration changes to Payara in this step can be done either on the command line using _asadmin_ commands, or via a browser-based administration console for Payara. For simplicity and ease of use, this document explains how to configure Payara using the Payara administration console.

2.  Open a web browser and type **localhost:4848** where _4848_ is the default port for Payara. If your instance of Payara has been configured to use another port, use that port instead.

3.  Enter **admin** as the username and **adminadmin** as the _password_ (the Payara default admin credentials). Depending on how the Payara admin login credentials are configured, the first page shown might be the login page for the console or might be the console’s home page.

4.  Clicking **Login** opens the console’s home page.

### Configure Thread Pool

1.  On the left, expand the node **Configurations -> server-config -> Thread Pools -> http-thread-pool**.

2.  Set _Max Thread Pool Size_ to **100**.

3.  Click **Save**.


### Create JDBC Resources

1.  **Copy** the MariaDB JDBC Connector _.JAR_ file into Payara.

2.  On the left side, expand **Resources -> JDBC -> JDBC Connection Pools**.

3.  Click **New**. A page opens to create a new JDBC connection pool. Enter the information as shown here:
    
    | Field | Value |
    |-------|-------|
    | _Pool name_ | **SKFSPool** |
    | _Resource Type_ | **javax.sql.ConnectionPoolDataSource** |
    | _Database Driver Vendor_ | **MariaDB** |

4.  Click **Next**. On the next page, scroll down to the _Additional properties_ section. This is where you must specify the _database name_, _hostname_, _port_, and _user credentials_ for access. Delete all the existing values and add the new values as shown here:
    
    | **Field** | **Value** |
    |-------|-------|
    | _user_ | **skfsdbuser** |
    | _port_ | **3306** |
    | _password_ | **AbracaDabra** |
    | _ServerName_ | **localhost** |
    | _DatabaseName_ | **skfs** |

5.  Click **Finish**. This will create the connection pool; but to test the connection, click **ping** and it should respond, “Ping succeeded”:
    
    **NOTE:** If the ping has failed, please verify you copied the JDBC driver _.JAR_ file into the Payara _/lib_ directory.
    
    `cp /usr/local/strongkey/jade/lib/mariadb-java-client-2.2.2.jar /usr/local/strongkey/payara41/glassfish/lib`

6.  Now, we need to create a JDBC resource that uses the connection pool we just created above.

7.  On the left, expand **Resources -> JDBC -> JDBC Resources**.

8.  Click **New** and enter the **JDBC resource information** as shown here.
   
    _JNDI Name_ = **jdbc/skfs**
    
    _Pool Name_ = **SKFSPool**

9.  Click **Ok** to create the JDBC resource.


### Restart Payara Server

1.  To get all the configuration changes into efffect, the **Payara server must be restarted**.

2.  On the **terminal window**, type the command below and press **Enter**.
    
    `/usr/local/strongkey/payara41/glassfish/bin/asadmin restart-domain`

3.  In case the server must be stopped or started for some reason, please use the commands below:
    
    `/usr/local/strongkey/payara41/glassfish/bin/asadmin stop-domain /usr/local/strongkey/payara41/glassfish/bin/asadmin start-domain`


Payara 4.1 is now installed, configured, and started.

----------

## Deploy StrongKey FIDO Server on Payara

The StrongKey FIDO Server is ready to be deployed.

1.  Open a **terminal window**.

2.  **Deploy** _skfs.ear_ on Payara using the _asadmin_ deploy command:
    
    `asadmin deploy /usr/local/strongkey/skfs.ear`
    
    **NOTE:** If the deployment fails, verify the GLASSFISH_HOME is configured and check the server logs for errors:
    _/usr/local/strongkey/payara41/glassfish/domains/domain1/logs/server.log_

3.  **Open a browser** and type the URL:
    
    _https://localhost:8181/api/application.wadl_
    
    The StrongKey FIDO Server application WADL displays.

**This concludes deployment of StrongKey FIDO Server. To test it, follow [these instructions](#test_fido_server).**
