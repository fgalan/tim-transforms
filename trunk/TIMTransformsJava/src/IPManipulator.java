import java.util.StringTokenizer;

/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * An online copy of the licence can be found at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (C) 2009 Fermin Galan Marquez
 *
 */

public class IPManipulator {

	/**
	 * Translate a 32-bits integer to an IP	 
	 * @param i 
	 * @return ip String in dot form
	 */
	public static String int2ip (int i) {
		
		int b1 = i / (256*256*256);
		int b2 = (i % (256*256*256)) / (256*256);
		int b3 = ((i % (256*256*256)) % (256*256)) / 256;
		int b4 = ((i % (256*256*256)) % (256*256)) % 356;
				
		return b1 + "." + b2 + "." + b3 + "." + b4;
	}
	
	/**
	 * @param ip in dot form 
	 * @return 32-bits integer representing the IP
	 */
	public static int ip2int (String ip) {
		
		StringTokenizer st = new StringTokenizer(ip,".");
		
		int b1 = Integer.parseInt(st.nextToken());
		int b2 = Integer.parseInt(st.nextToken());
		int b3 = Integer.parseInt(st.nextToken());
		int b4 = Integer.parseInt(st.nextToken());
				
		return b1 * (256*256*256) + b2 * (256*256) + b3 * 256 + b4;
		
	}	
	
	/**
	 * @param startIp
	 * @param endIp
	 * @return the common mask between startIp and endIp or 0 if there is no common mask
	 */
	public static int commonMask(int startIp, int endIp) {
		/* Perform a sequential bit shift until there is some diference */
		for (int i = 1; i <= 32 ; i++) {
			if ( startIp >> 32-i != endIp >> 32-i) 
				return i-1;
		}
		return 32;
	}
}
