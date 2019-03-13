#### StrongKey FIDO Server, Community Edition

## Prerequisites

-  **The scripted installation process has been tested on CentOS 7, Ubuntu 18.04, and Debian 9 only.** The installation script is untested on other flavors of Linux but may work with slight modifications.

-  A **fully qualified public domain name (FQDN)**. It is very important to have a hostname that is at least _top-level domain (TLD)_+1 (i.e., [acme.com](http://acme.com), [example.org](http://example.org), etc); otherwise FIDO functionality may not work.

-  The installation script installs Payara running HTTPS on port 8181, so make sure all firewall rules allow that port to be accessed.

- StrongKey's FIDO Server must be installed before the sample Relying Party and sample WebAuthn.

----------------

## Installation

1. Install **wget** if it does not exist already.
    ```sh
    sudo yum install wget 
    or
    sudo apt install wget
     ```

2.  **Change directory** to the target download folder.

3.  **Download** the binary distribution file [FIDOServer-v0.9-dist.tgz](https://github.com/StrongKey/FIDO-Server/blob/master/FIDOServer-v0.9-dist.tgz).

    ```sh
    wget https://github.com/StrongKey/FIDO-Server/raw/master/FIDOServer-v0.9-dist.tgz
    ```

4.  **Extract the downloaded file to the current directory**:

    ```sh
    tar xvzf FIDOServer-v0.9-dist.tgz
    ```
5. Be sure that you have your **server FQDN set as its hostname**. This is necessary to properly configure the self-signed certificate for the API. Check with the following command:

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

6.  **Execute** the _install-skfs.sh_ script as follows:

    ```sh
    sudo ./install-skfs.sh
    ```

    The installation script will create a _strongkey_ user account with the home directory of _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the _/usr/local/strongkey_ directory and be run by the _strongkey_ user. The default password for the _strongkey_ user is _ShaZam123_.
    
    **NOTE: The policy for the StrongKey FIDO Server is a generic policy with default settings.**

7. Using the following command, **confirm your FIDO Server is running**. You should get the API _Web Application Definition Language (WADL)_ file back in response.

    ```sh
    curl -k https://localhost:8181/api/application.wadl
    ```

8. To test this installation of the FIDO2 server, check out the [sample Relying Party](https://github.com/StrongKey/relying-party-java) and [sample WebAuthn client](https://github.com/StrongKey/WebAuthn).

