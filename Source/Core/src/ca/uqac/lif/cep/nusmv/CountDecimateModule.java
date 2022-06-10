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
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ScalarVariable;

/**
 * Module reproducing the operation of the BeepBeep {@link CountDecimate}
 * processor.
 */
public class CountDecimateModule extends SubsetProcessorModule
{
	/**
	 * The internal variable keeping track of the current cumulative count.
	 */
	/*@ non_null @*/ protected final ScalarVariable m_counter;

	/**
	 * The decimation interval.
	 */
	/*@ non_null @*/ protected final int m_interval;

	public CountDecimateModule(String name, int interval, Domain d, int Q_in, int Q_out)
	{
		super(name, d, Q_in, Q_out);
		m_interval = interval;
		m_counter = new ScalarVariable("cnt", new IntegerRange(0, interval - 1));
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

	/**
	 * Produces the condition stipulating that the element in the input porch
	 * at position m should be output by the processor, based on its relative
	 * position with respect to the last reset and the current value of the
	 * internal counter.
	 * @param m The index in the input porch
	 * @return The condition
	 */
	@Override
	/*@ non_null @*/ public Condition shouldBeOutput(boolean next, int m)
	{
		Disjunction big_or = new Disjunction();
		{
			// Case where no reset occurs: depends on counter
			Conjunction and = new Conjunction();
			and.add(new NoReset(next));
			Disjunction imp_or = new Disjunction();
			for (int c = 0; c < m_interval; c++)
			{
				if ((c + m) % m_interval == 0)
				{
					imp_or.add(new Equality(m_counter, new Constant(c)));
				}
			}
			and.add(imp_or);
			big_or.add(and);
		}
		// Other case: a reset occurs and m % interval == 0
		if (m % m_interval == 0)
		{
			big_or.add(new IsReset(next));
		}
		return big_or;
	}

	/**
	 * Produces the condition fixing the value of the internal counter in the
	 * next state.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextCounter()
	{
		int porch_size = getFrontPorch(0).getSize();
		Conjunction big_and = new Conjunction();
		for (int num_inputs = 0; num_inputs <= porch_size; num_inputs++)
		{
			Implication imp = new Implication();
			imp.add(hasInputs(false, num_inputs));
			if (num_inputs == 0)
			{
				// Easy case: counter does not change
				imp.add(new Equality(m_counter, m_counter.next()));
			}
			else
			{
				Disjunction or = new Disjunction();
				{
					// Either there is no reset in the vector; new value of counter =
					// (current counter + input events) mod interval
					Conjunction and = new Conjunction();
					and.add(new NoReset(false));
					Disjunction in_or = new Disjunction();
					for (int c = 0; c < m_interval; c++)
					{
						Conjunction in_and = new Conjunction();
						in_and.add(new Equality(m_counter, new Constant(c)));
						in_and.add(new Equality(m_counter.next(), new Constant((c + num_inputs) % m_interval)));
						in_or.add(in_and);
					}
					and.add(in_or);
					or.add(and);
				}
				{
					// Or there is a reset and the next counter is equal to num_inputs % interval
					Conjunction in_and = new Conjunction();
					in_and.add(new IsReset(false));
					in_and.add(new Equality(m_counter.next(), new Constant((num_inputs) % m_interval)));
					or.add(in_and);
				}
				imp.add(or);
			}
			big_and.add(imp);
		}		
		return big_and;
	}

	@Override
	public CountDecimateModule duplicate()
	{
		CountDecimateModule m = new CountDecimateModule(getName(), m_interval, getFrontPorch(0).getDomain(), getFrontPorch(0).getSize(), getBackPorch().getSize());
		super.copyInto(m);
		return m;
	}
}
