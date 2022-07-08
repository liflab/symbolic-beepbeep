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

import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.IntegerRange;

/**
 * Module that replicates the contents of its front porch to a number of back
 * porches.
 */
public class ForkModule extends ProcessorModule
{
	public ForkModule(String name, Domain d, int out_arity, int Q_in)
	{
		super(name, 1, new Domain[] {d}, out_arity, replicateDomains(d, out_arity), true, Q_in, 0, Q_in);
	}
	
	/**
	 * Creates an array with multiple copies of the same domain.
	 * @param d The domain
	 * @param times The size of the array
	 * @return The array
	 */
	protected static Domain[] replicateDomains(Domain d, int times)
	{
		Domain[] out = new Domain[times];
		for (int i = 0; i < times; i++)
		{
			out[i] = d;
		}
		return out;
	}

	@Override
	public ForkModule duplicate()
	{
		return new ForkModule(getName(), m_frontPorches[0].getDomain(), m_backPorches.length, m_frontPorches[0].getSize());
	}

	@Override
	protected void addToInit(Conjunction c)
	{
		c.add(new AllEqual(false));
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(new AllEqual(true));
	}
	
	protected class AllEqual extends Conjunction
	{
		protected final boolean m_next;
		
		public AllEqual(boolean next)
		{
			super();
			m_next = next;
			ArrayVariable f_c = m_frontPorches[0].m_arrayContents;
			ArrayVariable f_b = m_frontPorches[0].m_arrayFlags;
			for (int i = 0; i < m_backPorches.length; i++)
			{
				ArrayVariable b_c = m_backPorches[i].m_arrayContents;
				ArrayVariable b_b = m_backPorches[i].m_arrayFlags;
				for (int j = 0; j < f_c.getDimension(); j++)
				{
					add(new Equality(f_c.at(next, j), b_c.at(next, j)));
					add(new Equality(f_b.at(next, j), b_b.at(next, j)));
				}	
			}
		}
		
		@Override
		public String toString()
		{
			return "AllEqual" + (m_next ? "'" : "");
		}
	}

	@Override
	public IntegerRange getOutputRange(IntegerRange... ranges) 
	{
		return ranges[0];
	}      
}
