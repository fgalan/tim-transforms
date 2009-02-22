import java.util.Vector;

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
 * Copyright (C) 2008, 2009 Fermin Galan Marquez
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
		String file1vn = "/tmp/wbem/vnuml-basic.xml";
		String file1an = "/tmp/wbem/adnet-basic.xml";
		String file2vn = "/tmp/wbem/vnuml-nsfnet.xml";
		String file2vo = "/tmp/wbem/vnuml-nsfnet-ospf.xml";		
		String file2an = "/tmp/wbem/adnet-nsfnet.xml";
		String file2ao = "/tmp/wbem/adnet-nsfnet-ospf.xml";
		String file3vn = "/tmp/wbem/vnuml-rediris.xml";
		String file3vo = "/tmp/wbem/vnuml-rediris-ospf.xml";
		String file3an = "/tmp/wbem/adnet-rediris.xml";
		String file3ao = "/tmp/wbem/adnet-rediris-ospf.xml";
		
		/* When local machine is Windows */
		//String file1vn = "C:\\test\\vnuml-basic.xml";
		//String file1an = "C:\\test\\adnet-basic.xml";
		//String file2vn = "C:\\test\\vnuml-nsfnet.xml";
		//String file2vo = "C:\\test\\vnuml-nsfnet-ospf.xml";		
		//String file2an = "C:\\test\\adnet-nsfnet.xml";
		//String file2ao = "C:\\test\\adnet-nsfnet-ospf.xml";
		//String file3vn = "C:\\test\\vnuml-rediris.xml";
		//String file3vo = "C:\\test\\vnuml-rediris-ospf.xml";
		//String file3an = "C:\\test\\adnet-rediris.xml";
		//String file3ao = "C:\\test\\adnet-rediris-ospf.xml";
		
		/* Use TransformationManager(ns, true) if testbed parameters are stored in
		 * the CIMOM. Use TransformationManager(ns, false) to use the defaults
		 * hardwired in the code */
		TransformationManager tm = new TransformationManager(ns, true);
		
		Vector<String> v;

		System.out.println("Transformation: testbed "+testbed1+" -> vnuml ...");		
		tm.getVnumlTransformation().setDefaultConsole("xterm");
		v = new Vector<String>(1);
		v.add(file1vn);
		tm.getVnumlTransformation().toTsm(testbed1, v);
		
		System.out.println("Transformation: testbed "+testbed1+" -> adnetconf ...");
		v = new Vector<String>(1);
		v.add(file1an);
		tm.getAdrenalineTransformation().toTsm(testbed1, v);

		System.out.println("Transformation: testbed "+testbed2+" -> vnuml ...");
		v = new Vector<String>(2);
		v.add(file2vn);
		v.add(file2vo);
		tm.getVnumlTransformation().setDefaultConsole("pts");
		tm.getVnumlTransformation().toTsm(testbed2, v);
		
		System.out.println("Transformation: testbed "+testbed2+" -> adnetconf ...");
		v = new Vector<String>(2);
		v.add(file2an);
		v.add(file2ao);
		tm.getAdrenalineTransformation().toTsm(testbed2, v);
		
		System.out.println("Transformation: testbed "+testbed3+" -> vnuml ...");
		v = new Vector<String>(2);
		v.add(file3vn);
		v.add(file3vo);
		tm.getVnumlTransformation().toTsm(testbed3, v);
		
		System.out.println("Transformation: testbed "+testbed3+" -> adnetconf ...");
		v = new Vector<String>(2);
		v.add(file3an);
		v.add(file3ao);
		tm.getAdrenalineTransformation().toTsm(testbed3, v);
				
		System.out.println("End of program!");
	}
	

}
