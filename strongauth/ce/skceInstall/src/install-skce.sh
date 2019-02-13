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
# Must be run by 'root' from the /usr/local/software/skce
# directory AFTER having downloaded all required components:
#
# - Glassfish
# - Opendj 3.0.0
#
# ###############################################################

# Uncomment to show detailed installation process
#SHOWALL=1

##########################################
##########################################
# Change these values for this installation
GLASSFISH_PASSWORD=adminadmin
MYSQL_ROOT_PASSWORD=BigKahuna
MYSQL_SKLES_PASSWORD=AbracaDabra

## SAKA Cluster Configurations ##
SAKA_HOST1=$(hostname):8181
#SAKA_HOST2=
#SAKA_HOST3=
#SAKA_HOST4=
SAKA_HOSTS=( $SAKA_HOST1 )
#SAKA_HOSTS=( $SAKA_HOST1 $SAKA_HOST2 $SAKA_HOST3 $SAKA_HOST4 )

## Upgrade Only ##
SAKA_DID=2
SAKA_USER=encryptdecrypt
SAKA_PASS=Abcd1234!
SAKA_PINGUSER_PASS=Abcd1234!
## End Upgrade Only ##

## CryptoModule Configurations ##
## For Signing XML ##
CRYPTOMODULE_TYPE=sunjce                       # Other option is utimaco

## Service Account LDAP Configuration ##
## For Authenticating to SKCE ##
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

## OAM / AD Configurations ##
AUTH_TYPE=local                                 # Other option is oam
SEARCH_GROUPRESTRICTION_TYPE=default            # Other options are subtree and groupname
SEARCH_GROUP_SUBTREE=                           # If RESTRICTION_TYPE=subtree, specify an LDAP container to start searching for valid user groups
SEARCH_GROUPNAME_METHOD=                        # If RESTRICTION_TYPE=groupname, specify whether to restrict valid user groups to those with a specfic prefix, suffix, or both
SEARCH_GROUPNAME_PREFIX=                        # If GROUPNAME_METHOD=prefix or both, specify the prefix that must be present for the group to be valid
SEARCH_GROUPNAME_SUFFIX=                        # If GROUPNAME_METHOD=suffix or both, specify the suffix that must be present for the group to be valid
GROUPS_DISPLAY_FILTER=                          # Prefix characters to hide within the SKCC GUI when selecting a group
HEADER1=                                        # The name of the header provided by OAM for the UserDN, MUST BE LOWERCASE
HEADER2=                                        # The name of the header provided by OAM for the UID, MUST BE LOWERCASE

## CryptoCabinet Properties ##
UNIQUE_KEY=false                                # Whether each encrypted file has a shared encryption key or a unique key per file
KEY_ALG=AES                                     # Other option is DESede
KEY_SIZE=256                                    # Other options are AES(128, 192, 256) and DESede(112, 168)
MAX_FILE_SIZE=50                                # Upper Threshold for allowable filesize in MB

## SKCC Visual Elements Configuration ##
SHOW_FULL_DN=false                              # Whether the logged in user info in the GUI includes a full DN
SHOW_ERR_CODES=false                            # Whether ERR codes are shown in the GUI
SHOW_SINGLE_GROUP=false                         # Whether the group dropdown is shown when the user is in only one group
LOGOUT_URL="https://$(hostname):8181/skcc"      # URL to redirect to when Logout button is clicked

# Name of the png file for the Company Logo, must be in the skce software dir
LOGO_FILENAME=                                  # The filename of a png file to display in the GUI. File must be located in /usr/local/software/skce

## SKCC Tab Configuration ##
## Specify false to hide the tab ##
LOCALTOLOCAL_TAB=true
LOCALTOCLOUD_TAB=false
CLOUDTOLOCAL_TAB=false
CLOUDTOCLOUD_TAB=false
SETTINGS_TAB=true

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

. /etc/bashrc

# Flags to indicate if a module should be installed
INSTALL_GLASSFISH=Y
INSTALL_OPENDJ=Y
INSTALL_SKCE=Y
INSTALL_MYSQL=Y

