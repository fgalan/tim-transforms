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

import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMInstance;
import javax.wbem.cim.CIMObjectPath;
import javax.wbem.cim.UnsignedInt16;
import javax.wbem.cim.UnsignedInt64;
import javax.wbem.client.CIMClient;

/**
 * This class implements the TIM-to-TSM transformation for the
 * ADRENALINE testbed. In only considers (by the moment) networking
 * aspects (the adrenaline.xml file).
 * 
 * Note that the TSM domain is a subset of all the features offered by
 * ADNETCONF. For example, the RouterTester is not used (so the <router_tester>
 * and <client_link> are ignored).
 * 
 * @author Fermin Galan Marquez
 */
public class AdrenalineTransformation extends TIMTransformation {

	private CIMClient cc;

	/* Pathnames of some files to boot in OCCs */
	private String zebraBin;
	private String ospfdBin;
	private String lrmBin;
	private String rsvpdBin;
	private String rtapBin;
	private String snmpdBin;
	private String olrmBin;
	private String cpbwmBin;
	
	/* Base IP address to automatically (monolitically increasing) generate 
	 * the <m_ip> for OCCs */
	private Inet4Address baseMgtAddress;
	
	/* Base IP address to automatically (monolitically increasing) generate 
	 * the <vlan_connection>s' nets <m_ip> for OCC-to-OCC connections. It
	 * must be a /30 network address (i.e., last two bits must be zero)  */
	private Inet4Address baseConnectAddress;
		
	/* Base VLAN ID to automatically (monolitically increasing) generate 
	 * the <vlan> for OCC-to-OCC connections */
	private int baseVlan;
	
	private String vlanDev;
	
