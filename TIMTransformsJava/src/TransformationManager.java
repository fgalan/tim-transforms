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
 */

import javax.wbem.cim.CIMException;
import javax.wbem.cim.CIMNameSpace;
import javax.wbem.client.CIMClient;
import javax.wbem.client.PasswordCredential;
import javax.wbem.client.UserPrincipal;

/**
 * Object of this class implements the comention to the CIMOM and the different
 * supported tranformation types.
 * 
 * @author Fermin Galan Marquez
 */
public class TransformationManager {
	
	private CIMClient cc;
	
	private VNUMLTransformation tVnuml;
	private AdrenalineTransformation tAdrenaline; 
	
	/**
	 * Class constructor, requiring the parameters used to establish CIMOM connection
	 * 
	 * @param cns the CIM name space to connect to CIMOM
	 * @param up the user name to connect to CIMOM
	 * @param pc the password to connect to CIMOM
	 * @param initializeFromCIMOM if true, the testbed parameters are got from CIMON when
	 * initializing the transformers, instead of using default values in the code
	 * @throws TIMTransformationException 
	 * @throws CIMException 
	 */
	public TransformationManager(CIMNameSpace cns, UserPrincipal up, PasswordCredential pc, boolean initializeFromCIMOM) throws TIMTransformationException {
		
		try {
			cc = new CIMClient(cns, up, pc);
		} catch (CIMException e) {
			e.printStackTrace();
			throw new TIMTransformationException(e);
		}
		tVnuml = new VNUMLTransformation(cc,initializeFromCIMOM);
		tAdrenaline = new AdrenalineTransformation(cc,initializeFromCIMOM);
		
	}
	
	/**
	 * Class constructor, requiring the parameters used to establish CIMOM connection
	 * 
	 * @param cns the CIM name space to connect to CIMOM
	 * @param up the user name to connect to CIMOM
	 * @param pc the password to connect to CIMOM
	 * @param initializeFromCIMOM if true, the testbed parameters are got from CIMON when
	 * initializing the transformers, instead of using default values in the code 
	 * @throws CIMException 
	 */
	public TransformationManager(String cns, String up, String pc, boolean initializeFromCIMOM) throws TIMTransformationException {
		this(new CIMNameSpace(cns),new UserPrincipal(up),new PasswordCredential(pc), initializeFromCIMOM);
	}

	/**
	 * Class constructor, requiring the parameters used to establish CIMOM connection.
	 * Udes and password are not specified in this case.
	 * 
	 * @param cns the CIM name space to connect to CIMOM
	 * @param up the user name to connect to CIMOM
	 * @param initializeFromCIMOM if true, the testbed parameters are got from CIMON when
	 * initializing the transformers, instead of using default values in the code	 * 
	 * @throws CIMException 
	 */
	public TransformationManager(String cns, boolean initializeFromCIMOM) throws TIMTransformationException {
		this (cns,"none","none",initializeFromCIMOM);
	}

	public VNUMLTransformation getVnumlTransformation() {
		return tVnuml;
	}
	
	public AdrenalineTransformation getAdrenalineTransformation() {
		return tAdrenaline;
	}
	
	protected void finalize () throws CIMException {
		cc.close();
	}
	
}
