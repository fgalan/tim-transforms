#!/usr/bin/perl
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
# The MOF speficiation document (DSP0004, )allows alias utilization (sections ), but
# it seems that some compilers (in particular, the mofcomp that comes with WBEM Services)
# have problems when such alias are used.
#
# In order to help those compilers, this script peform automatic alias expansion. Thus,
# it takes an input MOF files as alias and generate an output MOF file in which the alias
# has been resolved. It needs as additional argument the URL namespace to use in the
# expansion (eg., "http://phoenix:5988/cimv2181").

# Usage: ./expand_alias.pl input_file output_file namespace_url

use strict;

my $ifile = shift;
my $ofile = shift;
my $url = shift;

# First, build a hash for the aliases. The key properties are identified after
# the "key properties" line, after finding a empty line(this is ugly because of
# it involves implicit restrictions in the MOF file format, but checking
# the CIM classes would be overwhelming for a simple script like this one)

my %aliases;
open INPUT, "$ifile";

my $in_keys  = 0;

my $current_alias;

while (<INPUT>) {
	if (/(\w+) as \$(\w+)/) {
		$current_alias = $2;		
		$aliases{$current_alias} = "$1.";
		print("alias $current_alias (class $1): ");
	}
	if (/key properties/) {
		$in_keys = 1;
	}
	if ((/(\w+)\W?=\W?"(.+)";/) && $in_keys) {
		my $l = $1;
		my $r = $2;
		if ($aliases{$current_alias} =~ /\.$/) {
			$aliases{$current_alias} .= "$l=$r";
		}
		else {
			$aliases{$current_alias} .= ",$l=$r";
		}		
	}
	if ((/^$/) && $in_keys) {
		$in_keys = 0;
		print "$aliases{$current_alias}\n";
	}
}
close INPUT;

# Second, expand each alias with the string calculated in the previous step
# In addition, remove the "as $ALIAS" string

my $n_e = 0;
my $n_v = 0;

open INPUT, "$ifile";
open OUT, ">$ofile";
while (<INPUT>) {
	if (/(.*) as \$\w+(.*)/) {
		print OUT "$1$2\n";
	}
	elsif (/(\w+)\W?=\W?"?\$(.+)"?;/) {
		foreach my $key (keys %aliases) {
			if ($2 eq $key) {
				print "expanding $key\n";
				$n_e++;
				print OUT "\t$1 = \"".$url.":".$aliases{$key}."\";\n";
				last;
			}
		}
	}
	else {
		print OUT "$_";
		$n_v++;
	}
}
close INPUT;
close OUT;

print "STATS: $n_e expansions, $n_v verbatim lines\n";