	/**
	 * Class constructor
	 * 
	 * @param cc the (already opened) connection to the CIMOM	 
	 * @param initializeFromCIMOM if true, the testbed parameters are got from CIMON when
	 * initializing the transformers, instead of using default values in the code 
	 * @throws TIMTransformationException 
	 */
	public AdrenalineTransformation(CIMClient cc, boolean initializeFromCIMOM) throws TIMTransformationException {
		this.cc = cc;
		
		/* Values not related with the scenario, but with the testbed 
		 * deployment environment */
		if (initializeFromCIMOM) {
			/* Get the CTM_AdrenalineTestbedParameters instance that match scenarioName */
			Enumeration e;
			try {
				e = cc.enumerateInstances(new CIMObjectPath("CTM_AdrenalineTestbedParameters"));
			} catch (CIMException ex) {
				ex.printStackTrace();
				throw new TIMTransformationException(ex);
			}
			if (e.hasMoreElements()) {
				CIMInstance inst = (CIMInstance)e.nextElement();
				
				zebraBin = (String)inst.getProperty("zebraBin").getValue().getValue();
				ospfdBin = (String)inst.getProperty("ospfdBin").getValue().getValue();
				lrmBin   = (String)inst.getProperty("lrmBin").getValue().getValue();
				rsvpdBin = (String)inst.getProperty("rsvpdBin").getValue().getValue();
				rtapBin  = (String)inst.getProperty("rtapBin").getValue().getValue();
				snmpdBin = (String)inst.getProperty("snmpdBin").getValue().getValue();
				olrmBin  = (String)inst.getProperty("olrmBin").getValue().getValue();
				cpbwmBin = (String)inst.getProperty("cpbwmBin").getValue().getValue();
			
				String baseMgtAddressString = (String)inst.getProperty("baseMgtAddress").getValue().getValue();
				String baseConnectAddressString = (String)inst.getProperty("baseConnectAddress").getValue().getValue();
				
				try {
					/* Building the raw IP address (there is no any method in the Java API to
					 * do this much simpler?)
					 */
					StringTokenizer st = new StringTokenizer(baseMgtAddressString,".");
					byte i1 = Byte.parseByte(st.nextToken());
					byte i2 = Byte.parseByte(st.nextToken());
					byte i3 = Byte.parseByte(st.nextToken());
					byte i4 = Byte.parseByte(st.nextToken());
					byte[] b1 = {i1, i2, i3, i4};
					baseMgtAddress = (Inet4Address) InetAddress.getByAddress(b1);

					st = new StringTokenizer(baseConnectAddressString,".");
					i1 = Byte.parseByte(st.nextToken());
					i2 = Byte.parseByte(st.nextToken());
					i3 = Byte.parseByte(st.nextToken());
					i4 = Byte.parseByte(st.nextToken());
					byte[] b2 = {i1, i2, i3, i4};
					baseConnectAddress = (Inet4Address) InetAddress.getByAddress(b2);

				} catch (UnknownHostException ex) {
					throw new TIMTransformationException("Bad address in CIMOM ADRENALINE parameters");
				}
			
				baseVlan = ((UnsignedInt16)inst.getProperty("baseVlan").getValue().getValue()).intValue();
				vlanDev = (String)inst.getProperty("vlanDev").getValue().getValue();

			}
			else {
				throw new TIMTransformationException("No testbed parameters were found in CIMOM for ADRENALINE");
			}
		}
		else {
			
			zebraBin = "/users/home/grups/zebra/test_bin/zebra";
			ospfdBin = "/users/home/grups/zebra/zebra-0.94/ospfd/ospfd";
			lrmBin   = "/mnt/server/grups/zebra/lrm/lrm/lrm";
			rsvpdBin = "/mnt/server/grups/rsvpte-svn/rsvpd/rsvpd";
			rtapBin  = "/mnt/server/grups/rsvpte-svn/apitools/rtap";
			snmpdBin = "/users/home/grups/nms/Agent/snmpd_project/snmpd_project";
			olrmBin  = "/mnt/server/grups/cci/before_protection_modifications/src/olrm";
			cpbwmBin = "/mnt/server/grups/cpbwmon/src/release/cpbwm";
		
			try {
				byte[] b1 = {10, 1, 1, 120};
				baseMgtAddress = (Inet4Address) InetAddress.getByAddress(b1);
				byte[] b2 = {10, 10, 0, 0};
				baseConnectAddress = (Inet4Address) InetAddress.getByAddress(b2);
			} catch (UnknownHostException ex) {
				// This CAN NOT happen, given the three addressed above are hardwired
				ex.printStackTrace();
			}
		
			baseVlan = 600;
			vlanDev = "eth0";
		}
	}
	