# Script Logging Location
LOGNAME=/root/strongauth_logs/install-skce-$(date +%s)

# Current versions
# Start Required Distributables
OPENDJ=OpenDJ-3.0.0.zip
# End Required Distributables

# Other vars
STRONGAUTH_HOME=/usr/local/strongauth
JADE=$STRONGAUTH_HOME/jade
EMERALD=$STRONGAUTH_HOME/emerald
SKCE_SOFTWARE=/usr/local/software/saka/skce
GLASSFISH_HOME=$STRONGAUTH_HOME/glassfish4/glassfish
GLASSFISH_CONFIG=$GLASSFISH_HOME/domains/domain1/config
OPENDJVER=opendj
OPENDJTGT=OpenDJ-3.0.0
OPENDJ_HOME=$STRONGAUTH_HOME/$OPENDJTGT
SKCE_HOME=$STRONGAUTH_HOME/skce
SKCEWS_HOME=$STRONGAUTH_HOME/skcews
SKCC_HOME=$STRONGAUTH_HOME/skcc

# Other files
DEFAULT_LOGO_FILENAME=str-logo.png
INDEX_HTML=index.html
SKCE=jade.tgz
SKCE_EAR=skce.ear
SKCE_LDIF=skce.ldif
SKCE_BASE_LDIF=skce-base.ldif
SKCE_SQL=skce.sql
SKCC=emerald.tgz
CRYPTOCABINET_WAR=skcc.war
PING_FILE=abc.txt
CSCONFIG=CryptoServer.cfg
SKCE_SETUP_WIZARD=Primary-SKCE-KeyCustodian-Setup-Wizard.sh
SKCE_CONSOLE_TOOL=SKCE-ConsoleTool.sh

function check_exists {
for ARG in "$@"
do
    if [ ! -f $ARG ]; then
        echo -e "\E[31m$ARG Not Found. Check to ensure the file exists in the proper location and try again." | tee -a $LOGNAME
        tput sgr0
        exit 1

    fi
done
}

if ! [ -d /root/strongauth_logs ]; then
        mkdir /root/strongauth_logs
fi

if ! $(id strongauth &> /dev/null); then
        echo "$(basename $0) requires a 'strongauth' user to already exist. Run install-saka.sh and try again." | tee -a $LOGNAME
        exit 1
fi

# Overwrite glassfish admin password
if ! [ -z "$1" ]; then
       GLASSFISH_PASSWORD="$1"
fi

# Overwrite mysql root password
if ! [ -z "$2" ]; then
        MYSQL_ROOT_PASSWORD="$2"
fi

# Overwrite mysql skles password
if ! [ -z "$3" ]; then
        MYSQL_SKLES_PASSWORD="$3"
fi

# Check that the script is run as root
if [ $UID -ne 0 ]; then
    echo "$0 must be run as root" | tee -a $LOGNAME
    exit 1
fi

# Check that hostname is resolvable
if which nc &> /dev/null; then
        if ! nc -zvw 2 $(hostname) 22 &> /dev/null; then
                echo "FQDN $(hostname) not resolvable. Modify DNS or add a hosts entry and try again." | tee -a $LOGNAME
                exit 1
        fi
fi

# Check that all files are present
if [ $INSTALL_OPENDJ = 'Y' ]; then
        check_exists $SKCE_SOFTWARE/$OPENDJ
        check_exists $SKCE_SOFTWARE/$SKCE_LDIF 
        check_exists $SKCE_SOFTWARE/$SKCE_BASE_LDIF 
        check_exists $SKCE_SOFTWARE/99-user.ldif
        check_exists $SKCE_SOFTWARE/opendjd
fi

if [ $INSTALL_GLASSFISH = 'Y' ]; then
        check_exists $SKCE_SOFTWARE/$INDEX_HTML
fi

if [ $INSTALL_MYSQL = 'Y' ]; then
	if ! [ -f $MYSQL_HOME/bin/mysql ]; then
		echo "MYSQL_HOME not set or missing. Try refreshing shell variables and try again." | tee -a $LOGNAME
		exit 1
	fi
        check_exists $SKCE_SOFTWARE/$SKCE_SQL
