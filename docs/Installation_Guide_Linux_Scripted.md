**This document first explains the installation of the StrongKey FIDO Server using a script and default settings, then describes the [manual, more customizeable process below](#unscripted). 

# Prerequisites

A  _fully qualified domain name (FQDN)_  for a hostname with either DNS or local hostfile entry in  _/etc/hosts_  that can resolve the hostname. It is very important to have a hostname that is at least TLD+1 (i.e.  [acme.com](http://acme.com),  [example.org](http://example.org), etc) otherwise FIDO functionality may not work.

The installation process has been tested on CentOS 7 only. The installation script is untested on CentOS 5, CentOS 6, and other flavors of Linux but may work with slight modifications.

It is recommended to have at least 10GB of available disk space and 4GB of memory.

----------

# Downloads

The following must be installed and configured to run StrongKey FIDO Server:

-   **Download the following**  binaries and copy them to the folder where the downloaded source resides:
    
    -   [FIDOServer-v#.#.tgz](https://github.com/StrongKey/FIDO-Server)  and  **save it as  _jade.tgz_**
    -   [payara-4.1.2.181.zip](http://repo1.maven.org/maven2/fish/payara/blue/distributions/payara/4.1.2.181/payara-4.1.2.181.zip)
    -   [mariadb-10.2.13-linux-x86_64.tar.gz](https://downloads.mariadb.org/mariadb/10.2.13/)
    -   [mariadb-java-client-2.2.2.jar](https://downloads.mariadb.com/Connectors/java/connector-java-2.2.2/mariadb-java-client-2.2.2.jar)
    -   [Jemalloc 3.6.0-1](https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm)
    
    StrongKey FIDO Server has been tested with these versions of the above software. StrongKey FIDO Server should work with any new minor versions released, but the installation script must be modified to accommodate the new filenames.  
    
-   **Modify**  the  _COMPANY_  variable in  _install-skfs.sh_. Otherwise, the default configuration should work on systems with at least 4 GB available.  
    

## Download and Install_Open Java Development Kit (JDK)_

As  _root_, type the following command:  
`yum install java-1.8.0-openjdk`

# Run the Installation Script

The installation script must be run as  _root_. The script will create a  _strongkey_  user account with the home directory of  _/usr/local/strongkey_. All software required for the StrongKey FIDO Server will be deployed to the  _/usr/local/strongkey_  directory and be run by  _strongkey_.

**NOTE:**  While the installation script allows for changing the default  _strongkey_  home directory, the software has not be updated to recognize a non-default directory.

1.  **Execute**  the  _[install-skfs.sh](http://install-skfs.sh)_  script.  
    `>  **<path to download directory>**/install-skfs.sh`
    
2.  If the script indicates a problem,  **correct**  the error and re-run the script.
    
3.  When the script finishes, all software will have been deployed and a  _strongkey_  user has been created.  **Log out**  of  _root_  and  **login**  to the  _strongkey_  user for the next steps. The default password for the  _strongkey_  user is  _ShaZam123_.
    

----------

# Test StrongKey FIDO Server

PLACEHOLDER CHAPTER

-----------------------------------
# Manual Installation



