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

/**
 * This class model an specific exceptions type for TIM-to-TSM transformations.
 * It does not add any new functionality to Exception, from which is derived.
 * 
 * @author Fermin Galan Marquez
 *
 */
public class TIMTransformationException extends Exception {

	public TIMTransformationException(Throwable e) {
		super(e);
	}
	
	public TIMTransformationException(String msg) {
		super(msg);
	}
}