fi

if [ $INSTALL_SKCE = 'Y' ]; then
        check_exists $SKCE_SOFTWARE/skce-configuration.properties
        check_exists $SKCE_SOFTWARE/skcc-configuration.properties $SKCE_SOFTWARE/jets3t.properties 
        check_exists $SKCE_SOFTWARE/skcc-help.properties $SKCE_SOFTWARE/skcc-help_en_US.properties $SKCE_SOFTWARE/skcc-help_fr_FR.properties
        check_exists $SKCE_SOFTWARE/validation.properties $SKCE_SOFTWARE/ESAPI.properties 
        check_exists $SKCE_SOFTWARE/$PING_FILE
        check_exists $SKCE_SOFTWARE/$CSCONFIG 
        check_exists $SKCE_SOFTWARE/$SKCE_SETUP_WIZARD
        check_exists $SKCE_SOFTWARE/$SKCE_CONSOLE_TOOL
        check_exists $SKCE_SOFTWARE/$DEFAULT_LOGO_FILENAME
        check_exists $SKCE_SOFTWARE/$SKCC $SKCE_SOFTWARE/$SKCE
fi

# Check if passwords are correct
if ! $MYSQL_HOME/bin/mysql -u skles -p${MYSQL_SKLES_PASSWORD} strongkeylite -e "\c" &> /dev/null; then
        >&2 echo -e "\E[31mMySQL 'skles' password is incorrect.\E[0m" | tee -a $LOGNAME
        exit 1
fi

if ! $MYSQL_HOME/bin/mysql -u root -p${MYSQL_ROOT_PASSWORD} strongkeylite -e "\c" &> /dev/null; then
        >&2 echo -e "\E[31mMySQL 'root' password is incorrect.\E[0m" | tee -a $LOGNAME
        exit 1
fi

if $MYSQL_HOME/bin/mysql -u skles -p${MYSQL_SKLES_PASSWORD} strongkeylite -B --skip-column-names -e "select * from domains where did=$SAKA_DID" | grep "$SAKA_DID" &> /dev/null; then
        CREATE_SKCE_DOMAIN=true
else
        CREATE_SKCE_DOMAIN=false
fi

# Add SKCE permissions to /etc/sudoers
cat >> /etc/sudoers <<-EOFSUDOERS
## SKCE permissions
Cmnd_Alias SKCE_COMMANDS = /sbin/service opendjd start, /sbin/service opendjd stop, /sbin/service opendjd restart
strongauth ALL=SKCE_COMMANDS
EOFSUDOERS

# Make needed directories
mkdir -p $STRONGAUTH_HOME/certs $SKCE_HOME/etc $SKCE_HOME/keystores $SKCE_HOME/so $SKCE_HOME/kc1 $SKCE_HOME/kc2 $SKCEWS_HOME/engine_in $SKCEWS_HOME/engine_out $SKCC_HOME/etc

##### OPENDJ #####
if [ $INSTALL_OPENDJ = 'Y' ]; then
        echo "Installing OpenDJ..." | tee -a $LOGNAME
        if [ $SHOWALL ]; then
                unzip $SKCE_SOFTWARE/$OPENDJ -d $STRONGAUTH_HOME
        else
                unzip $SKCE_SOFTWARE/$OPENDJ -d $STRONGAUTH_HOME > /dev/null
        fi

        mv $STRONGAUTH_HOME/$OPENDJVER $OPENDJ_HOME

        cp $SKCE_SOFTWARE/99-user.ldif $OPENDJ_HOME/template/config/schema

        export "OPENDJ_JAVA_HOME=$JAVA_HOME"
        if [ $SHOWALL ]; then
                $OPENDJ_HOME/setup --cli --acceptLicense --no-prompt \
                                   --ldifFile $SKCE_SOFTWARE/$SKCE_BASE_LDIF \
                                   --rootUserPassword $SERVICE_LDAP_BIND_PASS \
                                   --baseDN $SERVICE_LDAP_BASEDN \
                               	   --hostname $(hostname) \
                                   --ldapPort 1389 \
                                   --doNotStart 
        else
                $OPENDJ_HOME/setup --cli --acceptLicense --no-prompt \
                                   --ldifFile $SKCE_SOFTWARE/$SKCE_BASE_LDIF \
                                   --rootUserPassword $SERVICE_LDAP_BIND_PASS \
                                   --baseDN $SERVICE_LDAP_BASEDN \
                                   --hostname $(hostname) \
                                   --ldapPort 1389 \
                                   --doNotStart \
                                   --quiet
        fi
        
        cp $SKCE_SOFTWARE/opendjd /etc/init.d/
        chmod 755 /etc/init.d/opendjd
        chkconfig --add opendjd
        sed -i 's|^\(UNINSTALL_OPENDJ=\).|\1Y|' $SKCE_SOFTWARE/cleanup.sh
