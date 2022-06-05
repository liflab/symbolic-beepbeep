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
		Disjunction big_or = new Disjunction();
		{
			// First case: no reset until m; depends on current counter and position
			Conjunction and = new Conjunction();
			and.add(noResetBefore(0, m));
			Disjunction imp_or = new Disjunction();
			for (int c = 0; c < m_interval; c++)
			{
				if ((c + m) >= m_interval)
				{
					imp_or.add(new Equality(m_counter, new Constant(c)));
				}
			}
			and.add(imp_or);
			big_or.add(and);
		}
		// Other case: the last reset is at a distance greater than interval
		for (int i = 0; i <= m - m_interval ; i++)
		{
			big_or.add(isLastResetAt(next, 0, i, m));
		}
		return big_or;
	}
}
