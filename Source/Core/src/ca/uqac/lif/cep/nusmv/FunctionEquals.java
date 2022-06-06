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

import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.ConstantTrue;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Binary function representing equality between two values.
 */
public class FunctionEquals implements BinaryFunctionCall
{
	/**
	 * The input domain for a particular instance of the function.
	 */
	protected Domain m_inDomain;
	
	/**
	 * Creates a new instance of the function.
	 * @param in_domain The input domain for a particular instance of the
	 * function
	 */
	public FunctionEquals(Domain in_domain)
	{
		super();
		m_inDomain = in_domain;
	}
	
	@Override
	public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
	{
		// Written as: (z = true) <-> (x = y)
		Equivalence eqv = new Equivalence();
		eqv.add(new Equality(z, ConstantTrue.TRUE));
		eqv.add(new Equality(x, y));
		return eqv;
	}

	@Override
	public Domain getInputDomain(int index) 
	{
		return m_inDomain;
	}

	@Override
	public Domain getOutputDomain() 
	{
		return BooleanDomain.instance;
	}

}
