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
################################################################

. /etc/bashrc

MYSQL_SKLES_PASSWORD=$(grep \"password\" /usr/local/strongauth/payara41/glassfish/domains/domain1/config/domain.xml | sed "s|.*value=\"\(.*\)\".*|\1|" | head -1)
SERVICE_LDAP_BIND_PASS=$(sed -rn 's|^ldape.cfg.property.service.ce.ldap.ldapbinddn.password=(.*)|\1|p' /usr/local/strongauth/skce/etc/skce-configuration.properties)
SERVICE_LDAP_BASEDN=$(sed -rn 's|^ldape.cfg.property.service.ce.ldap.search.basedn=(.*)|\1|p' /usr/local/strongauth/skce/etc/skce-configuration.properties)
SAKA_DID=$1
SERVICE_LDAP_SVCUSER_PASS=$2

##########################################
##########################################

usage() {
        echo "Usage: "
        echo "${0##*/} <did> <skce-user-pass>"
        echo "Options:"
        echo "did              The SKCE did to create."
        echo "skce-user-pass   The desired password for the default ldap users that"
        echo "                 will be created."
}


if [ -z $SERVICE_LDAP_SVCUSER_PASS ] || [ -z $SAKA_DID ]; then
        usage
        exit 1
fi

SKCE_SOFTWARE=/usr/local/software/skfe
SKCE_LDIF=skce.ldif

function check_exists {
for ARG in "$@"
do
    if [ ! -f $ARG ]; then
        echo -e "\E[31m$ARG Not Found. Check to ensure the file exists in the proper location and try again."
        tput sgr0
        exit 1

    fi
done
}

# Check that the script is run as strongauth
if [ "$(whoami)" != 'strongauth' ]; then
    echo "$0 must be run as strongauth"
    exit 1
fi

check_exists $SKCE_SOFTWARE/$SKCE_LDIF 

# Check that we can find mysql
if ! [ -f $MYSQL_HOME/bin/mysql ]; then
        echo "MYSQL_HOME not set or missing. Try refreshing shell variables and try again."
        exit 1
fi

# Check if passwords are correct
if ! $MYSQL_HOME/bin/mysql -u skles -p${MYSQL_SKLES_PASSWORD} strongkeylite -e "\c" &> /dev/null; then
        >&2 echo -e "\E[31mMySQL 'skles' password is incorrect.\E[0m"
        exit 1
fi

# Check if the SAKA domain has been created
if ! $MYSQL_HOME/bin/mysql -u skles -p${MYSQL_SKLES_PASSWORD} strongkeylite -B --skip-column-names -e "select * from domains where did=$SAKA_DID;" | grep "$SAKA_DID" &> /dev/null; then
        >&2 echo -e "\E[31mYou must make a SAKA domain before you can make the SKCE domain.\E[0m"
        exit 1
fi

SLDNAME=${SERVICE_LDAP_BASEDN%%,dc*}
sed -r "s|dc=strongauth,dc=com|$SERVICE_LDAP_BASEDN|
        s|dc: strongauth|dc: ${SLDNAME#dc=}|
        s|did: .*|did: ${SAKA_DID}|
        s|did=[0-9]+,|did=${SAKA_DID},|
        s|^ou: [0-9]+|ou: ${SAKA_DID}|
        s|(domain( id)*) [0-9]*|\1 ${SAKA_DID}|
        s|userPassword: .*|userPassword: $SERVICE_LDAP_SVCUSER_PASS|" $SKCE_SOFTWARE/$SKCE_LDIF > /tmp/skce.ldif

echo "Importing default users..."
$OPENDJ_HOME/bin/ldapmodify --filename /tmp/skce.ldif \
                             --hostName $(hostname) \
                             --port 1389 \
                             --bindDN 'cn=Directory Manager' \
                             --bindPassword "$SERVICE_LDAP_BIND_PASS" \
                             --trustAll \
                             --noPropertiesFile \
                             --defaultAdd >/dev/null
#rm /tmp/skce.ldif

exit 0
