/*
    Modeling of BeepBeep processor pipelines in NuSMV
    Copyright (C) 2020-2022 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Interface implemented by binary functions that can express the relation
 * between their two arguments and their output as a Boolean condition.  
 */
public interface UnaryFunctionCall
{
	/**
	 * Gets the domain of one of the arguments of the function.
	 * @return The domain
	 */
	public Domain getInputDomain();
	
	/**
	 * Gets the domain of the return value of the function.
	 * @return The domain
	 */
	public Domain getOutputDomain();
	
	/**
	 * Produces the condition linking the argument of the function to its
	 * output value. The condition is expressed in terms of x and y,
	 * and is expected to be true exactly for those values of x and y where
	 * f(x) = y. 
	 * @param x The argument of the function
	 * @param y The output of the function
	 * @return The condition
	 */
	public Condition getCondition(Term<?> x, Term<?> y);		
}