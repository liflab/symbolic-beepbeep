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

/**
 * NuSMV module representing processors having an input arity of 1. These
 * processors ingest all the events of their front porch at any computation
 * step, hence they never need an internal buffer. This simplifies the
 * expression of some conditions; for instance, asserting that the processor
 * has <i>n</i> inputs ready to be consumed is equivalent to stating that
 * its front porch contains exactly <i>n</i> elements. 
 */
public abstract class UnaryProcessorModule extends ProcessorModule
{
	public UnaryProcessorModule(String name, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, 1, new Domain[] {in_domain}, out_domain, Q_in, Q_b, Q_out);
	}
	
	/**
	 * Produces the condition stipulating that the unary processor has at least
	 * <i>n</i> inputs ready to be consumed.
	 * @param n The number of input events
	 * @return The condition
	 */
	/*@ pure non_null @*/ public Condition minInputs(int n)
	{
		return m_frontPorches[0].minLength(n);
	}
	
	/**
	 * Produces the condition stipulating that the unary processor has exactly
	 * <i>n</i> inputs ready to be consumed.
	 * @param n The number of input events
	 * @return The condition
	 */
	/*@ pure non_null @*/ public Condition hasInputs(int n)
	{
		return m_frontPorches[0].hasLength(n);
	}

}
