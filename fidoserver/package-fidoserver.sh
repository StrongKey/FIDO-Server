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
# Script to create the FIDOSERVER Bundle
#
###############################################################

fidoserver=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
version=$(sed -nr 's|^version=(.*)|\1|p' $fidoserver/common/src/main/resources/resources/appliance/appliance-version.properties)
resources=$fidoserver/common/src/main/resources/resources/appliance
messages=$(sed -n '31,$p' $resources/appliance-messages.properties)

skceresources=$fidoserver/common/src/main/resources/resources/skce
skcemessages=$(sed -n '31,$p' $skceresources/skce-messages.properties)

cryptoresources=$fidoserver/crypto/src/main/resources/resources
cryptomessages=$(sed -n '31,$p' $cryptoresources/crypto-messages.properties)

if [ -f "$fidoserver/*.tgz" ]; then
        rm $fidoserver/*.tgz
fi

failure() {
        tty -s && tput setaf 1
        if [ -d $fidoserver/jade ]; then
                rm -r $fidoserver/jade
        fi
        echo "There was a problem creating the FIDOSERVER distribution. Aborting." >&2
        tty -s && tput sgr0
        exit 1
}

# If we exit prematurely, goto failure function
trap 'failure' 0

# If any command unexpectantly fails, exit prematurely
set -e

# Yellow
tty -s && tput setaf 3
echo "Creating jade..."

# Copy the messages part from the main strongkeylite-messages.properties to all langauge specific files
echo "-Duplicating messages..."
for languagefile in $resources/appliance-messages_*; do
        if grep "ja_JP" <<< "$languagefile" &>/dev/null; then
                continue # Do not edit the JP language file
        fi
        sed -i '31,$d' $languagefile
        echo "$messages" >> $languagefile
done

for languagefile in $skceresources/skce-messages_*; do
        if grep "ja_JP" <<< "$languagefile" &>/dev/null; then
                continue # Do not edit the JP language file
        fi
        sed -i '31,$d' $languagefile
        echo "$skcemessages" >> $languagefile
done

for languagefile in $cryptoresources/crypto-messages_*; do
        if grep "ja_JP" <<< "$languagefile" &>/dev/null; then
                continue # Do not edit the JP language file
        fi
        sed -i '31,$d' $languagefile
        echo "$cryptomessages" >> $languagefile
done

# Create jade
# This cd is important for mvn to work
cd $fidoserver
echo "-Clean and building source..."
mvn clean install -q 

# Copy the necessary jars, libs, wars, ears into jade
echo "-Copying files..."
mkdir -p $fidoserver/jade/sql
touch $fidoserver/jade/Version${version}
cp -r $fidoserver/fidoserverInstall/src/fidoserverSQL/mysql $fidoserver/jade/sql
cp $fidoserver/fidoserverEAR/target/fidoserver.ear $fidoserver/jade

# Create archives
echo "-Packaging jade..."
tar zcf FIDOServer-v${version}.tgz -C $fidoserver jade

# Remove jade
rm -r $fidoserver/jade

# Do not go to the failure function
trap : 0
echo "Success!"
tty -s && tput sgr0

exit 0
