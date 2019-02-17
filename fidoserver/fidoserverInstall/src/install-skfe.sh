#!/bin/bash
#
###############################################################
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License, as published by the Free Software Foundation and
# available at http://www.fsf.org/licensing/licenses/lgpl.html,
# version 2.1 or above.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# Copyright (c) 2001-2018 StrongAuth, Inc.
#
# $Date$
# $Revision$
# $Author$
# $URL$
#
# Must be run by 'root' from the /usr/local/software/skfe
# directory AFTER having downloaded all required components:
#
# - BouncyCastle
# - Glassfish
# - MariaDB RDBMS and JDBC Connector
# - OpenDJ LDAP Directory server
# - Utimaco CryptoServer (for Utimaco HSMs)
# - StrongAuth KeyAppliance
#
###############################################################

# Uncomment to show detailed installation process
#SHOWALL=1

##########################################
##########################################
# Company name for self signed certificate
COMPANY="StrongAuth Inc"

# Server Passwords
GLASSFISH_PASSWORD=adminadmin
LINUX_PASSWORD=ShaZam123
MARIA_ROOT_PASSWORD=BigKahuna
MARIA_SKLES_PASSWORD=AbracaDabra

##### CryptoEngine Module #####
## Service Account LDAP Configuration ##
SERVICE_LDAP_URL=localhost:1389
SERVICE_LDAP_CONNECTION=ldap                    # Other option is ldaps
SERVICE_LDAP_TYPE=LDAP                          # Other option is AD
SERVICE_LDAP_BIND_DN='cn=Directory Manager'
SERVICE_LDAP_BIND_PASS=Abcd1234!
SERVICE_LDAP_BASEDN='dc=strongauth,dc=com'      # Should be the same as the BaseDN of the Search LDAP

## Service Account for Encryption/Decryption on SKCE ##
SERVICE_LDAP_SVCUSER_DN='cn=service-cc-ce,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com'
SERVICE_LDAP_SVCUSER_PASS=Abcd1234!
SERVICE_LDAP_SVCUSER_GROUP='cn=Services'

## Service Account for Ping on SKCE ##
SERVICE_LDAP_PINGUSER_DN='cn=skceadmin,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com'
SERVICE_LDAP_PINGUSER_PASS=Abcd1234!
SERVICE_LDAP_PINGUSER_GROUP='cn=AdminAuthorized'

## Search User LDAP Configuration ##
## For Looking Up User Details ##
SEARCH_LDAP_URL=localhost:1389
SEARCH_LDAP_CONNECTION=ldap                     # Other option is ldaps
SEARCH_LDAP_TYPE=LDAP                           # Other option is AD
SEARCH_LDAP_BIND_DN='cn=Directory Manager'
SEARCH_LDAP_BIND_PASS=Abcd1234!

## Example Search User LDAP Configuration ##
#SEARCH_LDAP_URL=nova.strongauth.com:636
#SEARCH_LDAP_CONNECTION=ldaps
#SEARCH_LDAP_TYPE=AD
#SEARCH_LDAP_BIND_DN='cn=Administrator,cn=Users,dc=strongauth,dc=com'
#SEARCH_LDAP_BIND_PASS=Abcd1234!

## Mailhost Configurations ##
MAILHOST_TYPE=sendmail                           # Other options are SSL and StartTLS
MAILHOST_URL=localhost
MAILHOST_PORT=25
MAILHOST_FROM=info@strongauth.com
MAILHOST_USER=
MAILHOST_PASS=

## Example Mailhost Configurations for SSL ##
#MAILHOST_TYPE=SSL
#MAILHOST_URL=smtp.gmail.com
#MAILHOST_PORT=465
#MAILHOST_FROM=info@strongauth.com
#MAILHOST_USER=user@gmail.com
#MAILHOST_PASS=password

##########################################
##########################################

# Flags to indicate if a module should be installed
INSTALL_GLASSFISH=Y
INSTALL_MARIA=Y
INSTALL_OPENDJ=Y
INSTALL_JADE=Y

# Script logging location
LOGNAME=/root/strongauth_logs/install-skfe-$(date +%s)

# Start Required Distributables
GLASSFISH=payara-4.1.2.181.zip
JEMALLOC=jemalloc-3.6.0-1.el7.x86_64.rpm
MARIA=mariadb-10.2.13-linux-x86_64.tar.gz
MARIACONJAR=mariadb-java-client-2.2.2.jar
OPENDJ=OpenDJ-3.0.0.zip
# End Required Distributables