	/**
	 * Performs the TIM-to-TSM transformation, returning a String enclosing a
	 * XML comlaint with the ADNETCONF network specification (adrenaline.xml). 
	 * 
	 * @param scenarioName the name of the scenario to be transformed
	 * @return the derived TSM model (ADNETCONF-compliant XML)
	 * @throws TIMTransformationException
	 */	
	public String toTsm(String scenarioName) throws TIMTransformationException {
		
		StringWriter s = new StringWriter();
			
		/* Get the TIM_TestbedScenario instance that match scenarioName */
		CIMInstance scenario = null;
		Enumeration e;
		try {
			e = cc.enumerateInstances(new CIMObjectPath("TIM_TestbedScenario"));
		} catch (CIMException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		while (e.hasMoreElements()) {
			CIMInstance inst = (CIMInstance)e.nextElement();
			String name = (String)inst.getProperty("Name").getValue().getValue();
			if (name.equals(scenarioName)) {
				scenario = inst;
				break;
			}
		}
		if (scenario == null) {
			throw new TIMTransformationException("No scenario with name "+scenarioName+" was found");
		}
		//System.out.println(scenario.toString());
		
		try {
			s.write(specHeader(scenarioName));
			s.write(specNodes(scenario));
			s.write(specLinks(scenario));
			s.write(specFooter());
		} catch (CIMException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		
		return s.toString();
	}

	private String specHeader(String scenarioName) {
		
		StringWriter s = new StringWriter();
		
		/* Generate the usual file header */
		s.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		s.write("<!DOCTYPE adrenaline_netconf SYSTEM '/usr/share/adnetconf/adnetconf.dtd'>\n");
		s.write("<adrenaline_netconf>\n");
		
		s.write("  <conf>\n");
		s.write("    <!-- empty -->\n");
		s.write("  </conf>\n");
				
		return s.toString();
	}
	
	private String specNodes(CIMInstance scenario) throws CIMException, TIMTransformationException {
		
		StringWriter s = new StringWriter();
		
		/* Generate <nodes> tag */
		s.write("  <nodes>\n");
		
		/* TIM_TestbedScenario->CIM_ComputerSystem to generate <node> */
		Enumeration e = cc.associators(scenario.getObjectPath(), 
				"CIM_SystemComponent",
				"CIM_ComputerSystem", 
				"GroupComponent",
				"PartComponent", false, false, null);
		
		/* Management address counter. It seems a bit weird, but note that
		 * InetAddress are not clonable */
		Inet4Address currentMgtAddress = null;
		try {
			currentMgtAddress = (Inet4Address) InetAddress.getByAddress(baseMgtAddress.getAddress());
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		
		while(e.hasMoreElements()) {
			CIMInstance cs = (CIMInstance)e.nextElement();
			//System.out.println(cs.getObjectPath().toString());
			
			/* Search for a loopback CIM_IPProtocolEndPoint (the which is not
			 * connected to any network */
			String loIp = null;
			Enumeration ee = cc.associators(cs.getObjectPath(), 
					"CIM_HostedAccessPoint", 
					"CIM_IPProtocolEndpoint", 
					"Antecedent", 
					"Dependent", false, false, null);
			while(ee.hasMoreElements()) {
				CIMInstance ipe = (CIMInstance)ee.nextElement();
				//System.out.println(ipe.getObjectPath().toString());
				Enumeration eee = cc.associators(ipe.getObjectPath(), 
						"TIM_MemberOfLink", 
						"TIM_LinkConnectivityCollection", 
						"Member", 
						"Collection", false, false, null);
				if (eee.hasMoreElements()) {
					continue;
				}
				
				/* If we get here, it's a looback interface, get the IP */
				eee = cc.associators(ipe.getObjectPath(), 
						"CIM_ElementSettingData", 
						"CIM_StaticIPAssignmentSettingData", 
						"ManagedElement", 
						"SettingData", false, false, null);
				if(eee.hasMoreElements()) {
					CIMInstance ip4 = (CIMInstance)eee.nextElement();
					//	System.out.println(ip4.getObjectPath().toString());
					loIp = (String)ip4.getProperty("IPv4Address").getValue().getValue();
					break;
				}

			}
			 
			String name = (String)cs.getProperty("Name").getValue().getValue();
			s.write("    <node id='"+name+"'>\n");
			s.write("      <m_ip>"+currentMgtAddress.getHostAddress()+"</m_ip>\n");
			if (loIp != null) {
				s.write("      <lo_ip>"+loIp+"</lo_ip>\n");
			}
			else {
				System.out.println("WARNING: no looback IP for OCC "+name+". This probabliy cause fails in adnetconf.pl");
			}
			s.write("      <zebra_bin>"+zebraBin+"</zebra_bin>\n");
			s.write("      <ospfd_bin>"+ospfdBin+"</ospfd_bin>\n");
			s.write("      <lrm_bin>"+lrmBin+"</lrm_bin>\n");
			s.write("      <rsvpd_bin>"+rsvpdBin+"</rsvpd_bin>\n");
			s.write("      <rtap_bin>"+rtapBin+"</rtap_bin>\n");
			s.write("      <snmpd_bin>"+snmpdBin+"</snmpd_bin>\n");
			s.write("      <olrm_bin>"+olrmBin+"</olrm_bin>\n");
			s.write("      <cpbwm_bin>"+cpbwmBin+"</cpbwm_bin>\n");			
			s.write("    </node>\n");
			
			/* Increase management address counte */
			try {
				currentMgtAddress = getNextIpAddress(currentMgtAddress);
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
				throw new TIMTransformationException(ex);
			}
		}
		s.write("  </nodes>\n");
		
		return s.toString();
	}
		
	private String specLinks(CIMInstance scenario) throws CIMException, TIMTransformationException {
		
		StringWriter s = new StringWriter();
		
		/* Generate <links> tag. Only <link> are generated (because of we
		 * are not using RouterTester, <client_link> make no sense). Among
		 * the different link types ADNETCONF allows we base on <vlan_connection> 
		 * (i.e., one VLAN ID per OCC-to-OCC connection) 
		 */
		
		/* TIM_TestbedScenario->CIM_ComputerSystem->CIM_IPProtocolEndpoint->TIM_ConnectivityConnection
		 * to get the networks and generate <link>
		 */ 
		Hashtable ht = new Hashtable();
		
		Enumeration e = cc.associators(scenario.getObjectPath(), 
				"CIM_SystemComponent",
				"CIM_ComputerSystem", 
				"GroupComponent",
				"PartComponent", false, false, null);
		
		while(e.hasMoreElements()) {
			CIMInstance cs = (CIMInstance)e.nextElement();
			//System.out.println(cs.getObjectPath().toString());
			Enumeration ee = cc.associators(cs.getObjectPath(), 
					"CIM_HostedAccessPoint", 
					"CIM_IPProtocolEndpoint", 
					"Antecedent", 
					"Dependent", false, false, null);
			while(ee.hasMoreElements()) {
				CIMInstance ipe = (CIMInstance)ee.nextElement();
				//System.out.println(ipe.getObjectPath().toString());
				Enumeration eee = cc.associators(ipe.getObjectPath(), 
						"TIM_MemberOfLink", 
						"TIM_LinkConnectivityCollection", 
						"Member", 
						"Collection", false, false, null);
				
				/* We are considering that the Enumeration has only zero
				 * (if loopback interface) or one (if conventional interface)
				 * element, which is consistent with the MOF Class
				 * definition for TIM. Loopback interfaces are ignored.
				 */
				if (eee.hasMoreElements()) {
					CIMInstance lcc = (CIMInstance)eee.nextElement();
					//System.out.println(lcc.getObjectPath().toString());
					String net = (String)lcc.getProperty("InstanceID").getValue().getValue();
				
					/* Sanity check: ADRENALINE only allows point-to-poing connections, so
					 * any network with MaxConnections different from 2. Note that if
					 * the property has not been specified in the MOF (implicit "no limit")
					 * it gets null, so the first if statement
					 */
					
					if (lcc.getProperty("MaxConnections").getValue() == null) {
						System.out.println("WARNING: network "+net+" it isn't modeling a PPP connection (it has not MaxConnections defined). Skipping.");
						continue;
					}
					int maxConn = ((UnsignedInt16)lcc.getProperty("MaxConnections").getValue().getValue()).intValue();
					if (maxConn != 2) {
						System.out.println("WARNING: network "+net+" it isn't modeling a PPP connection (it has MaxConnections = "+maxConn+"). Skipping.");
						continue;
					}
						
					ht.put(net,lcc);
				
				}

			}

		}
		
		/* Counter for gre identifiers */
		int gre = 1;
		
		/* Counter for VLANs */
		int currentVlan = baseVlan;
		
		/* Connection and GRE addresses counter. It seems a bit weird, but note that
		 * InetAddress are not clonable */
		Inet4Address currentConnectAddress = null;
		try {
			currentConnectAddress = (Inet4Address) InetAddress.getByAddress(baseConnectAddress.getAddress());
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		
		s.write("  <links>\n");
		
		/* We need to use the hashtable because of duplication */
		e = ht.elements();	
		while (e.hasMoreElements()) {
			CIMInstance lcc = (CIMInstance)e.nextElement();
			//String net = (String)lcc.getProperty("InstanceID").getValue().getValue();
			//System.out.println(net);
			
			/* Get the both ends of the connection. We expect exactly two elements
			 * in the enumeration (we have checked before, with MaxConnections) */
			Enumeration ee = cc.associators(lcc.getObjectPath(), 
					"TIM_MemberOfLink",
					"CIM_IPProtocolEndpoint", 
					"Collection",
					"Member", false, false, null);
			CIMInstance if1 = (CIMInstance)ee.nextElement();
			CIMInstance if2 = (CIMInstance)ee.nextElement();
			
			/* Each if is associated with *exactly one* CIM_ComputerSystems, by
			 * definition (following the TIM MOF cardinality specification)
			 */
			ee = cc.associators(if1.getObjectPath(),
					"CIM_HostedAccessPoint",
					"CIM_ComputerSystem", 
					"Dependent",
					"Antecedent", false, false, null);
			CIMInstance occ1 = (CIMInstance)ee.nextElement();
			
			ee = cc.associators(if2.getObjectPath(),
					"CIM_HostedAccessPoint",
					"CIM_ComputerSystem", 
					"Dependent",
					"Antecedent", false, false, null);
			CIMInstance occ2 = (CIMInstance)ee.nextElement();
			
			String id1 = (String)occ1.getProperty("Name").getValue().getValue();
			String id2 = (String)occ2.getProperty("Name").getValue().getValue();
			
			/* Each if is associated with at least one CIM_StaticIPAssignmentSettingData, by
			 * definition (following the TIM MOF cardinality specification)
			 */
			String gre1 = "";
			ee = cc.associators(if1.getObjectPath(), 
					"CIM_ElementSettingData", 
					"CIM_StaticIPAssignmentSettingData", 
					"ManagedElement", 
					"SettingData", false, false, null);
			if (ee.hasMoreElements()) { 
				CIMInstance ip4 = (CIMInstance)ee.nextElement();
				//System.out.println(ip4.getObjectPath().toString());
				gre1 = (String)ip4.getProperty("IPv4Address").getValue().getValue();
			}
			
			String gre2 = "";
			ee = cc.associators(if2.getObjectPath(), 
					"CIM_ElementSettingData", 
					"CIM_StaticIPAssignmentSettingData", 
					"ManagedElement", 
					"SettingData", false, false, null);
			if (ee.hasMoreElements()) { 
				CIMInstance ip4 = (CIMInstance)ee.nextElement();
				//System.out.println(ip4.getObjectPath().toString());
				gre2 = (String)ip4.getProperty("IPv4Address").getValue().getValue();
			}
			
			Inet4Address net;
			try {
				/* Split the next /30 network */
				net = currentConnectAddress;
				Inet4Address host1 = getNextIpAddress(currentConnectAddress); // this is useless, but we need to skip it
				Inet4Address host2 = getNextIpAddress(host1); // this is useless, but we need to skip it
				Inet4Address bcast = getNextIpAddress(host2); // this is useless, but we need to skip it
				currentConnectAddress = getNextIpAddress(bcast);
				
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
				throw new TIMTransformationException(ex);
			}
			
			s.write("    <link>\n");
			s.write("      <peer node='"+id1+"' vlan_dev='"+vlanDev+"'>\n");
			s.write("        <vlan_connection net='"+net.getHostAddress()+"'>\n");
			s.write("          <vlan>"+ currentVlan++ +"</vlan>\n");
			s.write("          <gre>\n");
			s.write("            <name>gre"+ gre++ +"</name>\n");
			s.write("            <ip>"+ gre1 +"</ip>\n");
			s.write("          </gre>\n");
			s.write("        </vlan_connection>\n");
			s.write("      </peer>\n");
			s.write("      <peer node='"+id2+"' vlan_dev='"+vlanDev+"'>\n");
			s.write("        <vlan_connection>\n");
			s.write("          <gre>\n");
			s.write("            <name>gre"+ gre++ +"</name>\n");
			s.write("            <ip>"+ gre2 +"</ip>\n");
			s.write("          </gre>\n");
			s.write("        </vlan_connection>\n");
			s.write("      </peer>\n");
			
			/* Look for an associated TIM_LinkTransmissionElement (optionally) */
			ee = cc.associators(lcc.getObjectPath(), 
					"TIM_LinkTransmissionElement",
					"TIM_TransmissionCharacteristics", 
					"Antecedent",
					"Dependent", false, false, null);
			if (ee.hasMoreElements()) {
				CIMInstance tc = (CIMInstance)ee.nextElement();
				
				/* Get delay, loss and duplication */
				UnsignedInt64 delay = (UnsignedInt64)tc.getProperty("DelayMean").getValue().getValue();
				Float loss = (Float)tc.getProperty("LossProbabilityValue").getValue().getValue();
				Float dup = (Float)tc.getProperty("DuplicationProbabilityValue").getValue().getValue();
				s.write("      <qos>\n");
				if (delay != null) {
					s.write("          <delay>"+delay.intValue()+"</delay>\n");
				}
				if (loss != null) {
					s.write("          <drop>"+loss+"</drop>\n");
				}
				if (dup != null) {
					s.write("          <dup>"+dup+"</dup>\n");
				}
				s.write("      </qos>\n");
			}
			
			s.write("    </link>\n");
		}

		s.write("  </links>\n");
		
		return s.toString();
	}
	
	private String specFooter() {
		StringWriter s = new StringWriter();
		s.write("</adrenaline_netconf>\n");
		return s.toString();
	}
	
	/**
	 * Increases an IP address by one
	 * 
	 * @param ip
	 * @return
	 * @throws UnknownHostException
	 */
	private Inet4Address getNextIpAddress(Inet4Address ip) throws UnknownHostException {
		
		byte[] b = ip.getAddress();
		
		b[3]++;
		if (b[3] == 0) {
			b[2]++;
			if (b[2] == 0) {
				b[1]++;
				if (b[1] == 0) {
					b[0]++;
				}
			}
		}
		
		return (Inet4Address) InetAddress.getByAddress(b);
		
	}

	public Inet4Address getBaseConnectAddress() {
		return baseConnectAddress;
	}

	public void setBaseConnectAddress(Inet4Address baseConnectAddress) {
		this.baseConnectAddress = baseConnectAddress;
	}

	public Inet4Address getBaseMgtAddress() {
		return baseMgtAddress;
	}

	public void setBaseMgtAddress(Inet4Address baseMgtAddress) {
		this.baseMgtAddress = baseMgtAddress;
	}

	public int getBaseVlan() {
		return baseVlan;
	}

	public void setBaseVlan(int baseVlan) {
		this.baseVlan = baseVlan;
	}

	public String getCpbwmBin() {
		return cpbwmBin;
	}

	public void setCpbwmBin(String cpbwmBin) {
		this.cpbwmBin = cpbwmBin;
	}

	public String getLrmBin() {
		return lrmBin;
	}

	public void setLrmBin(String lrmBin) {
		this.lrmBin = lrmBin;
	}

	public String getOlrmBin() {
		return olrmBin;
	}

	public void setOlrmBin(String olrmBin) {
		this.olrmBin = olrmBin;
	}

	public String getOspfdBin() {
		return ospfdBin;
	}

	public void setOspfdBin(String ospfdBin) {
		this.ospfdBin = ospfdBin;
	}

	public String getRsvpdBin() {
		return rsvpdBin;
	}

	public void setRsvpdBin(String rsvpdBin) {
		this.rsvpdBin = rsvpdBin;
	}

	public String getRtapBin() {
		return rtapBin;
	}

	public void setRtapBin(String rtapBin) {
		this.rtapBin = rtapBin;
	}

	public String getSnmpdBin() {
		return snmpdBin;
	}

	public void setSnmpdBin(String snmpdBin) {
		this.snmpdBin = snmpdBin;
	}

	public String getVlanDev() {
		return vlanDev;
	}

	public void setVlanDev(String vlanDev) {
		this.vlanDev = vlanDev;
	}

	public String getZebraBin() {
		return zebraBin;
	}

	public void setZebraBin(String zebraBin) {
		this.zebraBin = zebraBin;
	}
	
}