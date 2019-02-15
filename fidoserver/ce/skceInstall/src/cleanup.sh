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
# Script to clean out the installation to start over
#
###############################################################

UNINSTALL_OPENDJ=Y
MYSQL_ROOT=BigKahuna
GLASSFISH_PASSWORD=adminadmin

LOGNAME=/root/strongauth_logs/cleanup-skce-$(date +%s)

if ! [ -d /root/strongauth_logs ]; then
        mkdir /root/strongauth_logs
fi

. /etc/bashrc

# Check that the script is run as root
if [ $UID -ne 0 ]; then
    echo "$0 must be run as root" | tee -a $LOGNAME
    exit 1
fi

echo "Undeploying software..." | tee -a $LOGNAME
echo "AS_ADMIN_PASSWORD=$GLASSFISH_PASSWORD" > /tmp/password
asadmin --user=admin --passwordfile /tmp/password undeploy skce
asadmin --user=admin --passwordfile /tmp/password undeploy skcc
asadmin --user=admin --passwordfile /tmp/password delete-jvm-options "-Dorg.owasp.esapi.resources=/usr/local/strongauth/skcc/etc"

rm /tmp/password

echo "Stopping SKCE services..." | tee -a $LOGNAME

service postfix stop
chkconfig postfix off

if [ $UNINSTALL_OPENDJ = 'Y' ]; then
        echo "Uninstalling OpenDJ..." | tee -a $LOGNAME
        service opendjd stop
        chkconfig --del opendjd
        rm /etc/rc.d/init.d/opendjd
        rm -rf /usr/local/strongauth/OpenDJ-3.0.0
fi

echo "Removing skce database..." | tee -a $LOGNAME
if ! mysql -u root -p$MYSQL_ROOT strongkeylite -e "drop table fido_keys; drop table fido_users; drop table attestation_certificates;"; then
	echo "Error removing skce tables Check mysql and if necessary manually drop skce tables 'fido_keys', 'fido_users', and 'attestation_certificates'." | tee -a $LOGNAME
fi

sed -i '/SKCE permissions/,/SKCE_COMMANDS$/d' /etc/sudoers

echo "Removing SKCE files..." | tee -a $LOGNAME
rm -rf /usr/local/strongauth/skcc
rm -rf /usr/local/strongauth/jdk1.7.0_79
rm -rf /usr/local/strongauth/skce
rm -rf /usr/local/strongauth/skcews
rm -rf /usr/local/strongauth/jade
rm -rf /usr/local/strongauth/emerald
rm -rf /usr/local/strongauth/.SKCEConsoleTool
rm /usr/local/strongauth/bin/New-SKCE-Domain-Setup.sh
rm /usr/local/strongauth/bin/SKCE-ConsoleTool.sh
rm /usr/local/strongauth/bin/Primary-SKCE-KeyCustodian-Setup-Wizard.sh

echo "Restoring original SAKA system files..." | tee -a $LOGNAME
sed -i '/skcerc/d' /etc/bashrc
rm /etc/skcerc

exit 0
