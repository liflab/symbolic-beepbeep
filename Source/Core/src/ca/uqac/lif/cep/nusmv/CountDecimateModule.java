package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;

/**
 * Module reproducing the operation of the BeepBeep {@link CountDecimate}
 * processor.
 */
public class CountDecimateModule extends UnaryProcessorModule
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
		super(name, d, d, Q_in, 0, Q_out);
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
	 * Produces the condition stipulating that the element in the input porch
	 * at position m corresponds to the element in the output porch at
	 * position n.
	 * @param m The index in the input porch
	 * @param n The index in the output porch
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isOutputAt(boolean next, int m, int n)
	{
		if (m == 0 && n == 0)
		{
			return shouldBeOutput(next, 0);
		}
		if (n == 0)
		{
			Conjunction and = new Conjunction();
			and.add(shouldBeOutput(next, m));
			for (int i = 0; i < m; i++)
			{
				and.add(new Negation(shouldBeOutput(next, i)));	
			}
			return and;
		}
		Conjunction and = new Conjunction();
		and.add(shouldBeOutput(next, m));
		Disjunction or = new Disjunction();
		for (int i = 0; i < m; i++)
		{
			or.add(isOutputAt(next, i, n - 1));
		}
		and.add(or);
		return and;
	}

	public Condition backPorchValues(boolean next)
	{
		ProcessorQueue front_porch = getFrontPorch(0);
		ProcessorQueue back_porch = getBackPorch();
		if (next)
		{
			front_porch = front_porch.next();
			back_porch = back_porch.next();
		}
		Conjunction and = new Conjunction();
		for (int i = 0; i < back_porch.getSize(); i++)
		{
			for (int j = 0; j < front_porch.getSize(); j++)
			{
				Implication eq = new Implication();
				eq.add(isOutputAt(next, j, i));
				Conjunction in_and = new Conjunction();
				{
					in_and.add(back_porch.hasAt(i));
					in_and.add(new Equality(front_porch.valueAt(j), back_porch.valueAt(i)));
				}
				eq.add(in_and);
				and.add(eq);
			}
		}
		return and;
	}
}
