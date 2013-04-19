package se.lnu.cs.doris.global;

/**
 * Enum representations of the flags that can be passed.
 * Used in switch for backwards compatibility reasons.
 * 
 * This file is a part of Doris
 *
 * Doris is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * Doris is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Doris.  
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 *  
 * @author Emil Carlsson
 * 
 */
public enum InputFlag {
	h, help, t, target, u, uri, n, nolog, s, startpoint, e, endpoint, l, limit, m, metric
}
