#### StrongKey FIDO2 Community Edition Server

## Prerequisites

-  **The scripted installation process has been tested on CentOS 7 only.** The installation script is untested on other flavors of Linux but may work with slight modifications.

-  A **fully qualified domain name (FQDN)** for a hostname with either DNS or local hostfile entry in _/etc/hosts_ that can resolve the hostname. It is very important to have a hostname that is at least TLD+1 (i.e., [acme.com](http://acme.com), [example.org](http://example.org), etc); otherwise FIDO functionality may not work.

----------------

## Installation

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

7.  The installation script will create a _strongkey_ user account with the home directory of _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the _/usr/local/strongkey_ directory and be run by the _strongkey_ user. 

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

