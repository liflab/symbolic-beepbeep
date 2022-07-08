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
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Unary function that returns the same output value regardless of its
 * argument.
 */
public class TurnIntoFunction implements UnaryFunctionCall
{
	/**
	 * The input domain for a particular instance of the function.
	 */
	protected final Domain m_inputDomain;
	
	/**
	 * The output domain for a particular instance of the function.
	 */
	protected final Domain m_outputDomain;
	
	/**
	 * The value to output.
	 */
	protected final Object m_value;
	
	/**
	 * Creates a new instance of the function.
	 * @param in_domain The input domain for a particular instance of the
	 * function
	 * @param out_domain The output domain for a particular instance of the
	 * function
	 * @param value The value to output
	 */
	public TurnIntoFunction(Domain in_domain, Domain out_domain, Object value)
	{
		super();
		m_inputDomain = in_domain;
		m_outputDomain = out_domain;
		m_value = value;
	}
	
	/**
	 * Gets the value that this function outputs.
	 * @return The value
	 */
	public Object getValue()
	{
		return m_value;
	}
	
	@Override
	public Domain getInputDomain()
	{
		return m_inputDomain;
	}
	
	@Override
	public Domain getOutputDomain() 
	{
		return m_outputDomain;
	}

	@Override
	public Condition getCondition(Term<?> x, Term<?> y)
	{
		return new Equality(y, new Constant(m_value));
	}
}
