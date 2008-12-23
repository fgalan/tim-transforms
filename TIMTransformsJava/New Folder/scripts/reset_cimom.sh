#!/bin/sh -x
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#
# An online copy of the licence can be found at http://www.gnu.org/copyleft/gpl.html
#
# Copyright (C) 2008 Fermin Galan Marquez
#
# Reset the CIMOM repository.
#
# WARNINGS:
# - we asume that the file /usr/local/src/wbemservices-1.0.2.bin.zip exists
# - ensure the CIMOM server is stopped before using this script

# FIXME: unhardwire to generic variables in the preamble

PROJECT=/data/fermin/Ph.D/validation/eclipse-workspace/TIMTransforms
HOST=localhost
NS=root/cimv2181

# Reseting
cd /usr/local/src/
cp wbemservices-1.0.2.bin.zip /tmp/
cd /tmp/ && unzip wbemservices-1.0.2.bin.zip
cd /usr/local/src/wbemservices/cimom
rm -f logr/*
mv /tmp/wbemservices/cimom/logr/* logr/
rm -rf /tmp/wbemservices

# Starting the CIMOM
cd /usr/local/src/wbemservices/cimom/bin
sh start_cimom.sh

# Compiling the MOF Schema
cd /usr/local/src/wbemservices/mof/dmtf/
mkdir 2.18.1
cd 2.18.1
wget http://www.dmtf.org/standards/cim/cim_schema_v2181/cim_schema_2.18.1-Final-MOFs.zip
unzip cim_schema_2.18.1-Final-MOFs.zip
cd /usr/local/src/wbemservices/bin
sh mofcomp -u fermin -p x -n $NS -c $HOST /usr/local/src/wbemservices/mof/dmtf/2.18.1/cimv218.mof
rm -rf /usr/local/src/wbemservices/mof/dmtf/2.18.1

# Copying MOF to tmp
cd $PROJECT
rm -rf /tmp/mof
cp -r mof /tmp

# Expanding MOF
$PROJECT/scripts/expand_alias.pl /tmp/mof/basic.mof /tmp/mof/basic_e.mof http://$HOST/$NS > /dev/null
$PROJECT/scripts/expand_alias.pl /tmp/mof/nsfnet.mof /tmp/mof/nsfnet_e.mof http://$HOST/$NS > /dev/null
$PROJECT/scripts/expand_alias.pl /tmp/mof/rediris.mof /tmp/mof/rediris_e.mof http://$HOST/$NS > /dev/null

# Compiling
cd /usr/local/src/wbemservices/bin
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/tim.mof
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/testbed_parameters.mof
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/testbed_parameters-defaults.mof
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/basic_e.mof
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/nsfnet_e.mof
sh mofcomp -u fermin -p x -n $NS -c $HOST /tmp/mof/rediris_e.mof
