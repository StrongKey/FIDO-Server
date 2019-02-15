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
# Script to clean out the installation to start over; note that
# if a Utimaco HSM is being used, the HSM keys and users cannot 
# be deleted due to security controls (Administrator smartcards)
# and must be cleaned up manually.  
#
###############################################################

LOGNAME=/root/strongauth_logs/cleanup-saka-$(date +%s)
IFS="
"

if ! [ -d /root/strongauth_logs ]; then
        mkdir /root/strongauth_logs
fi

echo "Stopping SAKA services..." | tee -a $LOGNAME
service mysqld restart 
service mysqld stop 
service glassfishd stop 

if [ -f /etc/rc.d/init.d/opendjd ]; then
	echo "Uninstalling OpenDJ..." | tee -a $LOGNAME
        service opendjd stop
        chkconfig --del opendjd
        rm /etc/rc.d/init.d/opendjd
fi

echo "Restoring original system files..." | tee -a $LOGNAME
sed -i '/skferc/d' /etc/bashrc
sed -i '/\/usr\/local\/strongauth\/lib/d' /etc/ld.so.conf
cp /etc/org/sudoers /etc
cp /etc/org/inittab /etc

echo "Removing SAKA configuration files..." | tee -a $LOGNAME
chkconfig --del mysqld
chkconfig --del glassfishd

rm /etc/my.cnf
rm /etc/skferc
rm /etc/rc.d/init.d/mysqld
rm /etc/rc.d/init.d/glassfishd
if [ -f /var/spool/cron/strongauth ]; then
        rm /var/spool/cron/strongauth
fi

echo "Removing User..." | tee -a $LOGNAME
userdel -r strongauth

firewall-cmd --permanent --zone=public --remove-port=8181/tcp
firewall-cmd --permanent --remove-icmp-block=echo-request
systemctl restart firewalld

if $(id strongauth &> /dev/null); then
	echo -e "\E[31m'strongauth' user not fully removed. Kill all processes owned by 'strongauth' and try again." | tee -a $LOGNAME
        tput sgr0
        exit 1
fi

echo "Done!" | tee -a $LOGNAME

exit 0
