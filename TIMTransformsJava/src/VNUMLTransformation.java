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

import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMInstance;
import javax.wbem.cim.CIMObjectPath;
import javax.wbem.cim.UnsignedInt16;
import javax.wbem.cim.UnsignedInt32;
import javax.wbem.cim.UnsignedInt8;
import javax.wbem.client.CIMClient;

/**
 * This class implements the TIM-to-VNUML transformations.
 * 
 * @author Fermin Galan Marquez
 */
public class VNUMLTransformation extends TIMTransformation {

	private CIMClient cc;

	private String specVersion;
	private String defaultExecMode;
	private String defaultFilesystemType;
	private String defaultFilesystem;
	private String defaultKernel;
	private String defaultConsole;
	
	/**
	 * Class constructor
	 * 
	 * @param cc the (already opened) connection to the CIMOM
	 * @param initializeFromCIMOM if true, the testbed parameters are got from CIMON when
	 * initializing the transformers, instead of using default values in the code 
	 * @throws TIMTransformationException 
	 */
	public VNUMLTransformation(CIMClient cc, boolean initializeFromCIMOM) throws TIMTransformationException {
		this.cc = cc;
		
		/* Values not related with the scenario, but with the testbed 
		 * deployment environment */
		
		if (initializeFromCIMOM) {
			/* Get the CTM_VNUMLTestbedParameters instance that match scenarioName */
			Enumeration e;
			try {
				e = cc.enumerateInstances(new CIMObjectPath("CTM_VNUMLTestbedParameters"));
			} catch (CIMException ex) {
				ex.printStackTrace();
				throw new TIMTransformationException(ex);
			}
			if (e.hasMoreElements()) {
				CIMInstance inst = (CIMInstance)e.nextElement();
				
				specVersion = (String)inst.getProperty("specVersion").getValue().getValue();
				defaultExecMode = (String)inst.getProperty("defaultExecMode").getValue().getValue();
				defaultFilesystemType = (String)inst.getProperty("defaultFilesystemType").getValue().getValue();
				defaultFilesystem = (String)inst.getProperty("defaultFilesystem").getValue().getValue();
				defaultKernel = (String)inst.getProperty("defaultKernel").getValue().getValue();

			}
			else {
				throw new TIMTransformationException("No testbed parameters were found in CIMOM for VNUML");
			}
		}
		else {		
			specVersion = "1.8";
			defaultExecMode = "mconsole";
			defaultFilesystemType = "cow";
			defaultFilesystem = "/usr/share/vnuml/filesystems/root_fs_tutorial";
			defaultKernel = "/usr/share/vnuml/kernels/linux";
			defaultConsole = "xterm";
		}
	}
	
	/**
	 * Performs the TIM-to-VNUML transformation, returning a String enclosing a
	 * VNUML-compliant XML 
	 * 
	 * @param scenarioName the name of the scenario to be transformed
	 * @return the derived TSM model (VNUML-compliant scenario specification and ospf plugin XMLs)
	 * @throws TIMTransformationException
	 */	
	public Vector<String> toTsm(String scenarioName) throws TIMTransformationException {
		
		Vector<String> v = new Vector<String>(2);
		
		v.add(toTsmScenario(scenarioName));
		
		/* This check is performed because of OSPF is not mandatory */
		String ospfTsmElement = toTsmOspf(scenarioName);
		if (ospfTsmElement != "") {
			v.add(ospfTsmElement);
		}
		
		return v;

	}

	public String toTsmScenario(String scenarioName) throws TIMTransformationException {
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
			s.write(vnumlSpecHeader(scenarioName));
			s.write(vnumlSpecNets(scenario));
			s.write(vnumlSpecVms(scenario));
			s.write(vnumlSpecFooter());
		} catch (CIMException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		
		return s.toString();

	}
	