# Other vars
SKFE=jade.tgz
CENTOS=$(awk '{print $3}' /etc/redhat-release | cut -c 1)
STRONGAUTH_HOME=/usr/local/strongauth
SKCE_HOME=$STRONGAUTH_HOME/skce
SKFE_HOME=$STRONGAUTH_HOME/skfe
SKCEWS_HOME=$STRONGAUTH_HOME/skcews
GLASSFISH_HOME=$STRONGAUTH_HOME/payara41/glassfish
GLASSFISH_CONFIG=$GLASSFISH_HOME/domains/domain1/config
JAVA_HOME=/lib/jvm/jre-1.8.0
MARIAVER=mariadb-10.2.13-linux-x86_64
MARIATGT=mariadb-10.2.13
MARIA_HOME=$STRONGAUTH_HOME/$MARIATGT
OPENDJVER=opendj
OPENDJTGT=OpenDJ-3.0.0
OPENDJ_HOME=$STRONGAUTH_HOME/$OPENDJTGT
JADE=/usr/local/strongauth/jade
SKFE_SOFTWARE=/usr/local/software/skfe
INDEX_HTML=index.html
PING_FILE=abc.txt
SKCE_BASE_LDIF=skce-base.ldif

# dmidecode prints information for all memory slots, awk applies addition to the second column of every line that returns a numerical value for Size (basically adds the memory capacity of every slot that is reporting memory).
TOTAL_SYSTEM_MEMORY=$(dmidecode -t 17 | awk '/Size: [0-9]+ MB/{total = total + $2}END{print total}')
if [ -n "$TOTAL_SYSTEM_MEMORY" ]; then
        # Apply the user specified percentage, dividing by 1 encourages BC to apply rounding.
        XMXSIZE=$(echo "scale=0; $TOTAL_SYSTEM_MEMORY * .5 / 1" | bc)
        if [ "$XMXSIZE" -gt 4096 ]; then
                XMXSIZE=4096
        fi

        REMAINING_SYSTEM_MEMORY=$(( $TOTAL_SYSTEM_MEMORY - $XMXSIZE))
        if [ "$REMAINING_SYSTEM_MEMORY" -le 4096 ]; then
                BUFFERPOOLSIZE=$(echo "scale=0; $REMAINING_SYSTEM_MEMORY * .50 / 1" | bc)M
        elif [ "$REMAINING_SYSTEM_MEMORY" -le 16384 ]; then
                BUFFERPOOLSIZE=$(( $REMAINING_SYSTEM_MEMORY - 2048 ))M
        else
                BUFFERPOOLSIZE=$(( $REMAINING_SYSTEM_MEMORY - 4096 ))M
        fi
        XMXSIZE=${XMXSIZE}m
else
        TOTAL_SYSTEM_MEMORY=$(dmidecode -t 17 | awk '/Size: [0-9]+ GB/{total = total + $2}END{print total}')
        if [ -n "$TOTAL_SYSTEM_MEMORY" ]; then
                # Apply the user specified percentage, dividing by 1 encourages BC to apply rounding.
                XMXSIZE=$(echo "scale=0; $TOTAL_SYSTEM_MEMORY * .5 / 1" | bc)
                if [ "$XMXSIZE" -gt 4 ]; then
                        XMXSIZE=4
                fi

                REMAINING_SYSTEM_MEMORY=$(( $TOTAL_SYSTEM_MEMORY - $XMXSIZE))
                if [ "$REMAINING_SYSTEM_MEMORY" -le 4 ]; then
                        BUFFERPOOLSIZE=$(echo "scale=0; $REMAINING_SYSTEM_MEMORY * .50 / 1" | bc)G
                elif [ "$REMAINING_SYSTEM_MEMORY" -le 16 ]; then
                        BUFFERPOOLSIZE=$(( $REMAINING_SYSTEM_MEMORY - 2 ))G
                else
                        BUFFERPOOLSIZE=$(( $REMAINING_SYSTEM_MEMORY - 4 ))G
                fi
                XMXSIZE=${XMXSIZE}g
        else
                # Couldn't figure out total memory, apply safe defaults
                >&2 echo -e "$(tput setaf 1)Warning. Unable to assign memory to MariaDB and Payara. Check settings manually.$(tput sgr0)" | tee -a $LOGNAME
                XMXSIZE=512m
                BUFFERPOOLSIZE=512m
        fi
fi

function check_exists {
for ARG in "$@"
do
    if [ ! -f $ARG ]; then
        >&2 echo -e "\E[31m$ARG Not Found. Check to ensure the file exists in the proper location and try again.\E[0m" | tee -a $LOGNAME
        exit 1
    fi
done
}

