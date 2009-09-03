#!/bin/sh
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
# Copyright (C) 2009 Fermin Galan Marquez

NEW=/mnt/hgfs/data2
REF=/mnt/hgfs/data2/fermin/Ph.D/validation/eclipse-workspace/TIMTransformsMDA/xml

for f in "adnet-basic.xml" "adnet-nsfnet.xml" "adnet-rediris.xml" "vnuml-basic.xml" "vnuml-nsfnet.xml" "vnuml-rediris.xml" "adnet-nsfnet-ospf.xml" "vnuml-nsfnet-ospf.xml" "adnet-rediris-ospf.xml" "vnuml-rediris-ospf.xml" ; do
	echo "Comparing $f ..."
	diff -u $NEW/$f $REF/$f
done
