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
# Change the FQDN of an appliance in the Database
#
###############################################################

SID=$1
FQDN=$2
EPOCH=`date +%s`
USAGE="Usage: change-fqdn.sh targetSID newFQDN
---------------------------------------------
       change-fqdn.sh 1 saka204.strongauth.com
       change-fqdn.sh 2 sa01.company.local
"

# Parse User Input
if [ $# -ne 2 ]; then
        echo "$USAGE"
        exit 1
fi

num='^[0-9]+$'
if [[ ! $SID =~ $num ]]; then
        echo "SID must be a number."
        exit 1
fi

fqdn='^[0-9a-zA-Z.-]+$'
if [[ ! $FQDN =~ $fqdn ]]; then
        echo "Invalid FQDN format."
        exit 1
fi

# Give the option of generating a new certificate if changing the FQDN of this server
THIS_SID=`sed -n 's|^appliance.cfg.property.serverid=||p' /usr/local/strongauth/appliance/etc/appliance-configuration.properties`
if [ $THIS_SID -eq $SID ]; then
        echo -n "Generate a new certificate (y/n)? "
        read GENERATE

        if [ $GENERATE == y -o $GENERATE == Y ]; then

                # Try to guess keystore password, prompt if incorrect
		$JAVA_HOME/bin/keytool -list -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass changeit 2>&1 > /dev/null
		if [ $? -eq 0 ]; then
			KEYPASS=changeit
		else
			echo "Enter the keystore Password:"
			read -s KEYPASS
		fi

                $JAVA_HOME/bin/keytool -list -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS 2>&1 > /dev/null
		if [ $? -eq 1 ]; then
			echo "Invalid Password, try again"
			exit 1
		fi
		
                # Accept user input for certificate customizables
		echo -n "Enter Common Name (CN=$FQDN): "
		read INPUT_FQDN
		if [ -z $INPUT_FQDN ]; then
			INPUT_FQDN=$FQDN
		fi

		if [[ ! $INPUT_FQDN =~ $fqdn ]]; then
			echo "Invalid FQDN format."
			exit 1
		fi

		COMPANY=`$JAVA_HOME/bin/keytool -list -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS -alias s1as -v | grep "^Owner: " | sed 's|.*O=\([^,]\+\).*|\1|'`
                echo -n "Enter Organization Name for DN (O=$COMPANY):"
                read INPUT_COMPANY
		if [ -z "$INPUT_COMPANY" ]; then
			INPUT_COMPANY=$COMPANY
		fi

                # Backup original keystore
                if [ -f $GLASSFISH_HOME/domains/domain1/config/keystore.jks.org ]; then
                        cp $GLASSFISH_HOME/domains/domain1/config/keystore.jks $GLASSFISH_HOME/domains/domain1/config/keystore.jks.$EPOCH
                else
                        cp $GLASSFISH_HOME/domains/domain1/config/keystore.jks $GLASSFISH_HOME/domains/domain1/config/keystore.jks.org
                fi

                # Generate new certificate
		$JAVA_HOME/bin/keytool -delete -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS -alias s1as
                $JAVA_HOME/bin/keytool -genkeypair -alias s1as -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS -keypass $KEYPASS -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -validity 3652 -dname "CN=$INPUT_FQDN,OU=\"StrongAuth KeyAppliance\",O=$INPUT_COMPANY"
		echo "New Certificate Generated Successfully:"
		echo ""
		$JAVA_HOME/bin/keytool -list -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS -alias s1as -v
		echo ""

		if [ ! -d /usr/local/strongauth/certs ]; then
			mkdir /usr/local/strongauth/certs
		fi

		$JAVA_HOME/bin/keytool -export -keystore $GLASSFISH_HOME/domains/domain1/config/keystore.jks -storepass $KEYPASS -alias s1as -file /usr/local/strongauth/certs/$FQDN.pem.$EPOCH
		
                # Import new certificate into java truststore
		$JAVA_HOME/bin/keytool -list -keystore $GLASSFISH_HOME/domains/domain1/config/cacerts.jks -storepass changeit 2>&1 > /dev/null
		if [ $? -eq 0 ]; then
			GLASSFISHTRUSTPASS=changeit
		else
			echo "Enter the Glassfish Truststore Password:"
			read -s GLASSFISHTRUSTPASS
		fi
		$JAVA_HOME/bin/keytool -import -noprompt -keystore $GLASSFISH_HOME/domains/domain1/config/cacerts.jks -storepass $GLASSFISHTRUSTPASS -alias $FQDN.pem.$EPOCH -file /usr/local/strongauth/certs/$FQDN.pem.$EPOCH

                $JAVA_HOME/bin/keytool -list -keystore /usr/local/strongauth/certs/cacerts -storepass changeit 2>&1 > /dev/null
                if [ $? -eq 0 ]; then
                        JAVAKEYPASS=changeit
                else
                        echo "Enter the JDK Truststore Password:"
                        read -s JAVAKEYPASS
                fi
		$JAVA_HOME/bin/keytool -import -noprompt -keystore /usr/local/strongauth/certs/cacerts -storepass $JAVAKEYPASS -alias $FQDN.pem.$EPOCH -file /usr/local/strongauth/certs/$FQDN.pem.$EPOCH
        fi
fi

# Try to guess DB password, prompt if incorrect
mysql -u skles -pAbracaDabra strongkeylite -e "\c" 2> /dev/null
if [ $? -eq 0 ]; then
        DBPASS=AbracaDabra
else
        echo "Enter the Mysql 'skles' Password:"
        read -s DBPASS
fi

mysql -u skles -p$DBPASS strongkeylite -e "\c" 2> /dev/null
if [ $? -eq 1 ]; then
        echo "Invalid Password, try again"
        exit 1
fi

if [ ! -d /usr/local/strongauth/dbdumps ]; then
	mkdir /usr/local/strongauth/dbdumps
fi

# Change server information in the database
mysqldump -u skles -p$DBPASS strongkeylite servers > /usr/local/strongauth/dbdumps/sakadb.servers.db.$EPOCH
echo "Created servers table backup in /usr/local/strongauth/dbdumps/sakadb.servers.db"
mysqldump -u skles -p$DBPASS strongkeylite server_domains > /usr/local/strongauth/dbdumps/sakadb.server_domains.db.$EPOCH
echo "Created server_domains table backup in /usr/local/strongauth/dbdumps/sakadb.server_domains.db"

mysql -u skles -p$DBPASS strongkeylite -e "update servers set fqdn='$FQDN' where sid=$SID;"
echo "Updated servers table FQDN"

mysql -u skles -p$DBPASS strongkeylite -e "update servers set mask='`mysql -u skles -p$DBPASS strongkeylite -B --skip-column-names -e "select mask from servers where sid=$SID" | sed "s|<TargetFqdn>.*</TargetFqdn>|<TargetFqdn>$FQDN</TargetFqdn>|"`' where sid=$SID"
echo "Updated servers table Mask"

for DID in `mysql -u skles -p$DBPASS strongkeylite -B --skip-column-names -e "select did from server_domains where sid=$SID"`
do
	mysql -u skles -p$DBPASS strongkeylite -e "update server_domains set migrating_key='`mysql -u skles -p$DBPASS strongkeylite -B --skip-column-names -e "select migrating_key from server_domains where sid=$SID and did=$DID" | sed "s|<TargetFqdn>.*</TargetFqdn>|<TargetFqdn>$FQDN</TargetFqdn>|"`' where sid=$SID and did=$DID"
	echo "Update server_domains for did=$DID"
done