function get_ip {
        # Try using getent if it is available, best option
        if ! getent hosts $1 2>/dev/null | awk '{print $1; succ=1} END{exit !succ}'; then

                # If we are here, likely don't have getent. Try reading /etc/hosts.
                if ! awk "/^[^#].*$1/ "'{ print $1; succ=1} END{exit !succ}' /etc/hosts; then

                        # Wasn't in /etc/hosts, try DNS
                        if ! dig +short +time=2 +retry=1 +tries=1 $1 | grep '.' 2>/dev/null; then

                                # Can't resolve IP
                                >&2 echo -e "\E[31mFQDN $1 not resolvable. Modify DNS or add a hosts entry and try again.\E[0m" | tee -a $LOGNAME
                                exit 1
                        fi
                fi
        fi
}

# Make sure we can resolve our own hostname
get_ip "$(hostname)" > /dev/null

if ! [ -d /root/strongauth_logs ]; then
        mkdir /root/strongauth_logs
fi

# Check that the script is run as root
if [ $UID -ne 0 ]; then
        >&2 echo -e "\E[31m$0 must be run as root\E[0m" | tee -a $LOGNAME
        exit 1
fi

# Check that strongauth doesn't already exist
if $(id strongauth &> /dev/null); then
        >&2 echo -e "\E[31m'strongauth' user already exists. Run cleanup.sh and try again.\E[0m" | tee -a $LOGNAME
        exit 1
fi

# Check that openjdk is installed
if ! [ -d "$JAVA_HOME" ]; then
        >&2 echo -e "\E[31mOpenJDK must be installed before running the installation script. Install it with the command 'yum install java-1.8.0-openjdk.x86_64'\E[0m" | tee -a $LOGFILE
        exit 1
fi

# Check that all files are present
if [ $INSTALL_GLASSFISH = 'Y' ]; then
        check_exists $SKFE_SOFTWARE/$GLASSFISH
fi

if [ $INSTALL_MARIA = 'Y' ]; then
        check_exists $SKFE_SOFTWARE/$MARIA $SKFE_SOFTWARE/$JEMALLOC $SKFE_SOFTWARE/$MARIACONJAR
fi

if [ $INSTALL_JADE = 'Y' ]; then
        check_exists $SKFE_SOFTWARE/$SKFE
        check_exists $SKFE_SOFTWARE/signingkeystore.bcfks $SKFE_SOFTWARE/signingtruststore.bcfks
fi

# Make backup directory if not there
if [ -d /etc/org ]; then
        :
else
        mkdir /etc/org
        cp /etc/bashrc /etc/org
        cp /etc/sudoers /etc/org
        cp /etc/inittab /etc/org
fi

# Create the strongauth group and user, and add it to /etc/sudoers
groupadd strongauth
useradd -g strongauth -c"StrongAuth, Inc" -d $STRONGAUTH_HOME -m strongauth
chcon -u user_u -t user_home_dir_t /usr/local/strongauth
echo $LINUX_PASSWORD | passwd --stdin strongauth
cat >> /etc/sudoers <<-EOFSUDOERS

## SKFE permissions
Cmnd_Alias SKFE_COMMANDS = /sbin/service glassfishd start, /sbin/service glassfishd stop, /sbin/service glassfishd restart, /sbin/service mysqld start, /sbin/service mysqld stop, /sbin/service mysqld restart, /sbin/service opendjd start, /sbin/service opendjd stop, /sbin/service opendjd restart /sbin/service opendjd start, /sbin/service opendjd stop, /sbin/service opendjd restart, /bin/systemctl restart glassfishd
strongauth ALL=SKFE_COMMANDS
EOFSUDOERS

##### Create skferc #####
cat > /etc/skferc << EOFSKFERC
      export CRYPTOSERVER=/dev/cs2.0
    export GLASSFISH_HOME=$GLASSFISH_HOME
         export JAVA_HOME=$JAVA_HOME
        export MYSQL_HOME=$MARIA_HOME
       export OPENDJ_HOME=$OPENDJ_HOME
   export STRONGAUTH_HOME=$STRONGAUTH_HOME
              export PATH=\$OPENDJ_HOME/bin:\$GLASSFISH_HOME/bin:\$JAVA_HOME/bin:\$MYSQL_HOME/bin:\$STRONGAUTH_HOME/bin:/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin:/root/bin

