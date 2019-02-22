#### StrongKey FIDO2 Community Edition Server

**This document first explains the installation of the StrongKey FIDO Server using a script and default settings, then describes the [manual, more customizeable process below](#unscripted).**

-----------------------------------------------
# SCRIPTED INSTALLATION

## Prerequisites

-  The installation process has been tested on CentOS 7 only. The installation script is untested on other flavors of Linux but may work with slight modifications.

-  A _fully qualified domain name (FQDN)_ for a hostname with either DNS or local hostfile entry in _/etc/hosts_ that can resolve the hostname. It is very important to have a hostname that is at least TLD+1 (i.e., [acme.com](http://acme.com), [example.org](http://example.org), etc); otherwise FIDO functionality may not work.

----------------

## Installation

The following must be downloaded to the same folder (we recommend _/user/local/strongkey/_, but if another path is used, substitute it where appropriate herein), installed, and configured to run StrongKey FIDO Server:

1.  **Change directory** to the target download folder.
2.  **Download** the binary distribution file [FIDOServer-v0.9-dist.tgz](https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz).

```sh
wget https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz
```

3.  **Extract the downloaded file to the current directory**:

```sh
tar xvzf FIDOServer-v0.9.tgz
```

4.  **Download the following**
    
    -   [payara-4.1.2.181.zip](http://repo1.maven.org/maven2/fish/payara/distributions/payara/4.1.2.181/payara-4.1.2.181.zip)
    -   [mariadb-10.2.13-linux-x86_64.tar.gz](https://downloads.mariadb.org/interstitial/mariadb-10.2.13/bintar-linux-x86_64/mariadb-10.2.13-linux-x86_64.tar.gz/from/http%3A//ftp.hosteurope.de/mirror/archive.mariadb.org/)
    -   [mariadb-java-client-2.2.2.jar](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar)
    -   [Jemalloc 3.6.0-1](https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm)

You can copy the commands below to download the files directly to your server.  
    
```sh
wget http://repo1.maven.org/maven2/fish/payara/distributions/payara/4.1.2.181/payara-4.1.2.181.zip
wget https://downloads.mariadb.org/interstitial/mariadb-10.2.13/bintar-linux-x86_64/mariadb-10.2.13-linux-x86_64.tar.gz/from/http%3A//ftp.hosteurope.de/mirror/archive.mariadb.org/ -O mariadb-10.2.13-linux-x86_64.tar.gz
wget https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar
wget https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm
```
    
**NOTE:** StrongKey FIDO Server has been tested with the above software versions. StrongKey FIDO Server should work with any new minor versions released, but the installation script must be modified to accommodate the new filenames.

5.  **Modify** the _COMPANY_ variable in _install-skfs.sh_ to your company name.
6.  **Download and Install** _Open Java Development Kit (JDK)_. Type the following command:

```sh
sudo yum install java-1.8.0-openjdk
```

7.  The installation script must be run using _sudo_. The script will create a _strongkey_ user account with the home directory of _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the _/usr/local/strongkey_ directory and be run by the _strongkey_ user. 

**Execute** the _install-skfs.sh_ script as follows:

```sh
sudo ./install-skfs.sh
```
    
8.  When the script finishes, all software will have been deployed and a _strongkey_ user will have been been created. The default password for the _strongkey_ user is _ShaZam123_.

9. Confirm that your FIDO Server is running with the following command. You should get the API WADL file back in response.

```sh
curl -k https://localhost:8181/api/application.wadl
```

10. For further testing, check out the [sample Relying Party](https://github.com/StrongKey/relying-party-java) and [sample WebAuthn client](https://github.com/StrongKey/WebAuthn).

***This ends the scripted install instructions.***
===============================================

# <a name="unscripted"></a>MANUAL INSTALLATION

Use these instructions if you are not on CentOs or RHEL 7 or if the script above is not working for you.

1. Download StrongKey FIDO Server

Make sure you have the following set up and/or ready to run before you begin installation.

Please create the following directories on the Linux server where you are about to install.

```sh
sudo mkdir /usr/local/workspace
sudo mkdir /usr/local/strongkey/skfs
sudo mkdir /usr/local/strongkey/skfs/etc
sudo mkdir /usr/local/strongkey/skfs/keystores
```

2. Ensure the logged in account has _read/write/execute_ privileges on the _/strongkey_ directory. Failing to have privileges on _/strongkey_ directory will lead to many problems in the further steps of installation.

```sh
sudo chmod 755 /usr/local/strongkey/
```

3.  **Download** the binary distribution file [FIDOServer-v0.9-dist.tgz](https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz).

```sh
wget https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz
```

4.  **Extract the downloaded file** to _/usr/local/strongkey_:

```sh
sudo tar xvzf FIDOServer-v0.9-dist.tgz -C /usr/local/strongkey/
```

5.  **Verify the contents** of the _jade_ directory:

```sh
ls -l /usr/local/strongkey/jade/
```
    
6.  Copy the following two files from [here](https://github.com/StrongKey/FIDO-Server/tree/master/fidoserver/fidoserverInstall/src) into _/usr/local/strongkey/skfs/keystores_:

```sh
sudo wget https://github.com/StrongKey/FIDO-Server/tree/master/fidoserver/fidoserverInstall/src/signingkeystore.bcfks /usr/local/strongkey/skfs/keystores/signingkeystore.bcfks
sudo wget https://github.com/StrongKey/FIDO-Server/tree/master/fidoserver/fidoserverInstall/src/_signingtruststore.bcfks /usr/local/strongkey/skfs/keystores/_signingtruststore.bcfks
```

7. Download and Install _Open Java Development Kit (JDK)_

```sh
sudo yum install java-1.8.0-openjdk
```

8. Download and Install MariaDB

```sh
sudo yum install mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
sudo mysql_secure_installation
```

You will be asked to set the root password for your MariaDB. You will also be asked to make various changes - accept all changes to secure your database.

9. Edit _/etc/my.cnf_ to add this under "mysqld," then restart the database:

`lower_case_table_names = 1`

```sh
sudo systemctl restart mariadb
```

### Create a Database Schema for StrongKey FIDO Server

10.  **Login** to MariaDB as _root_ via terminal and use the _mysql_ database. This will open MariaDB access as _root_.
    
```sh
mysql -u root mysql -p<PASSWORD> mysql
```

11.  **Create a database** called _skfs_ and a MariaDB user called _skfsdbuser_ for the StrongKey FIDO Server application and grant privileges for the user on the new database. This document uses _AbracaDabra_ as the password for the _skfsdbuser_.
    
```sql
create database skfs;
grant all on skfs.* to skfsdbuser@localhost identified by 'AbracaDabra';
flush privileges;
exit;
```

12.  **Change Directory** to _/usr/local/strongkey/jade/sql/mysql_.

```sh
cd /usr/local/strongkey/jade/sql/mysql
```

13. **Login** to MariaDB via terminal as _skfsdbuser_ using the _skfs_ database.
    
```sh
mysql -u skfsdbuser -p<PASSWORD> skfs
```

14.  **Source** the _create.txt_ file to create tables. The output should not have any errors.
    
```sql
source create.txt;
```

15.  Use the **show tables** command in MariaDB to list the created tables.
    
```sql
show tables;
```

16.  Add the default entries to the the SERVERS, DOMAINS, and FIDO_POLICIES tables:
    
```sql
insert into SERVERS values (1, '$(hostname)', 'Active', 'Both', 'Active', null, null);

insert into DOMAINS values (1,'SKFS','Active','Active','-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'CN=SKFS Signing Key,OU=SAKA DID 1,OU=SKFS Signing Certificate 1,O=StrongKey Inc','https://localhost:8181',NULL);
    
insert into FIDO_POLICIES values (1,1,1,NOW(),NULL,'Default Policy','eyJzdG9yZVNpZ25hdHVyZXMiOmZhbHNlLCJleHRlbnNpb25zIjp7ImV4YW1wbGUuZXh0ZW5zaW9uIjp0cnVlfSwidXNlclNldHRpbmdzIjp0cnVlLCJjcnlwdG9ncmFwaHkiOnsiYXR0ZXN0YXRpb25fZm9ybWF0cyI6WyJmaWRvLXUyZiIsInBhY2tlZCIsInRwbSIsImFuZHJvaWQta2V5IiwiYW5kcm9pZC1zYWZldHluZXQiLCJub25lIl0sImVsbGlwdGljX2N1cnZlcyI6WyJzZWNwMjU2cjEiLCJzZWNwMzg0cjEiLCJzZWNwNTIxcjEiLCJjdXJ2ZTI1NTE5Il0sImFsbG93ZWRfcnNhX3NpZ25hdHVyZXMiOlsicnNhc3NhLXBrY3MxLXYxXzUtc2hhMSIsInJzYXNzYS1wa2NzMS12MV81LXNoYTI1NiIsInJzYXNzYS1wa2NzMS12MV81LXNoYTM4NCIsInJzYXNzYS1wa2NzMS12MV81LXNoYTUxMiIsInJzYXNzYS1wc3Mtc2hhMjU2IiwicnNhc3NhLXBzcy1zaGEzODQiLCJyc2Fzc2EtcHNzLXNoYTUxMiJdLCJhbGxvd2VkX2VjX3NpZ25hdHVyZXMiOlsiZWNkc2EtcDI1Ni1zaGEyNTYiLCJlY2RzYS1wMzg0LXNoYTM4NCIsImVjZHNhLXA1MjEtc2hhNTEyIiwiZWRkc2EiLCJlY2RzYS1wMjU2ay1zaGEyNTYiXSwiYXR0ZXN0YXRpb25fdHlwZXMiOlsiYmFzaWMiLCJzZWxmIiwiYXR0Y2EiLCJlY2RhYSIsIm5vbmUiXX0sInJlZ2lzdHJhdGlvbiI6eyJhdHRlc3RhdGlvbiI6WyJub25lIiwiaW5kaXJlY3QiLCJkaXJlY3QiXSwiZGlzcGxheU5hbWUiOiJyZXF1aXJlZCIsImF1dGhlbnRpY2F0b3JTZWxlY3Rpb24iOnsiYXV0aGVudGljYXRvckF0dGFjaG1lbnQiOlsicGxhdGZvcm0iLCJjcm9zcy1wbGF0Zm9ybSJdLCJ1c2VyVmVyaWZpY2F0aW9uIjpbInJlcXVpcmVkIiwicHJlZmVycmVkIiwiZGlzY291cmFnZWQiXSwicmVxdWlyZVJlc2lkZW50S2V5IjpbdHJ1ZSxmYWxzZV19LCJleGNsdWRlQ3JlZGVudGlhbHMiOiJlbmFibGVkIn0sImNvdW50ZXIiOnsicmVxdWlyZUluY3JlYXNlIjp0cnVlLCJyZXF1aXJlQ291bnRlciI6ZmFsc2V9LCJycCI6eyJuYW1lIjoiZGVtby5zdHJvbmdhdXRoLmNvbTo4MTgxIn0sImF1dGhlbnRpY2F0aW9uIjp7InVzZXJWZXJpZmljYXRpb24iOlsicmVxdWlyZWQiLCJwcmVmZXJyZWQiLCJkaXNjb3VyYWdlZCJdLCJhbGxvd0NyZWRlbnRpYWxzIjoiZW5hYmxlZCJ9fQ',1,'Active','',NOW(),NULL,NULL);
```
    
17.  Use the **show tables** command in MariaDB to list the created tables.
    
```sql
show tables;
```

18.  **Exit MariaDB**.
    
```sql
exit
```

19.  **Close** the terminal window.

MariaDB is now installed and configured for StrongKey FIDO Server.

----------

## Configure StrongKey FIDO Server

The StrongKey FIDO Server is completely configurable to suit a specific enterprise environment. These settings must be altered before the software is deployed and run. In this section, we will add the FIDOSERVER_HOME and set an environment variable to match.

20.  Open a **terminal window**.

21.  You must be a _root_ user to do this step. Edit the _/etc/bashrc_ file and export the variables using the command below:
    
 ```sh
vi /etc/bashrc
```
    
22.  **Add these lines** at the end of the file:
    
    `export FIDOSERVER_HOME=/usr/local/strongkey/skfs`
    `export GLASSFISH_HOME=/usr/local/strongkey/payara41/glassfish/`
    `PATH=$GLASSFISH_HOME/bin:$PATH`
    
23.  **Save** and **close** the file and **exit** out of _root_. 

## Install and Configure Payara

The StrongKey FIDO Server is fully tested using Payara 4.1 application server.

### Download and Install Payara 4.1

24.  **Download** Payara 4.1 edition .ZIP file, [payara-4.1.2.181.zip](http://repo1.maven.org/maven2/fish/payara/distributions/payara/4.1.2.181/payara-4.1.2.181.zip). **Save** the file.

25.  Open a  **terminal window** and extract the download using the following command:
    
```sh
unzip payara-4.1.2.181.zip -d /usr/local/strongkey
```

26.  **Download and copy** the [MariaDB JDBC driver .JAR](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar) file into the Payara _/lib_ directory.
    
```sh
cp {jar-location}/mariadb-java-client-2.2.2.jar /usr/local/strongkey/payara41/glassfish/lib
```

27.  **Start Payara** application server using the command below and ensure that the server has started successfully.
    
```sh
/usr/local/strongkey/payara41/glassfish/bin/asadmin start-domain
```

**NOTE:** To stop Payara, use the following command:

```sh
/usr/local/strongkey/payara41/glassfish/bin/asadmin stop-domain
```

### Configure Payara 4.1

28.  All configuration changes to Payara in this step can be done either on the command line using _asadmin_ commands, or via a browser-based administration console for Payara. For simplicity and ease of use, this document explains how to configure Payara using the Payara administration console.

29.  Open a web browser and type **localhost:4848** where _4848_ is the default port for Payara. If your instance of Payara has been configured to use another port, use that port instead. This opens the FIDO Server launch page.

### Configure Thread Pool

30.  On the left, expand the node **Configurations -> server-config -> Thread Pools -> http-thread-pool**.

31.  Set _Max Thread Pool Size_ to **100**.

32.  Click **Save**.

### Create JDBC Resources

33.  **Copy** the MariaDB JDBC Connector _.JAR_ file into Payara.

34.  On the left side, expand **Resources -> JDBC -> JDBC Connection Pools**.

35.  Click **New**. A page opens to create a new JDBC connection pool. Enter the information as shown here:
    
    | Field | Value |
    |-------|-------|
    | _Pool name_ | **SKFSPool** |
    | _Resource Type_ | **javax.sql.ConnectionPoolDataSource** |
    | _Database Driver Vendor_ | **MariaDB** |

36.  Click **Next**. On the next page, scroll down to the _Additional properties_ section. This is where you must specify the _database name_, _hostname_, _port_, and _user credentials_ for access. Delete all the existing values and add the new values as shown here:
    
    | **Field** | **Value** |
    |-------|-------|
    | _user_ | **skfsdbuser** |
    | _port_ | **3306** |
    | _password_ | **AbracaDabra** |
    | _ServerName_ | **localhost** |
    | _DatabaseName_ | **skfs** |

37.  Click **Finish**. This will create the connection pool; but to test the connection, click **ping**. The expected response is, “Ping succeeded”:
    
    **NOTE:** If the ping has failed, please verify you copied the JDBC driver _.JAR_ file into the Payara _/lib_ directory.
    
```sh
cp /usr/local/strongkey/jade/lib/mariadb-java-client-2.2.2.jar /usr/local/strongkey/payara41/glassfish/lib
```

38.  Now, we need to create a JDBC resource that uses the connection pool we just created above. On the left, expand **Resources -> JDBC -> JDBC Resources**. Click **New** and enter the **JDBC resource information** as shown here.
   
    _JNDI Name_ = **jdbc/skfs**
    
    _Pool Name_ = **SKFSPool**

39.  Click **Ok** to create the JDBC resource.

### Restart Payara Server

40.  To effect all the configuration changes, the **Payara server must be restarted**. On the **terminal window**, type the command below and press **Enter**.
    
```sh
/usr/local/strongkey/payara41/glassfish/bin/asadmin restart-domain
```

41.  If server must be stopped or started, please use the commands below:
    
```sh
/usr/local/strongkey/payara41/glassfish/bin/asadmin stop-domain /usr/local/strongkey/payara41/glassfish/bin/asadmin start-domain
```

Payara 4.1 is now installed, configured, and started.

----------

## Deploy StrongKey FIDO Server on Payara

The StrongKey FIDO Server is ready to be deployed.

42.  Open a **terminal window**.

43.  **Deploy** _fidoserver.ear_ on Payara using the _asadmin_ deploy command:
    
```sh
asadmin deploy /usr/local/strongkey/fidoserver.ear
```
    
    **NOTE:** If the deployment fails, verify the GLASSFISH_HOME is configured and check the server logs for errors:
    _/usr/local/strongkey/payara41/glassfish/domains/domain1/logs/server.log_

44. Confirm that your FIDO Server is running with the following command. You should get the API WADL file back in response.

```sh
curl -k https://localhost:8181/api/application.wadl
```

45. For further testing, check out the [sample Relying Party](https://github.com/StrongKey/relying-party-java) and [sample WebAuthn client](https://github.com/StrongKey/WebAuthn).