	private String vnumlSpecHeader(String scenarioName) {
		
		StringWriter s = new StringWriter();
		
		/* Generate the usual file header */
		s.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		s.write("<!DOCTYPE vnuml SYSTEM '/usr/share/xml/vnuml/vnuml.dtd'>\n");
		s.write("<vnuml>\n");		
			
		/* Generate the <global> section */
		/* FIXME: it can be improved */
		s.write("<global>\n");
		s.write("    <version>"+specVersion+"</version>\n");
		s.write("    <simulation_name>"+ scenarioName +"</simulation_name>\n");
		s.write("    <automac/>\n");
		s.write("    <vm_mgmt type='none' />\n");
		s.write("    <vm_defaults exec_mode='"+defaultExecMode+"'>\n");
		s.write("       <filesystem type='"+defaultFilesystemType+"'>"+defaultFilesystem+"</filesystem>\n");
		s.write("       <kernel>"+defaultKernel+"</kernel>\n");
		s.write("       <console id='0'>"+defaultConsole+"</console>\n");
		s.write("    </vm_defaults>\n");
		s.write("  </global>\n");
				
		return s.toString();
	}
	
	private String vnumlSpecNets(CIMInstance scenario) throws CIMException {
		
		StringWriter s = new StringWriter();
		
		/* TIM_TestbedScenario->CIM_ComputerSystem->CIM_IPProtocolEndpoint->TIM_ConnectivityConnection
		 * to get the networks and generate <net>
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
				 * (if loopback if) or one (if conventional if)
				 * element, which is consistent with the MOF Class
				 * definition for TIM. Loopback are not considered
				 */
				if (eee.hasMoreElements()) {
					CIMInstance lcc = (CIMInstance)eee.nextElement();
					//System.out.println(lcc.getObjectPath().toString());
					String net = (String)lcc.getProperty("InstanceID").getValue().getValue();
					ht.put(net,lcc);
				}

			}

		}
		
		/* We need to use the hashtable because of duplication */
		e = ht.elements();	
		while (e.hasMoreElements()) {
			CIMInstance lcc = (CIMInstance)e.nextElement();
			String net = (String)lcc.getProperty("InstanceID").getValue().getValue();
			//System.out.println(net);
			s.write("  <net name='"+net+"' mode='uml_switch'/>\n");
		}
		
		return s.toString();
	}
	
	private String vnumlSpecVms(CIMInstance scenario) throws CIMException {
		
		StringWriter s = new StringWriter();
		
		/* TIM_TestbedScenario->CIM_ComputerSystem to generate <vm> */
		Enumeration e = cc.associators(scenario.getObjectPath(), 
				"CIM_SystemComponent",
				"CIM_ComputerSystem", 
				"GroupComponent",
				"PartComponent", false, false, null);
		
		while(e.hasMoreElements()) {
			CIMInstance cs = (CIMInstance)e.nextElement();
			//System.out.println(cs.getObjectPath().toString());
			String name = (String)cs.getProperty("Name").getValue().getValue();
			s.write("  <vm name='"+name+"'>\n");
			
			s.write(vnumlSpecVmIfs(cs));
			s.write(vnumlSpecVmRoutes(cs));
			s.write(vnumlSpecVmForwarding(cs));
			
			s.write("  </vm>\n");
			
		}
		
		return s.toString();
	}
	
	private String vnumlSpecVmIfs(CIMInstance vm) throws CIMException {
		StringWriter s = new StringWriter();
		
		/* Generate <if> tags */
		Enumeration e = cc.associators(vm.getObjectPath(), 
				"CIM_HostedAccessPoint", 
				"CIM_IPProtocolEndpoint", 
				"Antecedent", 
				"Dependent", false, false, null);
		int ifId = 1;
		int ifLoopId = 1;
		while(e.hasMoreElements()) {
			CIMInstance ipe = (CIMInstance)e.nextElement();
			//System.out.println(ipe.getObjectPath().toString());

			Enumeration ee = cc.associators(ipe.getObjectPath(), 
					"TIM_MemberOfLink", 
					"TIM_LinkConnectivityCollection", 
					"Member", 
					"Collection", false, false, null);
			
			/* We are considering that the Enumeration has only zero
			 * (if loopback if) or one (if conventional if)
			 * element, which is consistent with the MOF Class
			 * definition for TIM
			 */
			if (ee.hasMoreElements()) {
				CIMInstance lcc = (CIMInstance)ee.nextElement();
				//System.out.println(lcc.getObjectPath().toString());
				String net = (String)lcc.getProperty("InstanceID").getValue().getValue();

				s.write("    <if id='"+ ifId++ +"' net='"+ net +"'>\n");
			}
			else {
				/* Since VNUML 1.8.4rc2 loopback interfaces can be defined in VNUML
				 * using the "lo" reserved name.
				 */
				s.write("    <if id='"+ ifLoopId++ +"' net='lo'>\n");
			}
			
			// Search for <ipv4> addresses
			ee = cc.associators(ipe.getObjectPath(), 
					"CIM_ElementSettingData", 
					"CIM_StaticIPAssignmentSettingData", 
					"ManagedElement", 
					"SettingData", false, false, null);
			while(ee.hasMoreElements()) {
				CIMInstance ip4 = (CIMInstance)ee.nextElement();
				//System.out.println(ip4.getObjectPath().toString());
				String ipAddr = (String)ip4.getProperty("IPv4Address").getValue().getValue();
				String mask = (String)ip4.getProperty("SubnetMask").getValue().getValue();
				s.write("      <ipv4 mask='"+mask+"'>"+ipAddr+"</ipv4>\n");
			}
			
			// Search for <ipv6> addresses
			ee = cc.associators(ipe.getObjectPath(), 
					"CIM_ElementSettingData", 
					"TIM_StaticIPv6AssignmentSettingData", 
					"ManagedElement", 
					"SettingData", false, false, null);
			while(ee.hasMoreElements()) {
				CIMInstance ip6 = (CIMInstance)ee.nextElement();
				//System.out.println(ip6.getObjectPath().toString());
				String ipAddr = (String)ip6.getProperty("IPv6Address").getValue().getValue();
				int length = ((UnsignedInt8)ip6.getProperty("PrefixLength").getValue().getValue()).intValue();
				s.write("      <ipv6 mask='/"+length+"'>"+ipAddr+"</ipv6>\n");
			}
			
			s.write("    </if>\n");
			
		}
		return s.toString();
	}
	
	private String vnumlSpecVmRoutes(CIMInstance vm) throws CIMException {
		StringWriter s = new StringWriter();
		
		/* Generate <route> tags */
		Enumeration e = cc.associators(vm.getObjectPath(), 
				"CIM_HostedRoute", 
				"TIM_NextHopAddressedIPRoute", 
				"Antecedent", 
				"Dependent", false, false, null);
		while(e.hasMoreElements()) {
			CIMInstance rt = (CIMInstance)e.nextElement();
			//System.out.println(rt.getObjectPath().toString());
			int family = ((UnsignedInt16)rt.getProperty("AddressType").getValue().getValue()).intValue();
			String dest = (String)rt.getProperty("DestinationAddress").getValue().getValue();
			String gw = (String)rt.getProperty("NextHopAddress").getValue().getValue();	
			if (family == CIMConstants.IPV4_RT_FAMILY) {
				String mask = (String)rt.getProperty("DestinationMask").getValue().getValue();
				s.write("    <route type='ipv4' gw='"+gw+"'>"+dest+"/"+translateMask(mask)+"</route>\n");
			}
			else {
				int mask = ((UnsignedInt8)rt.getProperty("PrefixLength").getValue().getValue()).intValue();
				s.write("    <route type='ipv6' gw='"+gw+"'>"+dest+"/"+mask+"</route>\n");
			}
		}
		return s.toString();
	}
	
	private String vnumlSpecVmForwarding(CIMInstance vm) throws CIMException {
		StringWriter s = new StringWriter();

		/* Generate forwarding. Given that in GNU/Linux system forwarding is a global 
		 * property of the entire system (AFAIK), we don't use CIM_ForwardsAmongs,
		 * but CIM_HostedService
		 */
		Enumeration e = cc.associators(vm.getObjectPath(), 
				"CIM_HostedService", 
				"CIM_ForwardingService", 
				"Antecedent", 
				"Dependent", false, false, null);
		/* We are using if instead of while because of only one CIM_ForwardingService
		 * make sense (note that a multiplicity of CIM_ForwardingServices would lead to
		 * multiple <forwarding> tags, which contradicts the VNUML DTD
		 */
		if(e.hasMoreElements()) {
			CIMInstance fw = (CIMInstance)e.nextElement();
			//System.out.println(fw.getObjectPath().toString());
			int family = ((UnsignedInt16)fw.getProperty("ProtocolType").getValue().getValue()).intValue();
			if (family == CIMConstants.IPV4_FW_FAMILY) {
				s.write("    <forwarding type='ipv4'/>\n");
			}
			else if (family == CIMConstants.IPV6_FW_FAMILY) {
				s.write("    <forwarding type='ipv6'/>\n");
			}
			else {
				s.write("    <forwarding type='ip'/>\n");
			}
		}
		
		return s.toString();
	}
	
	private String vnumlSpecFooter() {
		StringWriter s = new StringWriter();
		s.write("</vnuml>\n");
		return s.toString();
	}

	public String toTsmOspf(String scenarioName) throws TIMTransformationException {
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
			/* TIM_TestbedScenario->CIM_ComputerSystem */
			e = cc.associators(scenario.getObjectPath(), 
					"CIM_SystemComponent",
					"CIM_ComputerSystem", 
					"GroupComponent",
					"PartComponent", false, false, null);
			

			while(e.hasMoreElements()) {
				
				CIMInstance vm = (CIMInstance)e.nextElement();
				Enumeration ee = cc.associators(vm.getObjectPath(), 
						"CIM_HostedService", 
						"CIM_OSPFService", 
						"Antecedent", 
						"Dependent", false, false, null);				
				
				/* We are using if instead of while because of only one CIM_ForwardingService
				 * make sense (note that a multiplicity of CIM_OSPFService would lead to
				 * multiple <vm> tags with the same 'name', which contradicts the OSPF plugin
				 * way of workingg
				 */				
				if (ee.hasMoreElements()) {
					CIMInstance ospfService = (CIMInstance)ee.nextElement();
					// FIXME: unhardwire password
					String name = (String)vm.getProperty("Name").getValue().getValue();
					s.write("  <vm name='"+name+"' type='quagga' subtype='lib-install'>\n");
					s.write("    <zebra hostname='"+name+"' password='xxxx'/>\n");
					s.write(ospfNetworks(ospfService));
					s.write("  </vm>\n");
				}
				
			}
		} catch (CIMException ex) {
			ex.printStackTrace();
			throw new TIMTransformationException(ex);
		}
		
		/* Note that we put the <ospf_conf> at the end, because we need to check the empty-ness
		 * of the cumulative string after processing the CIM_ComputerSystems (note that returning 
		 * "" causes to not generate the second TSM element in those scenarios that don't 
		 * use OSPF at all) */
		if (!s.toString().equals("")) {
			return "<?xml version='1.0' encoding='UTF-8'?>\n" + 
			"\n" +
			"<ospf_conf>\n" + 
			s.toString() + 
			"</ospf_conf>\n";
		}
		else {
			return "";
		}		

	}
	
	private String ospfNetworks(CIMInstance ospfService) throws CIMException, TIMTransformationException {
		
		StringWriter s = new StringWriter();
		
		/* CIM_OSPFService-> CIM_OSPFAreaConfiguration */
		Enumeration e = cc.associators(ospfService.getObjectPath(), 
				"CIM_OSPFServiceConfiguration", 
				"CIM_OSPFAreaConfiguration", 
				"Antecedent", 
				"Dependent", false, false, null);
		while (e.hasMoreElements()) {
			CIMInstance ospfAreaConf = (CIMInstance) e.nextElement();
			
			int areaId;
			/* CIM_OSPFAreaConfiguration -> CIM_Area */
			Enumeration ee = cc.associators(ospfAreaConf.getObjectPath(), 
					"CIM_AreaOfConfiguration", 
					"CIM_OSPFArea", 
					"Dependent", 
					"Antecedent", false, false, null);
			
			/* We are using if instead of where because of, according to the intretation of the
			 * CIM Schema, only one CIM_OSPFArea instance is associated to 
			 * each CIM_OSPFAreaConfiguration */
			if (ee.hasMoreElements()) {
				CIMInstance ospfArea = (CIMInstance) ee.nextElement();
				areaId = ((UnsignedInt32)ospfArea.getProperty("AreaID").getValue().getValue()).intValue();
			}
			else {
				throw new TIMTransformationException("missing CIM_OSPFArea");
			}
			
			/* CIM_OSPFAreaConfiguration -> CIM_RangeOfIPAddresses */
			ee = cc.associators(ospfAreaConf.getObjectPath(), 
					"CIM_RangesOfConfiguration", 
					"CIM_RangeOfIPAddresses", 
					"Dependent", 
					"Antecedent", false, false, null);
			
			// FIXME: check EnableAdvertise
			while (ee.hasMoreElements()) {
				CIMInstance rangeIp = (CIMInstance) ee.nextElement();
				int addressType = ((UnsignedInt16)rangeIp.getProperty("AddressType").getValue().getValue()).intValue();				
				String startIp = (String)rangeIp.getProperty("StartAddress").getValue().getValue();
				String endIp = (String)rangeIp.getProperty("EndAddress").getValue().getValue();
				
				if (addressType != CIMConstants.IPV4_RANGE) {
					throw new TIMTransformationException("OSPF IPv6 ranges are not supported by the moment");
				}
		      
		      	s.write("    <network>\n");
				s.write("      <ip mask='"+IPManipulator.commonMask(IPManipulator.ip2int(startIp),IPManipulator.ip2int(endIp))+"'>"+startIp+"</ip>\n");
				s.write("      <area>"+IPManipulator.int2ip(areaId)+"</area>\n");
		      	s.write("    </network>\n");
			}
			
		}
		
		return s.toString();
	}
	
	/**
	 * This method it is due to limitations of VNUML parser (version 1.8.3)
	 * to use route destinations in the form X.X.X.X/M.M.M.M
	 */
	private String translateMask (String m) {
		// FIXME: only works by the moment for "8-bit dotted"
		if (m.equals("255.255.255.0"))
			return "24";
		if (m.equals("255.255.0.0"))
			return "16";
		if (m.equals("255.0.0.0"))
			return "8";
		if (m.equals("0.0.0.0"))
			return "0";
		
		return null;

	}
	
	public String getDefaultConsole() {
		return defaultConsole;
	}

	public void setDefaultConsole(String defaultConsole) {
		this.defaultConsole = defaultConsole;
	}

	public String getDefaultExecMode() {
		return defaultExecMode;
	}

	public void setDefaultExecMode(String defaultExecMode) {
		this.defaultExecMode = defaultExecMode;
	}

	public String getDefaultFilesystem() {
		return defaultFilesystem;
	}

	public void setDefaultFilesystem(String defaultFilesystem) {
		this.defaultFilesystem = defaultFilesystem;
	}

	public String getDefaultFilesystemType() {
		return defaultFilesystemType;
	}

	public void setDefaultFilesystemType(String defaultFilesystemType) {
		this.defaultFilesystemType = defaultFilesystemType;
	}

	public String getDefaultKernel() {
		return defaultKernel;
	}

	public void setDefaultKernel(String defaultKernel) {
		this.defaultKernel = defaultKernel;
	}

	public String getSpecVersion() {
		return specVersion;
	}

	public void setSpecVersion(String specVersion) {
		this.specVersion = specVersion;
	}
	
}
