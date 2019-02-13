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
# Install a new server certificate to glassfish from a provided p12 file
#
###############################################################

#### defaults ####
keystore=$1
destkeystore=$2
destpass=changeit
storetype=PKCS12
timestamp=$(date +%s)
usage="Usage: $(basename $0) keystore <destination keystore>"

# Must be run as strongauth
if [ "$(whoami)" != 'strongauth' ]; then
        echo -e "\E[31mMust be run as 'strongauth' user\E[0m"
        exit 1
fi

# If the keystore argument isn't passed
if [ -z $keystore ]; then
        echo -e "\E[31mMissing Argument: keystore\E[0m"
        echo "$usage"
        exit 1
fi

# If the keystore doesn't exist
if ! [ -f $keystore ]; then
        echo -e "\E[31mKeystore Not Found: $keystore\E[0m"
        echo "$usage"
        exit 1
fi

read -p "Enter keystore password for $(basename "$keystore"): " -rs password
echo

echo "Verifying password..."
attempts=0
while ! keytool -list -keystore "$keystore" -storetype "$storetype" -storepass "$password" &> /dev/null; do
        attempts=$(expr $attempts + 1)
        if [ $attempts -eq 4 ]; then
                echo 'Three incorrect attempts; try again.'
                exit 1
        fi
        read -p "Keystore password incorrect for $(basename "$keystore"). Enter keystore password: " -rs password
        echo
done

# If the destination keystore argument is passed, does the keystore exist?
if [ -n "$destkeystore" ] && [ -f "$destkeystore" ]; then
	# Guess the password, else ask for it
	if ! keytool -list -keystore "$destkeystore" -storepass "$destpass" &> /dev/null; then
		read -p "Enter keystore password for $(basename "$destkeystore"): " -rs destpass
		echo

		echo "Verifying password..."
		attempts=0
		while ! keytool -list -keystore "$destkeystore" -storetype JKS -storepass "$destpass" &> /dev/null; do
			attempts=$(expr $attempts + 1)
			if [ $attempts -eq 4 ]; then
				echo 'Three incorrect attempts; try again.'
				exit 1
			fi
			read -p "Keystore password incorrect for $(basename "$destkeystore"). Enter keystore password: " -rs destpass
			echo
		done
	fi
fi

echo "Exporting keypair..."
alias=$(keytool -list -keystore $keystore -storepass $password -storetype "$storetype" | grep PrivateKeyEntry | sed "s|,.*||")

# Create a temporary keystore that contains the keypair and certificate from the p12 into the format glassfish expects (keystore and key passwords changeit, alias s1as, keystore name keystore.jks)
keytool -importkeystore -srckeystore "$keystore" -srcstoretype "$storetype" -srcstorepass "$password" -srckeypass "$password" -destkeystore /tmp/keystore.jks.$timestamp -deststoretype JKS -deststorepass "$destpass" -destkeypass "$destpass" -alias "$alias" -destalias s1as 

if ! [ -f /tmp/keystore.jks.$timestamp ]; then
        echo -e "\E[31mExport Failed\E[0m"
        exit 1
fi

openssl pkcs12 -nokeys -clcerts -passin pass:"$password" -in $keystore 2> /dev/null | sed -n '/BEGIN CERTIFICATE/,/END CERTIFICATE/p' > /usr/local/strongauth/certs/$(hostname).pem

echo "Found End-Entity certificate..."
openssl x509 -text -in /usr/local/strongauth/certs/"$(hostname)".pem | sed -n '1,/Subject Public Key Info/p' | sed "$ d"
unset ANSWER
read -p "Trust this cert? (y/n): " -r ANSWER
if [ "$ANSWER" != 'y' ]; then
	echo "Cancelling p12 install"
	exit 1
fi

# Import new cert into glassfish and jdk truststores
if [ -f /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks ]; then
	keytool -delete -keystore /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias $(hostname) -noprompt &> /dev/null
	RESULT=$(keytool -import -keystore /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias $(hostname) -file /usr/local/strongauth/certs/$(hostname).pem -noprompt 2>&1)
	echo "$(hostname) $RESULT at: /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks"
fi

if [ -f /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks ]; then
	keytool -delete -keystore /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias $(hostname) -noprompt &> /dev/null
	RESULT=$(keytool -import -keystore /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias $(hostname) -file /usr/local/strongauth/certs/$(hostname).pem -noprompt 2>&1)
	echo "$(hostname) $RESULT at: /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks"
fi

for i in $(ls -1 /usr/local/strongauth/ | grep "jdk1.[0-9].0_[0-9]*");
do
	keytool -delete -keystore /usr/local/strongauth/$i/jre/lib/cacerts -storepass changeit -alias $(hostname) -noprompt &> /dev/null
	RESULT=$(keytool -import -keystore /usr/local/strongauth/$i/jre/lib/cacerts -storepass changeit -alias $(hostname) -file /usr/local/strongauth/certs/$(hostname).pem -noprompt 2>&1)
	echo "$(hostname) $RESULT at: /usr/local/strongauth/$i/jre/lib/cacerts"
done

echo "Parsing CA certificates..."
# Capture the pkcs12 output, including all certificates, into a variable
output=$(openssl pkcs12 -nokeys -cacerts -passin pass:"$password" -in $keystore 2> /dev/null)