alias str='cd $STRONGAUTH_HOME'
alias jade='cd $STRONGAUTH_HOME/jade'
alias aslg='cd $GLASSFISH_HOME/domains/domain1/logs'
alias ascfg='cd $GLASSFISH_HOME/domains/domain1/config'
alias tsl='tail --follow=name $GLASSFISH_HOME/domains/domain1/logs/server.log'
alias mys='mysql -u skles -p\`dbpass 2> /dev/null\` strongkeylite'
alias repl='mysql -u skles -p\`dbpass 2> /dev/null\` strongkeylite -e "select tsid, count(1) from replication group by tsid"'
alias zmq='netstat -an | sed -re "s|tcp6?\s+[0-9]+\s+[0-9]+\s+||" -e "s|(\S+\s+)(\S+:700.\s+)(\S+)\s+|0\2\1\3|" | sort | sed -re "s|^0(\S+\s+)(\S+\s+)(\S+)|\2\1\3|" -ne "/:700[^ ].*(EST|SYN)/p"'
alias err='grep SKL-ERR $GLASSFISH_HOME/domains/domain1/logs/server.log | sed "s|.*\(SKL-ERR-....\).*|\1|" | sort | uniq -c'
alias errext='for i in \`grep SKL-ERR $GLASSFISH_HOME/domains/domain1/logs/server.log | sed "s|.*\(SKL-ERR-....\).*|\1|" | sort | uniq\`; do grep -m 1 \$i $GLASSFISH_HOME/domains/domain1/logs/server.log; done'
alias jpid="ps -efww | grep [g]lassfish.jar | awk '{print \\\$2}'"
alias gcu='jstat -gcutil -h12 \`jpid\` 5000'
alias jst='jstack \`jpid\`'
alias java='java -Djavax.net.ssl.trustStore=\$STRONGAUTH_HOME/certs/cacerts '
EOFSKFERC

echo ". /etc/skferc" >> /etc/bashrc

# Make needed directories
mkdir -p $STRONGAUTH_HOME/certs $STRONGAUTH_HOME/Desktop $STRONGAUTH_HOME/dbdumps $STRONGAUTH_HOME/lib $STRONGAUTH_HOME/bin $STRONGAUTH_HOME/appliance/etc $STRONGAUTH_HOME/crypto/etc $SKFE_HOME/etc $SKCE_HOME/etc $SKCE_HOME/keystores $SKCEWS_HOME/engine_in $SKCEWS_HOME/engine_out 

