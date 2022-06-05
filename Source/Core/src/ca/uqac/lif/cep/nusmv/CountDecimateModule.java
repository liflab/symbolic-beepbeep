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
			and.add(noResetBefore(0, m));
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
		for (int i = m; i >= 0; i -= m_interval)
		{
			big_or.add(isLastResetAt(next, 0, i, m));
		}
		return big_or;
	}
	
	/**
	 * Produces the condition fixing the value of the internal counter in the
	 * next state.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextCounter()
	{
		int porch_size = getFrontPorch(0).getSize();
		Conjunction big_and = new Conjunction();
		for (int num_inputs = 0; num_inputs <= porch_size; num_inputs++)
		{
			Implication imp = new Implication();
			imp.add(hasInputs(num_inputs));
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
					and.add(noResetBefore(0, num_inputs - 1));
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
					for (int m = 0; m < num_inputs; m++)
					{
						// Or the last reset is at position m
						Conjunction in_and = new Conjunction();
						in_and.add(this.isLastResetAt(false, 0, m, num_inputs - 1));
						in_and.add(new Equality(m_counter.next(), new Constant((num_inputs - m) % m_interval)));
						or.add(in_and);
					}
				}
				imp.add(or);
			}
			big_and.add(imp);
		}		
		return big_and;
	}
}
