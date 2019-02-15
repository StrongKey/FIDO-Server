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
# Script to setup the New SKCE domain
#
###############################################################

. /etc/bashrc

GFLIBS=$GLASSFISH_HOME/lib
SKCE_HOME=/usr/local/strongauth/jade
$JAVA_HOME/bin/java -cp $GFLIBS/appserv-rt.jar:\
$GFLIBS/javaee.jar:\
$SKCE_HOME/skcewizards.jar \
com.strongauth.skce.wizard.SKCENewDomainSetup
