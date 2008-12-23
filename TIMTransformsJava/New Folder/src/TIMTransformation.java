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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
	 * @return the derived TSM model
	 * @throws TIMTransformationException
	 */
	public abstract String toTsm(String testbedName) throws TIMTransformationException;
	
	/**
	 * Performs the TIM-to-TSM transformation, writing the TSM to a file.
	 * 
	 * @param testbedName the name of the testbed to be transformed
	 * @param fileName the filename in which the TSM model would be writted
	 * @throws TIMTransformationException
	 */
	public void toTsm(String testbedName, String fileName) throws TIMTransformationException {
		
		PrintStream ps = null;
		try {
			ps = new PrintStream(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new TIMTransformationException(e);
		}
		ps.print(toTsm(testbedName));
	}
	
}
