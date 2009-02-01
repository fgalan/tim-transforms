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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This is the abstract class for TIM to TSM transformation.
 * Specific transformations for specific testbed are derived from it.
 * 
 * @author Fermin Galan Marquez
 *
 */
public abstract class TIMTransformation {

	/**
	 * Performs the TIM-to-TSM transformation, returning a String enclosing the TSM
	 * model.
	 * 
	 * @param testbedName the name of the testbed to be transformed
	 * @return the derived TSM model elements (each one a vector element). The semantic
	 * of each element is testbed-specific
	 * @throws TIMTransformationException
	 */
	public abstract Vector<String> toTsm(String testbedName) throws TIMTransformationException;
	
	/**
	 * Performs the TIM-to-TSM transformation, writing the TSM to a file.
	 * 
	 * @param testbedName the name of the testbed to be transformed
	 * @param vector of filename in which each element of the TSM model would be written
	 * @throws TIMTransformationException
	 */
	public void toTsm(String testbedName, Vector<String> fileNames) throws TIMTransformationException {
		
		Vector<String> tsm = toTsm(testbedName);
		if (tsm.size() != fileNames.size()) {
			throw new TIMTransformationException("TSM elements size ("+tsm.size()+
					") don't match fileNames vector size ("+fileNames.size()+")");
		}
		
		Enumeration<String> tsmElements = tsm.elements(); 
		for (Enumeration<String> e = fileNames.elements(); e.hasMoreElements() ; ) {
			String fileName = (String)e.nextElement();
			PrintStream ps = null;
			try {
				ps = new PrintStream(new FileOutputStream(fileName));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				throw new TIMTransformationException(ex);
			}
			ps.print((String)tsmElements.nextElement());
		}
	}
	
}