else
        sed -i 's|^\(UNINSTALL_OPENDJ=\).|\1N|' $SKCE_SOFTWARE/cleanup.sh
fi

##### Install SKCE #####
if [ $INSTALL_SKCE = 'Y' ]; then

        if [ $SHOWALL ]; then
                tar zxvf $SKCE_SOFTWARE/$SKCE -C $STRONGAUTH_HOME
        else
                tar zxf $SKCE_SOFTWARE/$SKCE -C $STRONGAUTH_HOME
        fi

        if [ $SHOWALL ]; then
                tar zxvf $SKCE_SOFTWARE/$SKCC -C $STRONGAUTH_HOME
        else
                tar zxf $SKCE_SOFTWARE/$SKCC -C $STRONGAUTH_HOME
        fi

        SERVICE_LDAP_USERNAME=$(sed -r 's|^[cC][nN]=([^,]*),.*|\1|' <<< "$SERVICE_LDAP_SVCUSER_DN")
        SERVICE_LDAP_SUFFIX=$(sed -r 's|^[cC][nN]=[^,]*(,.*)|\1|' <<< "$SERVICE_LDAP_SVCUSER_DN")

        SERVICE_LDAP_PINGUSER=$(sed -r 's|^[cC][nN]=([^,]*),.*|\1|' <<< "$SERVICE_LDAP_PINGUSER_DN")
        SERVICE_LDAP_PINGUSER_SUFFIX=$(sed -r 's|^[cC][nN]=[^,]*(,.*)|\1|' <<< "$SERVICE_LDAP_PINGUSER_DN")

        if [ "${SERVICE_LDAP_SUFFIX}" != "${SERVICE_LDAP_PINGUSER_SUFFIX}" ]; then
		echo "Warning: SERVICE_LDAP_USER and SERVICE_LDAP_PINGUSER must be in the same OU. Pinguser may not authenticate as expected. Run update-ldap-config with corrected users."
	fi

        sed -r "s|^(skce.cfg.property.saka.cluster.1.domain.1.id=).*|\1$SAKA_DID|
                s|^(skce.cfg.property.saka.cluster.1.domain.1.username=).*|\1$SAKA_USER|
                s|^(skce.cfg.property.saka.cluster.1.domain.1.password=).*|\1$SAKA_PASS|
                s|^(ldape.cfg.property.service.ce.ldap.ldapservicegroup=).*|\1$SERVICE_LDAP_SVCUSER_GROUP|
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
                s|dc=strongauth,dc=com|$SERVICE_LDAP_BASEDN|" $SKCE_SOFTWARE/skce-configuration.properties > $STRONGAUTH_HOME/skce/etc/skce-configuration.properties
        
        test "$CRYPTOMODULE_TYPE"                 == 'utimaco' && echo 'skce.cfg.property.cryptomodule.type=hsm' >> $SKCE_HOME/etc/skce-configuration.properties
        test "$CRYPTOMODULE_TYPE"                 == 'utimaco' && echo 'skce.cfg.property.cryptomodule.vendor=utimaco' >> $SKCE_HOME/etc/skce-configuration.properties
        test "$CRYPTOMODULE_TYPE"                 == 'utimaco' && echo 'skse.cfg.property.dsig.jceprovider=CryptoServer' >> $SKCE_HOME/etc/skce-configuration.properties

        test "$SEARCH_GROUPRESTRICTION_TYPE"      != 'default' && echo "ldape.cfg.property.service.ce.ldap.search.grouprestriction.type=$SEARCH_GROUPRESTRICTION_TYPE" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUP_SUBTREE}x"           != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.subtree.base=$SEARCH_GROUP_SUBTREE" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_METHOD}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.method=$SEARCH_GROUPNAME_METHOD" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_PREFIX}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.prefix=$SEARCH_GROUPNAME_PREFIX" >> $SKCE_HOME/etc/skce-configuration.properties
        test "${SEARCH_GROUPNAME_SUFFIX}x"        != 'x' && echo "ldape.cfg.property.service.ce.ldap.search.groupname.suffix=$SEARCH_GROUPNAME_SUFFIX" >> $SKCE_HOME/etc/skce-configuration.properties

        numOfSAKA=${#SAKA_HOSTS[@]}
        sed -i "s|^skce.cfg.property.saka.cluster.1.hosturls.count=.*|skce.cfg.property.saka.cluster.1.hosturls.count=${numOfSAKA}|" $SKCE_HOME/etc/skce-configuration.properties
        sed -i "/skce.cfg.property.saka.cluster.1.hosturl\./d" $SKCE_HOME/etc/skce-configuration.properties
        for (( i=1; i<${numOfSAKA}+1; i++ ));
        do
                sed -i "/^skce.cfg.property.saka.cluster.1.hosturls.count/i skce.cfg.property.saka.cluster.1.hosturl.$i=https://${SAKA_HOSTS[$i-1]}" $SKCE_HOME/etc/skce-configuration.properties
        done

        sed -r "s|^(skcc.cfg.property.hostport=).*|\1https://$(hostname):8181|
                s|^(skcc.cfg.property.defaultdid=).*|\1$SAKA_DID|
                s|^(skcc.cfg.property.service.cc.ce.username=).*|\1$SERVICE_LDAP_USERNAME|
                s|^(skcc.cfg.property.service.cc.ce.password=).*|\1$SERVICE_LDAP_SVCUSER_PASS|
                s|^(skcc.cfg.property.service.cc.fe.password=).*|\1$SERVICE_LDAP_SVCUSER_PASS|
                s|^(skcc.cfg.property.service.cc.ce.ping.username=).*|\1$SERVICE_LDAP_PINGUSER|
                s|^(skcc.cfg.property.service.cc.ce.ping.password=).*|\1$SERVICE_LDAP_PINGUSER_PASS|
                s|^(skcc.cfg.property.sakahostport=).*|\1https://$(hostname):8181|
                s|^(skcc.cfg.property.sakapwd=).*|\1$SAKA_PINGUSER_PASS|
                s|dc=strongauth,dc=com|$SERVICE_LDAP_BASEDN|
                s|^(ldape.cfg.property.service.cc.ldap.ldapurl=).*|\1$SEARCH_LDAP_CONNECTION://$SEARCH_LDAP_URL|
                s|^(ldape.cfg.property.service.cc.ldap.ldaptype=).*|\1$SEARCH_LDAP_TYPE|
                s|^(ldape.cfg.property.service.cc.ldap.ldapbinddn=).*|\1$SEARCH_LDAP_BIND_DN|
                s|^(ldape.cfg.property.service.cc.ldap.ldapbinddn.password=).*|\1$SEARCH_LDAP_BIND_PASS|
                s|^(skcc.cfg.property.logout.url=).*|\1$LOGOUT_URL|
                s|^(skcc.cfg.property.mailhost.type=).*|\1$MAILHOST_TYPE|
                s|^(skcc.cfg.property.mailhost=).*|\1$MAILHOST_URL|
                s|^(skcc.cfg.property.mail.smtp.port=).*|\1$MAILHOST_PORT|
                s|^(skcc.cfg.property.mail.smtp.from=).*|\1$MAILHOST_FROM|
                s|^(skcc.cfg.property.smtp.auth.user=).*|\1$MAILHOST_USER|
                s|^(skcc.cfg.property.smtp.auth.password=).*|\1$MAILHOST_PASS|" $SKCE_SOFTWARE/skcc-configuration.properties > $SKCC_HOME/etc/skcc-configuration.properties

        test $AUTH_TYPE        == 'oam' && echo 'skcc.cfg.property.auth.mechanism=oam' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $AUTH_TYPE        == 'oam' && echo 'skcc.cfg.property.groupretrieve.mechanism=oam' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $KEY_ALG          != 'AES' && echo "skcc.cfg.property.defaultenckeyalgorithm=$KEY_ALG" >> $SKCC_HOME/etc/skcc-configuration.properties
        test $KEY_SIZE         != '128' && echo "skcc.cfg.property.defaultaesenckeysize=$KEY_SIZE" >> $SKCC_HOME/etc/skcc-configuration.properties
        test $MAX_FILE_SIZE    != '10' && echo "skcc.esapi.file.maxsize.mb=$MAX_FILE_SIZE" >> $SKCC_HOME/etc/skcc-configuration.properties
        test $UNIQUE_KEY       == 'false' && echo 'skcc.cfg.property.defaultuniquekey=false' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $LOCALTOLOCAL_TAB == 'false' && echo 'skcc.cfg.property.tab.localtolocal.visible=false' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $LOCALTOCLOUD_TAB == 'false' && echo 'skcc.cfg.property.tab.localtocloud.visible=false' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $CLOUDTOLOCAL_TAB == 'false' && echo 'skcc.cfg.property.tab.cloudtolocal.visible=false' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $CLOUDTOCLOUD_TAB == 'false' && echo 'skcc.cfg.property.tab.cloudtocloud.visible=false' >> $SKCC_HOME/etc/skcc-configuration.properties
        test $SETTINGS_TAB     == 'false' && echo 'skcc.cfg.property.tab.settings.visible=false' >> $SKCC_HOME/etc/skcc-configuration.properties

        test "$SEARCH_GROUPRESTRICTION_TYPE"      != 'default' && echo "ldape.cfg.property.service.cc.ldap.grouprestriction.type=$SEARCH_GROUPRESTRICTION_TYPE" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${SEARCH_GROUP_SUBTREE}x"           != 'x' && echo "ldape.cfg.property.service.cc.ldap.subtree.base=$SEARCH_GROUP_SUBTREE" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${SEARCH_GROUPNAME_METHOD}x"        != 'x' && echo "ldape.cfg.property.service.cc.ldap.groupname.method=$SEARCH_GROUPNAME_METHOD" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${SEARCH_GROUPNAME_PREFIX}x"        != 'x' && echo "ldape.cfg.property.service.cc.ldap.groupname.prefix=$SEARCH_GROUPNAME_PREFIX" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${SEARCH_GROUPNAME_SUFFIX}x"        != 'x' && echo "ldape.cfg.property.service.cc.ldap.groupname.suffix=$SEARCH_GROUPNAME_SUFFIX" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${GROUPS_DISPLAY_FILTER}x"          != 'x' && echo "skcc.cfg.property.filter.text=$GROUPS_DISPLAY_FILTER" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${HEADER1}x"                        != 'x' && echo "skcc.cfg.property.header.key.userdn=$HEADER1" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "${HEADER2}x"                        != 'x' && echo "skcc.cfg.property.header.key.userid=$HEADER2" >> $SKCC_HOME/etc/skcc-configuration.properties

        test "$SHOW_FULL_DN"                      != 'false' && echo "skcc.cfg.property.ui.show.userdn=false" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "$SHOW_ERR_CODES"                    != 'false' && echo "skcc.cfg.property.ui.show.codes=false" >> $SKCC_HOME/etc/skcc-configuration.properties
        test "$SHOW_SINGLE_GROUP"                 != 'false' && echo "skcc.cfg.property.ui.show.dropdown=false" >> $SKCC_HOME/etc/skcc-configuration.properties

        mkdir $SKCC_HOME/etc/images
        if [ -f $SKCE_SOFTWARE/$LOGO_FILENAME ]; then
                echo "skcc.cfg.property.logo.default=$LOGO_FILENAME" >> $SKCC_HOME/etc/skcc-configuration.properties
                cp $SKCE_SOFTWARE/$LOGO_FILENAME $SKCC_HOME/etc/images
        else
                if [ "${LOGO_FILENAME}x" != "x" ]; then
                        echo "$LOGO_FILENAME not found! Using default logo." | tee -a $LOGNAME
                fi
                cp $SKCE_SOFTWARE/$DEFAULT_LOGO_FILENAME $SKCC_HOME/etc/images
                cp $SKCE_SOFTWARE/verificationKeyImage.png $SKCC_HOME/etc/images
        fi

        cp $SKCE_SOFTWARE/$CSCONFIG $SKCE_HOME/etc
        cp $SKCE_SOFTWARE/$SKCE_SETUP_WIZARD $STRONGAUTH_HOME/bin
        cp $SKCE_SOFTWARE/$SKCE_CONSOLE_TOOL $STRONGAUTH_HOME/bin
        cp $SKCE_SOFTWARE/create-SKCE-Users.sh $STRONGAUTH_HOME/bin
        cp $SKCE_SOFTWARE/New-SKCE-Domain-Setup.sh $STRONGAUTH_HOME/bin

        cp $SKCE_SOFTWARE/jets3t.properties $SKCC_HOME/etc
        cp $SKCE_SOFTWARE/jets3t.properties $SKCE_HOME/etc
        cp $SKCE_SOFTWARE/skcc-help.properties $SKCE_SOFTWARE/skcc-help_en_US.properties $SKCE_SOFTWARE/skcc-help_fr_FR.properties $SKCC_HOME/etc
        cp $SKCE_SOFTWARE/validation.properties $SKCE_SOFTWARE/ESAPI.properties $SKCC_HOME/etc
        cp $SKCE_SOFTWARE/$PING_FILE $SKCE_HOME/etc

        cp $SKCE_SOFTWARE/$INDEX_HTML $GLASSFISH_HOME/domains/domain1/docroot
        if [ "$CRYPTOMODULE_TYPE" == 'utimaco' ]; then
                cp $STRONGAUTH_HOME/strongkeylite/etc/ADMIN.key $SKCE_HOME/etc 
        fi
        chkconfig postfix on
        service postfix start
fi

##### Change ownership of files #####
chown -R strongauth:strongauth $JADE
chown -R strongauth:strongauth $EMERALD
chown -R strongauth:strongauth $STRONGAUTH_HOME/glassfish4
chown -R strongauth:strongauth $STRONGAUTH_HOME/bin
chown -R strongauth:strongauth $OPENDJ_HOME
chown -R strongauth:strongauth $SKCE_SOFTWARE
chown -R strongauth:strongauth $SKCE_HOME $SKCEWS_HOME $SKCC_HOME $STRONGAUTH_HOME/certs

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
        if [ "$CREATE_SKCE_DOMAIN" == 'true' ]; then
                SLDNAME=${SERVICE_LDAP_BASEDN%%,dc*}
                sed -r "s|dc=strongauth,dc=com|$SERVICE_LDAP_BASEDN|
                        s|dc: strongauth|dc: ${SLDNAME#dc=}|
                        s|did: .*|did: ${SAKA_DID}|
                        s|did=[0-9]+,|did=${SAKA_DID},|
                        s|^ou: [0-9]+|ou: ${SAKA_DID}|
                        s|(domain( id)*) [0-9]*|\1 ${SAKA_DID}|
                        s|userPassword: .*|userPassword: $SERVICE_LDAP_SVCUSER_PASS|" $SKCE_SOFTWARE/$SKCE_LDIF > /tmp/skce.ldif

                $OPENDJ_HOME/bin/ldapmodify --filename /tmp/skce.ldif \
                                            --hostName $(hostname) \
                                            --port 1389 \
                                            --bindDN 'cn=Directory Manager' \
                                            --bindPassword "$SERVICE_LDAP_BIND_PASS" \
                                            --trustAll \
                                            --noPropertiesFile \
                                            --defaultAdd >/dev/null
                rm /tmp/skce.ldif
        fi
fi

# Get server id
grep '^strongkeylite.cfg.property.serverid' $STRONGAUTH_HOME/strongkeylite/etc/strongkeylite-configuration.properties | sed 's|strongkeylite|skce|' >> $SKCE_HOME/etc/skce-configuration.properties

##### Install Mysql #####
if [ $INSTALL_MYSQL = 'Y' ]; then
        echo "Creating Mysql Schema..." | tee -a $LOGNAME
        $MYSQL_HOME/bin/mysql -u root -p$MYSQL_ROOT_PASSWORD strongkeylite -e "source $SKCE_SOFTWARE/$SKCE_SQL;"
fi	

echo "AS_ADMIN_PASSWORD=$GLASSFISH_PASSWORD" > /tmp/password
$GLASSFISH_HOME/bin/asadmin --user=admin --passwordfile /tmp/password create-jvm-options -Dorg.owasp.esapi.resources=$SKCC_HOME/etc
echo "Deploying CryptoEngine..." | tee -a $LOGNAME
$GLASSFISH_HOME/bin/asadmin --user=admin --passwordfile /tmp/password deploy $JADE/$SKCE_EAR
echo "Deploying CryptoCabinet..." | tee -a $LOGNAME
$GLASSFISH_HOME/bin/asadmin --user=admin --passwordfile /tmp/password deploy $EMERALD/$CRYPTOCABINET_WAR
rm /tmp/password

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
for fqdn in $($MYSQL_HOME/bin/mysql -u skles -p${MYSQL_SKLES_PASSWORD} strongkeylite -B --skip-column-names -e "select fqdn from servers;"); do
        # Skip doing ourself again
        if [ "$fqdn" == "$(hostname)" ]; then
                continue
        fi
        sed -i "/^\[/a \"           https://$fqdn:8181\"," $GLASSFISH_HOME/domains/domain1/docroot/app.json
        sed -i "/^\[/a \"           https://$fqdn\"," $GLASSFISH_HOME/domains/domain1/docroot/app.json
done

cp $GLASSFISH_HOME/domains/domain1/docroot/app.json $SKCE_SOFTWARE
chown strongauth. $GLASSFISH_HOME/domains/domain1/docroot/app.json

##### Create skcerc #####
cat > /etc/skcerc << EOFSKCERC
  export OPENDJ_JAVA_HOME=\$JAVA_HOME
       export OPENDJ_HOME=$OPENDJ_HOME
              export PATH=\$OPENDJ_HOME/bin:\$PATH

alias jade='cd $JADE'
alias emrd='cd $EMERALD'
alias skce='cd $SKCE_HOME/etc'
alias skcc='cd $SKCC_HOME/etc'
EOFSKCERC

mkdir $STRONGAUTH_HOME/.SKCEConsoleTool

CRYPTOMODULE=HSM
if [ "$CRYPTOMODULE_TYPE" == 'sunjce' ]; then
        CRYPTOMODULE=TPM
fi

cat > $STRONGAUTH_HOME/.SKCEConsoleTool/appProperties <<-EOFSCPROP
----- StrongKey CryptoEngine Console Tool Preferences -----
#`date`
com.strongauth.skce.consoletool.keystorelocation=
com.strongauth.skce.consoletool.cryptomodule=$CRYPTOMODULE
com.strongauth.skce.consoletool.kcrole=Security Officer
com.strongauth.skce.consoletool.skceurls=https\://$(hostname)\:8181
EOFSCPROP

chown -R strongauth. $STRONGAUTH_HOME/.SKCEConsoleTool

echo ". /etc/skcerc" >> /etc/bashrc

exit 0