# Populate three arrays containing the cn, starting line and ending line of the cert such that each index of the arrays corrosponds to information about one cert in the keystore
cert_cn=($(grep "friendlyName:" <<< "$output" | sed -e 's|^[^:]*:\s*||g' -e 's| ||g'))
cert_begin=($(grep -n "BEGIN CERTIFICATE" <<< "$output" | sed 's|:.*$||'))
cert_end=($(grep -n "END CERTIFICATE" <<< "$output" | sed 's|:.*$||'))

for (( i=0; i<${#cert_cn[@]}; i++ ));
do
	# Using the start and end positions, pull the certificate from the pkcs12 output into a file then import them into the temp keystore
        sed -n "${cert_begin[$i]},${cert_end[$i]}p" <<< "$output" > /usr/local/strongauth/certs/"${cert_cn[$i]}".pem
	keytool -import -keystore /tmp/keystore.jks.$timestamp -storepass "$destpass" -alias "${cert_cn[$i]}" -file /usr/local/strongauth/certs/"${cert_cn[$i]}".pem -noprompt &> /dev/null
	cat /usr/local/strongauth/certs/"${cert_cn[$i]}".pem >> /tmp/CAcerts.pem.$TIMESTAMP
	
	echo "Found CA certificate..."
	openssl x509 -text -in /usr/local/strongauth/certs/"${cert_cn[$i]}".pem | sed -n '1,/Subject Public Key Info/p' | sed "$ d"
	unset ANSWER
	read -p "Trust this cert? (y/n): " -r ANSWER
	if [ "$ANSWER" != 'y' ]; then
		continue
	fi

	if [ -f /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks ]; then
		keytool -delete -keystore /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias "${cert_cn[$i]}" -noprompt &> /dev/null
		RESULT=$(keytool -import -keystore /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias "${cert_cn[$i]}" -file /usr/local/strongauth/certs/"${cert_cn[$i]}".pem -noprompt 2>&1)
		echo "${cert_cn[$i]} $RESULT at: /usr/local/strongauth/glassfish/domains/domain1/config/cacerts.jks"
	fi

	if [ -f /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks ]; then
		keytool -delete -keystore /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias "${cert_cn[$i]}" -noprompt &> /dev/null
		RESULT=$(keytool -import -keystore /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks -storepass changeit -alias "${cert_cn[$i]}" -file /usr/local/strongauth/certs/"${cert_cn[$i]}".pem -noprompt 2>&1)
		echo "${cert_cn[$i]} $RESULT at: /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/cacerts.jks"
	fi

	for j in $(ls -1 /usr/local/strongauth/ | grep "jdk1.[0-9].0_[0-9]*");
	do
		keytool -delete -keystore /usr/local/strongauth/$j/jre/lib/cacerts -storepass changeit -alias "${cert_cn[$i]}" -noprompt &> /dev/null
		RESULT=$(keytool -import -keystore /usr/local/strongauth/$j/jre/lib/cacerts -storepass changeit -alias "${cert_cn[$i]}" -file /usr/local/strongauth/certs/"${cert_cn[$i]}".pem -noprompt 2>&1)
		echo "${cert_cn[$i]} $RESULT at: /usr/local/strongauth/$j/jre/lib/cacerts"
	done

done

if [ -f /tmp/CAcerts.pem.$TIMESTAMP ]; then
	openssl verify -purpose sslserver -CAfile /tmp/CAcerts.pem.$TIMESTAMP /usr/local/strongauth/certs/$(hostname).pem &> /dev/null || echo -e "\E[31mWarning! p12 contents do not create a valid chain.\E[0m"
else
	openssl verify -purpose sslserver /usr/local/strongauth/certs/$(hostname).pem &> /dev/null || echo -e "\E[31mWarning! p12 contents do not create a valid chain.\E[0m"
fi

# If destkeystore was specified, import into that keystore. Otherwise look for glassfish installations and replace keystore.jks
echo "Installing keystore..."
if [ -n "$destkeystore" ]; then
	keytool -importkeystore -srckeystore "/tmp/keystore.jks.$timestamp" -srcstoretype JKS -srcstorepass "$destpass" -destkeystore "$destkeystore" -deststoretype JKS -deststorepass "$destpass" 
else
	if [ -f /usr/local/strongauth/glassfish/domains/domain1/config/keystore.jks ]; then
		mv /usr/local/strongauth/glassfish/domains/domain1/config/keystore.jks /usr/local/strongauth/glassfish/domains/domain1/config/keystore.jks.$timestamp 
		cp /tmp/keystore.jks.$timestamp /usr/local/strongauth/glassfish/domains/domain1/config/keystore.jks
		echo "Keystore installed in /usr/local/strongauth/glassfish/domains/domain1/config/"
	fi

	if [ -f /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/keystore.jks ]; then
		mv /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/keystore.jks /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/keystore.jks.$timestamp 
		cp /tmp/keystore.jks.$timestamp /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/keystore.jks
		echo "Keystore installed in /usr/local/strongauth/glassfish4/glassfish/domains/domain1/config/"
	fi
fi

rm /tmp/keystore.jks.$timestamp
rm /tmp/CAcerts.pem.$TIMESTAMP

exit 0
