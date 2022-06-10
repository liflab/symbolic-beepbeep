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
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ScalarVariable;

public class TrimModule extends SubsetProcessorModule
{
	/**
	 * The internal variable keeping track of the current cumulative count.
	 */
	/*@ non_null @*/ protected final ScalarVariable m_counter;

	/**
	 * The number of events to trim.
	 */
	/*@ non_null @*/ protected final int m_interval;
	
	public TrimModule(String name, int interval, Domain d, int Q_in, int Q_out)
	{
		super(name, d, Q_in, Q_out);
		m_interval = interval;
		m_counter = new ScalarVariable("cnt", new IntegerRange(0, interval));
	}
	
	/**
	 * Gets the internal variable acting as the processor's counter.
	 * @return The variable
	 */
	/*@ pure non_null @*/ public ScalarVariable getCounter()
	{
		return m_counter;
	}

	/**
	 * Gets the decimation interval of the processor.
	 * @return The interval
	 */
	/*@ pure @*/ public int getInterval()
	{
		return m_interval;
	}

	@Override
	public Condition shouldBeOutput(boolean next, int m)
	{
		ScalarVariable counter = m_counter;
		if (next)
		{
			counter = m_counter.next();
		}
		Disjunction big_or = new Disjunction();
		{
			// First case: no reset; depends on current counter and position
			Conjunction and = new Conjunction();
			and.add(new NoReset(next));
			Disjunction imp_or = new Disjunction();
			for (int c = 0; c < m_interval; c++)
			{
				if ((c + m) >= m_interval)
				{
					imp_or.add(new Equality(counter, new Constant(c)));
				}
			}
			and.add(imp_or);
			big_or.add(and);
		}
		if (m >= m_interval)
		{
			big_or.add(new IsReset(next));
		}
		return big_or;
	}
	
	@Override
	public TrimModule duplicate()
	{
		TrimModule m = new TrimModule(getName(), m_interval, getFrontPorch(0).getDomain(), getFrontPorch(0).getSize(), getBackPorch().getSize());
		super.copyInto(m);
		return m;
	}
}
