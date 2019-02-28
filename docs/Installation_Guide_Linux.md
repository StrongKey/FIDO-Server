#### StrongKey FIDO Server, Community Edition

## Prerequisites

-  **The scripted installation process has been tested on CentOS 7 only.** The installation script is untested on other flavors of Linux but may work with slight modifications.

-  A **fully qualified public domain name (FQDN)**. It is very important to have a hostname that is at least _top-level domain (TLD)_+1 (i.e., [acme.com](http://acme.com), [example.org](http://example.org), etc); otherwise FIDO functionality may not work.

-  The installation script installs Payara running HTTPS on port 8181, so make sure all firewall rules allow that port to be accessed.

- StrongKey's FIDO Server must be installed before the sample Relying Party and sample WebAuthn.

----------------

## Installation

1.  **Change directory** to the target download folder.

2.  **Download and install** various dependencies. Run the following commands:
    
    ```sh
    sudo yum install wget unzip libaio java-1.8.0-openjdk
    ```

3.  **Download** the binary distribution file [FIDOServer-v0.9-dist.tgz](https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz).

    ```sh
    wget https://github.com/StrongKey/FIDO-Server/raw/master/FIDOServer-v0.9-dist.tgz
    ```

4.  **Extract the downloaded file to the current directory**:

    ```sh
    tar xvzf FIDOServer-v0.9-dist.tgz
    ```

5.  **Download the following**:
    
    -   [payara-4.1.2.181.zip](http://repo1.maven.org/maven2/fish/payara/distributions/payara/4.1.2.181/payara-4.1.2.181.zip)
    -   [mariadb-10.2.13-linux-x86_64.tar.gz](https://downloads.mariadb.org/interstitial/mariadb-10.2.13/bintar-linux-x86_64/mariadb-10.2.13-linux-x86_64.tar.gz/from/http%3A//ftp.hosteurope.de/mirror/archive.mariadb.org/)
    -   [mariadb-java-client-2.2.2.jar](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar)
    -   [Jemalloc 3.6.0-1](https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm)

    To download the files directly to your server, copy the commands below:  
    
    ```sh
    wget http://repo1.maven.org/maven2/fish/payara/distributions/payara/4.1.2.181/payara-4.1.2.181.zip
    wget https://downloads.mariadb.org/interstitial/mariadb-10.2.13/bintar-linux-x86_64/mariadb-10.2.13-linux-x86_64.tar.gz/from/http%3A//ftp.hosteurope.de/mirror/archive.mariadb.org/ -O mariadb-10.2.13-linux-x86_64.tar.gz
    wget https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar
    wget https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm
    ```

    **NOTE:** The StrongKey FIDO Server has been tested with the above software versions. The StrongKey FIDO Server should work with any new minor versions released, but the installation script must be modified to accommodate the new filenames.

6.  **Modify** the _COMPANY_ variable in _install-skfs.sh_ to your company name. This provides a lable for your certificate.

7. Be sure that you have your **server FQDN set as its hostname**. This is necessary to properly configure the self-signed certificate for the API. Check with the following command:

    ```sh
    hostname
    ```

    If you see only the machine name and not the public FQDN, run the following command:

    ```sh
    sudo hostnamectl set-hostname <YOUR SERVER'S PUBLIC FQDN>
    ```

    If you do not have DNS configured for this machine, please run the following command to add an entry to the _/etc/hosts_ file. 
    **DO NOT run this if your machine does not have a configured FQDN and is still running as _localhost_.**

    ```sh
    echo `hostname -I | awk '{print $1}'` $(hostname) | sudo tee -a /etc/hosts
    ```

8.  **Execute** the _install-skfs.sh_ script as follows:

    ```sh
    sudo ./install-skfs.sh
    ```

    The installation script will create a _strongkey_ user account with the home directory of _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the _/usr/local/strongkey_ directory and be run by the _strongkey_ user. The default password for the _strongkey_ user is _ShaZam123_.

9. Using the following command, **confirm your FIDO Server is running**. You should get the API WADL file back in response.

    ```sh
    curl -k https://localhost:8181/api/application.wadl
    ```

10. For further testing, check out the [sample Relying Party](https://github.com/StrongKey/relying-party-java) and [sample WebAuthn client](https://github.com/StrongKey/WebAuthn).