##### Install Jade #####
if [ $INSTALL_JADE = 'Y' ]; then

        echo "Installing SKFE..." | tee -a $LOGNAME
        tar zxf $SKFE_SOFTWARE/$SKFE -C $STRONGAUTH_HOME

        cp $SKFE_SOFTWARE/certimport.sh $STRONGAUTH_HOME/bin
        cp $SKFE_SOFTWARE/create-SKCE-Users.sh $STRONGAUTH_HOME/bin
        cp $STRONGAUTH_HOME/bin/* $STRONGAUTH_HOME/Desktop/

        chmod 700 $STRONGAUTH_HOME/Desktop/*.sh

        SERVICE_LDAP_USERNAME=$(sed -r 's|^[cC][nN]=([^,]*),.*|\1|' <<< "$SERVICE_LDAP_SVCUSER_DN")
        SERVICE_LDAP_SUFFIX=$(sed -r 's|^[cC][nN]=[^,]*(,.*)|\1|' <<< "$SERVICE_LDAP_SVCUSER_DN")

        SERVICE_LDAP_PINGUSER=$(sed -r 's|^[cC][nN]=([^,]*),.*|\1|' <<< "$SERVICE_LDAP_PINGUSER_DN")
        SERVICE_LDAP_PINGUSER_SUFFIX=$(sed -r 's|^[cC][nN]=[^,]*(,.*)|\1|' <<< "$SERVICE_LDAP_PINGUSER_DN")

        if [ "${SERVICE_LDAP_SUFFIX}" != "${SERVICE_LDAP_PINGUSER_SUFFIX}" ]; then
                echo "Warning: SERVICE_LDAP_USER and SERVICE_LDAP_PINGUSER must be in the same OU. Pinguser may not authenticate as expected. Run update-ldap-config with corrected users."
        fi

        sed -r "s|^(ldape.cfg.property.service.ce.ldap.ldapservicegroup=).*|\1$SERVICE_LDAP_SVCUSER_GROUP|
                s|^(ldape.cfg.property.service.ce.ldap.ldapadmingroup=).*|\1$SERVICE_LDAP_PINGUSER_GROUP|
                s|^(ldape.cfg.property.service.ce.ldap.ldapurl=).*|\1$SERVICE_LDAP_CONNECTION://$SERVICE_LDAP_URL|
                s|^(ldape.cfg.property.service.ce.ldap.ldaptype=).*|\1$SERVICE_LDAP_TYPE|
                s|^(ldape.cfg.property.service.ce.ldap.ldapdnsuffix=).*|\1$SERVICE_LDAP_SUFFIX|
                s|^(ldape.cfg.property.service.ce.ldap.ldapbinddn=).*|\1$SERVICE_LDAP_BIND_DN|
                s|^(ldape.cfg.property.service.ce.ldap.ldapbinddn.password=).*|\1$SERVICE_LDAP_BIND_PASS|
                s|^(ldape.cfg.property.service.ce.ldap.search.ldapurl=).*|\1$SEARCH_LDAP_CONNECTION://$SEARCH_LDAP_URL|
                s|^(ldape.cfg.property.service.ce.ldap.search.ldaptype=).*|\1$SEARCH_LDAP_TYPE|
                s|^(ldape.cfg.property.service.ce.ldap.search.ldapbinddn=).*|\1$SEARCH_LDAP_BIND_DN|
                s|^(ldape.cfg.property.service.ce.ldap.search.ldapbinddn.password=).*|\1$SEARCH_LDAP_BIND_PASS|
                s|dc=strongauth,dc=com|$SERVICE_LDAP_BASEDN|" $SKFE_SOFTWARE/skce-configuration.properties > $SKCE_HOME/etc/skce-configuration.properties

        cp $SKFE_SOFTWARE/skfe-configuration.properties $SKCE_HOME/etc/skce-configuration.properties


        test "$SEARCH_GROUPRESTRICTION_TYPE"      != 'default' && echo "ldape.cfg.property.service.ce.ldap.search.grouprestriction.type=$SEARCH_GROUPRESTRICTION_TYPE" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUP_SUBTREE}x"           != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.subtree.base=$SEARCH_GROUP_SUBTREE" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_METHOD}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.method=$SEARCH_GROUPNAME_METHOD" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_PREFIX}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.prefix=$SEARCH_GROUPNAME_PREFIX" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_SUFFIX}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.suffix=$SEARCH_GROUPNAME_SUFFIX" >> $SKCE_HOME/etc/skce-configuration.properties

        cp $SKFE_SOFTWARE/jets3t.properties $SKCE_HOME/etc
        cp $SKFE_SOFTWARE/$PING_FILE $SKCE_HOME/etc
        cp $SKFE_SOFTWARE/signingkeystore.bcfks $SKFE_SOFTWARE/signingtruststore.bcfks $SKCE_HOME/keystores

        chkconfig postfix on
        service postfix start
fi

##### MariaDB #####
if [ $INSTALL_MARIA = 'Y' ]; then
        echo "Installing MariaDB..." | tee -a $LOGNAME
        if [ $SHOWALL ]; then
                tar zxvf $SKFE_SOFTWARE/$MARIA -C $STRONGAUTH_HOME
        else
                tar zxf $SKFE_SOFTWARE/$MARIA -C $STRONGAUTH_HOME
        fi

        rpm -ivh $SKFE_SOFTWARE/$JEMALLOC &> /dev/null
        sed -i 's|^mysqld_ld_preload=$|mysqld_ld_preload=/usr/lib64/libjemalloc.so.1|' $STRONGAUTH_HOME/$MARIAVER/bin/mysqld_safe
        cp $STRONGAUTH_HOME/$MARIAVER/support-files/mysql.server /etc/init.d/mysqld
        chmod 755 /etc/init.d/mysqld
        chkconfig --add mysqld
        mkdir $STRONGAUTH_HOME/$MARIAVER/backups $STRONGAUTH_HOME/$MARIAVER/binlog $STRONGAUTH_HOME/$MARIAVER/log $STRONGAUTH_HOME/$MARIAVER/ibdata
        mv $STRONGAUTH_HOME/$MARIAVER $STRONGAUTH_HOME/$MARIATGT

        DBSIZE=10M
        SERVER_BINLOG=$STRONGAUTH_HOME/$MARIATGT/binlog/skfe-binary-log

        cat > /etc/my.cnf <<-EOFMYCNF
	[client]
	socket                          = /usr/local/strongauth/$MARIATGT/log/mysqld.sock

	[mysqld]
	user                            = strongauth
	lower_case_table_names          = 1
	log-bin                         = $SERVER_BINLOG

	[server]
	basedir                         = /usr/local/strongauth/$MARIATGT
	datadir                         = /usr/local/strongauth/$MARIATGT/ibdata
	pid-file                        = /usr/local/strongauth/$MARIATGT/log/mysqld.pid
	socket                          = /usr/local/strongauth/$MARIATGT/log/mysqld.sock
	general_log                     = 0
	general_log_file                = /usr/local/strongauth/$MARIATGT/log/mysqld.log
	log-error                       = /usr/local/strongauth/$MARIATGT/log/mysqld-error.log
	innodb_data_home_dir            = /usr/local/strongauth/$MARIATGT/ibdata
	innodb_data_file_path           = ibdata01:$DBSIZE:autoextend
	innodb_flush_method             = O_DIRECT
	innodb_buffer_pool_size         = ${BUFFERPOOLSIZE}
	innodb_log_file_size            = 2G
	innodb_log_buffer_size          = 5M
	innodb_flush_log_at_trx_commit  = 1
	sync_binlog                     = 1
	lower_case_table_names          = 1
	max_connections                 = 1000
	thread_cache_size               = 1000
	expire_logs_days                = 10
	EOFMYCNF
fi

##### Glassfish #####
if [ $INSTALL_GLASSFISH = 'Y' ]; then
        echo "Installing Glassfish..." | tee -a $LOGNAME
        if [ $SHOWALL ]; then
                unzip $SKFE_SOFTWARE/$GLASSFISH -d $STRONGAUTH_HOME
        else
                unzip $SKFE_SOFTWARE/$GLASSFISH -d $STRONGAUTH_HOME > /dev/null
        fi

        if [ -d /root/.gfclient ]; then
                rm -rf /root/.gfclient
        fi

        if [ -d $STRONGAUTH_HOME/.gfclient ]; then
                rm -rf $STRONGAUTH_HOME/.gfclient
        fi

        cp $SKFE_SOFTWARE/glassfishd /etc/init.d
        chmod 755 /etc/init.d/glassfishd
        chkconfig --add glassfishd

        $JAVA_HOME/bin/keytool -genkeypair -alias skfe -keystore $GLASSFISH_CONFIG/keystore.jks -storepass changeit -keypass changeit -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3562 -dname "CN=$(hostname),OU=\"StrongAuth KeyAppliance\",O=\"$COMPANY\"" &>/dev/null
        $JAVA_HOME/bin/keytool -changealias -alias s1as -destalias s1as.original -keystore $GLASSFISH_CONFIG/keystore.jks -storepass changeit &>/dev/null
        $JAVA_HOME/bin/keytool -changealias -alias skfe -destalias s1as -keystore $GLASSFISH_CONFIG/keystore.jks -storepass changeit &>/dev/null
        sed -ri 's|^(com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=).*|\1true|
                 s|^(com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=).*|\1200000000|' $GLASSFISH_CONFIG/logging.properties
        $JAVA_HOME/bin/keytool -exportcert -alias s1as -file $STRONGAUTH_HOME/certs/$(hostname).der --keystore $GLASSFISH_CONFIG/keystore.jks -storepass changeit &>/dev/null
        $JAVA_HOME/bin/keytool -importcert -noprompt -alias $(hostname) -file $STRONGAUTH_HOME/certs/$(hostname).der --keystore $STRONGAUTH_HOME/certs/cacerts -storepass changeit &>/dev/null
        $JAVA_HOME/bin/keytool -importcert -noprompt -alias $(hostname) -file $STRONGAUTH_HOME/certs/$(hostname).der --keystore $GLASSFISH_CONFIG/cacerts.jks -storepass changeit &>/dev/null

        ##### MariaDB JDBC Driver #####
        echo "Installing JDBC Driver..." | tee -a $LOGNAME
        cp $SKFE_SOFTWARE/$MARIACONJAR $GLASSFISH_HOME/lib
        cp $SKFE_SOFTWARE/$INDEX_HTML $GLASSFISH_HOME/domains/domain1/docroot
fi

if [ $INSTALL_OPENDJ = 'Y' ]; then
        echo "Installing OpenDJ..." | tee -a $LOGNAME
        if [ $SHOWALL ]; then
                unzip $SKFE_SOFTWARE/$OPENDJ -d $STRONGAUTH_HOME
        else
                unzip $SKFE_SOFTWARE/$OPENDJ -d $STRONGAUTH_HOME > /dev/null
        fi

        mv $STRONGAUTH_HOME/$OPENDJVER $OPENDJ_HOME

        cp $SKFE_SOFTWARE/99-user.ldif $OPENDJ_HOME/template/config/schema

        export "OPENDJ_JAVA_HOME=$JAVA_HOME"
        if [ $SHOWALL ]; then
                $OPENDJ_HOME/setup --cli --acceptLicense --no-prompt \
                                   --ldifFile $SKFE_SOFTWARE/$SKCE_BASE_LDIF \
                                   --rootUserPassword $SERVICE_LDAP_BIND_PASS \
                                   --baseDN $SERVICE_LDAP_BASEDN \
                                   --hostname $(hostname) \
                                   --ldapPort 1389 \
                                   --doNotStart
        else
                $OPENDJ_HOME/setup --cli --acceptLicense --no-prompt \
                                   --ldifFile $SKFE_SOFTWARE/$SKCE_BASE_LDIF \
                                   --rootUserPassword $SERVICE_LDAP_BIND_PASS \
                                   --baseDN $SERVICE_LDAP_BASEDN \
                                   --hostname $(hostname) \
                                   --ldapPort 1389 \
                                   --doNotStart \
                                   --quiet
        fi


        sed -i '/^control-panel/s|$| -Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true|' $OPENDJ_HOME/config/java.properties
        $OPENDJ_HOME/bin/dsjavaproperties >/dev/null

        cp $SKFE_SOFTWARE/opendjd /etc/init.d/
        chmod 755 /etc/init.d/opendjd
        chkconfig --add opendjd
fi

##### Change ownership of files #####
chown -R strongauth:strongauth $STRONGAUTH_HOME
chown -R strongauth:strongauth $SKFE_SOFTWARE

##### Start OpenDJ #####
if [ $INSTALL_OPENDJ = 'Y' ]; then
        service opendjd start
        $OPENDJ_HOME/bin/dsconfig set-global-configuration-prop \
                                  --hostname $(hostname) \
                                  --port 4444 \
                                  --bindDN "cn=Directory Manager" \
                                  --bindPassword "$SERVICE_LDAP_BIND_PASS" \
                                  --set check-schema:false \
                                  --trustAll \
                                  --no-prompt
fi

##### Start MariaDB and Glassfish #####
echo -n "Creating $DBSIZE SKFE Internal Database..." | tee -a $LOGNAME
cd $STRONGAUTH_HOME/$MARIATGT
scripts/mysql_install_db --basedir=`pwd` --datadir=`pwd`/ibdata &>/dev/null
# Sleep till the database is created
bin/mysqld_safe &>/dev/null &
READY=`grep "ready for connections" $MARIA_HOME/log/mysqld-error.log | wc -l`
while [ $READY -ne 1 ]
do
        echo -n . | tee -a $LOGNAME
        sleep 3
        READY=`grep "ready for connections" $MARIA_HOME/log/mysqld-error.log | wc -l`
done
echo done | tee -a $LOGNAME
$MARIA_HOME/bin/mysql -u root mysql -e "update user set password=password('$MARIA_ROOT_PASSWORD') where user = 'root';
                                                    delete from mysql.db where host = '%';
                                                    delete from mysql.user where user = '';
                                                    create database strongkeylite;
                                                    grant all on strongkeylite.* to skles@localhost identified by '$MARIA_SKLES_PASSWORD';
                                                    flush privileges;"
cd $JADE/sql/mysql
$STRONGAUTH_HOME/$MARIATGT/bin/mysql --user=skles --password=$MARIA_SKLES_PASSWORD --database=strongkeylite --quick < create.txt

# Add server entries to SERVERS table
$STRONGAUTH_HOME/$MARIATGT/bin/mysql --user=skles --password=$MARIA_SKLES_PASSWORD --database=strongkeylite -e "insert into SERVERS values (1, '$(hostname)', 'Active', 'Both', 'Active', null, null);"
$STRONGAUTH_HOME/$MARIATGT/bin/mysql --user=skles --password=$MARIA_SKLES_PASSWORD --database=strongkeylite -e "insert into DOMAINS values (1,'SKFE','Active','Active','-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'CN=SKCE Signing Key,OU=SAKA DID 1,OU=SKCE Signing Certificate 1,O=StrongAuth Inc','https://$(hostname):8181/app.json',NULL);"

touch $STRONGAUTH_HOME/crypto/etc/crypto-configuration.properties

echo "appliance.cfg.property.serverid=1" > $STRONGAUTH_HOME/appliance/etc/appliance-configuration.properties
echo "appliance.cfg.property.enableddomains.ccspin=$CCS_DOMAINS" >> $STRONGAUTH_HOME/appliance/etc/appliance-configuration.properties
echo "appliance.cfg.property.replicate=false" >> $STRONGAUTH_HOME/appliance/etc/appliance-configuration.properties
chown -R strongauth. $STRONGAUTH_HOME/appliance

chown strongauth:strongauth $STRONGAUTH_HOME/crypto/etc/crypto-configuration.properties
service glassfishd start
sleep 5

##### Perform Glassfish Tasks #####
$GLASSFISH_HOME/bin/asadmin set server.network-config.network-listeners.network-listener.http-listener-1.enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.http.request-timeout-seconds=7200
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.ssl.ssl3-tls-ciphers=+TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,+TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,+TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,+TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,+TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,+TLS_DHE_RSA_WITH_AES_256_CBC_SHA
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.ssl.ssl2-enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.ssl.ssl3-enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.ssl.tls-enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.ssl.tls11-enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.http.trace-enabled=false
$GLASSFISH_HOME/bin/asadmin set server.network-config.protocols.protocol.http-listener-2.http.xpowered-by=false
$GLASSFISH_HOME/bin/asadmin create-jdbc-connection-pool \
        --datasourceclassname org.mariadb.jdbc.MySQLDataSource \
        --restype javax.sql.ConnectionPoolDataSource \
        --isconnectvalidatereq=true \
        --validationmethod meta-data \
        --property ServerName=localhost:DatabaseName=strongkeylite:port=3306:user=skles:password=$MARIA_SKLES_PASSWORD:DontTrackOpenResources=true \
        SKFEPool
$GLASSFISH_HOME/bin/asadmin create-jdbc-resource --connectionpoolid SKFEPool jdbc/strongkeylite
$GLASSFISH_HOME/bin/asadmin set server.resources.jdbc-connection-pool.SKFEPool.max-pool-size=1000
$GLASSFISH_HOME/bin/asadmin set server.thread-pools.thread-pool.http-thread-pool.max-thread-pool-size=1000
$GLASSFISH_HOME/bin/asadmin set server.thread-pools.thread-pool.http-thread-pool.min-thread-pool-size=10

$GLASSFISH_HOME/bin/asadmin delete-jvm-options $($GLASSFISH_HOME/bin/asadmin list-jvm-options | sed -n '/\(-XX:NewRatio\|-XX:MaxPermSize\|-XX:PermSize\|-client\|-Xmx\|-Xms\)/p' | sed 's|:|\\\\:|' | tr '\n' ':')
$GLASSFISH_HOME/bin/asadmin create-jvm-options -Djtss.tcs.ini.file=$STRONGAUTH_HOME/lib/jtss_tcs.ini:-Djtss.tsp.ini.file=$STRONGAUTH_HOME/lib/jtss_tsp.ini:-Xmx${XMXSIZE}:-Xms${XMXSIZE}:-server:-Djdk.tls.ephemeralDHKeySize=2048:-Dproduct.name="":-XX\\:-DisableExplicitGC

cat > $GLASSFISH_HOME/domains/domain1/docroot/app.json << EOFAPPJSON
{
  "trustedFacets" : [{
    "version": { "major": 1, "minor" : 0 },
    "ids": [
           "https://$(hostname)",
           "https://$(hostname):8181"
    ]
  }]
}
EOFAPPJSON

# Add other servers to app.json
for fqdn in $($MARIA_HOME/bin/mysql -u skles -p${MARIA_SKLES_PASSWORD} strongkeylite -B --skip-column-names -e "select fqdn from servers;"); do
        # Skip doing ourself again
        if [ "$fqdn" == "$(hostname)" ]; then
                continue
        fi
        sed -i "/^\[/a \"           https://$fqdn:8181\"," $GLASSFISH_HOME/domains/domain1/docroot/app.json
        sed -i "/^\[/a \"           https://$fqdn\"," $GLASSFISH_HOME/domains/domain1/docroot/app.json
done

chown strongauth. $GLASSFISH_HOME/domains/domain1/docroot/app.json

echo "Deploying StrongAuth KeyAppliance ..." | tee -a $LOGNAME
$GLASSFISH_HOME/bin/asadmin deploy $STRONGAUTH_HOME/jade/skfe.ear
service glassfishd stop

echo "AS_ADMIN_PASSWORD=" > /tmp/password
echo "AS_ADMIN_NEWPASSWORD=$GLASSFISH_PASSWORD" >> /tmp/password
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password change-admin-password --domain_name domain1

service glassfishd start
echo "AS_ADMIN_PASSWORD=$GLASSFISH_PASSWORD" > /tmp/password
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password enable-secure-admin --instancealias=s1as
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password set server.network-config.protocols.protocol.sec-admin-listener.ssl.ssl3-tls-ciphers=+TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,+TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,+TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,+TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,+TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,+TLS_DHE_RSA_WITH_AES_256_CBC_SHA
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password set server.network-config.protocols.protocol.sec-admin-listener.ssl.ssl2-enabled=false
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password set server.network-config.protocols.protocol.sec-admin-listener.ssl.ssl3-enabled=false
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password set server.network-config.protocols.protocol.sec-admin-listener.http.trace-enabled=false
$GLASSFISH_HOME/bin/asadmin --user admin --passwordfile /tmp/password set server.network-config.protocols.protocol.sec-admin-listener.http.xpowered-by=false
rm /tmp/password

##### Change Firewall settings #####
firewall-cmd --permanent --zone=public --add-port=8181/tcp
firewall-cmd --permanent --add-icmp-block=echo-request

systemctl restart firewalld

##### Change default startup level - no X11 #####
sed -i 's|id:5:initdefault:|id:3:initdefault:|' /etc/inittab

# Add a new alias to only the strongauth user that reads the value of the skles DB password from domain.xml for use in other aliases (see repl and mys)
echo "alias dbpass='grep \\\"password\\\" $GLASSFISH_HOME/domains/domain1/config/domain.xml | sed \"s|.*value=\\\"\(.*\)\\\".*|\1|\" | head -1'" >> $STRONGAUTH_HOME/.bashrc

su strongauth -c "$STRONGAUTH_HOME/bin/create-SKCE-Users.sh 1 Abcd1234!"

echo "Done!" | tee -a $LOGNAME

