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
 * Copyright (C) 2008 Fermin Galan Marquez
 *
 */

public class TransTester {

	/**
	 * @param args
	 * @throws TIMTransformationException 
	 */
	public static void main(String[] args) throws TIMTransformationException  {

		//String ns = "http://phoenix:5988//root/cimv2181";
		//String ns = "http://192.168.1.103:5988//root/cimv2181";
		String ns = "http://localhost:5988//root/cimv2181";
		String testbed1 = "basic";
		String testbed2 = "nsfnet";
		String testbed3 = "rediris";
		
		/* When local machine is Linux */
		String file1v = "/tmp/vnuml-basic.xml";
		String file1a = "/tmp/adnet-basic.xml";
		String file2v = "/tmp/vnuml-nsfnet.xml";
		String file2a = "/tmp/adnet-nsfnet.xml";
		String file3v = "/tmp/vnuml-rediris.xml";
		String file3a = "/tmp/adnet-rediris.xml";		
		
		/* When local machine is Windows */
		//String file1v = "C:\\test\\vnuml-basic.xml";
		//String file1a = "C:\\test\\adnet-basic.xml";
		//String file2v = "C:\\test\\vnuml-nsfnet.xml";
		//String file2a = "C:\\test\\adnet-nsfnet.xml";
		//String file3v = "C:\\test\\vnuml-rediris.xml";
		//String file3a = "C:\\test\\adnet-rediris.xml";
		
		/* Use TransformationManager(ns, true) if testbed parameters are stored in
		 * the CIMOM. Use TransformationManager(ns, false) to use the defaults
		 * hardwired in the code */
		TransformationManager tm = new TransformationManager(ns, true);

		tm.getVnumlTransformation().setDefaultConsole("xterm");
		System.out.println("Transformation: testbed "+testbed1+" -> vnuml ...");
		tm.getVnumlTransformation().toTsm(testbed1, file1v);
		System.out.println("Transformation: testbed "+testbed1+" -> adnetconf ...");
		tm.getAdrenalineTransformation().toTsm(testbed1, file1a);
		
		tm.getVnumlTransformation().setDefaultConsole("pts");
		System.out.println("Transformation: testbed "+testbed2+" -> vnuml ...");
		tm.getVnumlTransformation().toTsm(testbed2, file2v);
		System.out.println("Transformation: testbed "+testbed2+" -> adnetconf ...");
		tm.getAdrenalineTransformation().toTsm(testbed2, file2a);
		
		System.out.println("Transformation: testbed "+testbed3+" -> vnuml ...");
		tm.getVnumlTransformation().toTsm(testbed3, file3v);
		System.out.println("Transformation: testbed "+testbed3+" -> adnetconf ...");
		tm.getAdrenalineTransformation().toTsm(testbed3, file3a);
				
		System.out.println("End of program!");
	}
	

}
