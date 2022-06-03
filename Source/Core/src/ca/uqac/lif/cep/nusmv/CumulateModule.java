package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;

/**
 * Module reproducing the operation of the BeepBeep {@link Cumulate}
 * processor.
 */
public class CumulateModule extends UnaryProcessorModule
{
	/**
	 * The internal variable keeping track of the current cumulative count.
	 */
	/*@ non_null @*/ protected final ScalarVariable m_counter;
	
	/**
	 * The binary function to be applied on the input values.
	 */
	/*@ non_null @*/ protected final BinaryFunctionCall m_function;
	
	public CumulateModule(String name, BinaryFunctionCall f, Domain d, int Q_in, int Q_out)
	{
		super(name, d, d, Q_in, 0, Q_out);
		m_function = f;
		m_counter = new ScalarVariable("cnt", d);
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
	 * Produces the condition stipulating that the number of events in the
	 * front porch is equal to the number of events in the front porch.
	 * @param next A flag indicating if the condition is expressed on the next
	 * state of the processor
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition backPorchSize(boolean next)
	{
		Conjunction and = new Conjunction();
		ProcessorQueue front_porch = getFrontPorch(0);
		ProcessorQueue back_porch = getBackPorch();
		if (next)
		{
			front_porch = front_porch.next();
			back_porch = back_porch.next();
		}
		for (int i = 0; i <= front_porch.getSize(); i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(front_porch.hasLength(i));
			eq.add(back_porch.hasLength(i));
			and.add(eq);
		}
		return and;
	}
	
	/**
	 * Produces the condition stipulating the value of each cell in the
	 * back porch, based on the current value of the internal counter, and the
	 * contents of the front porch and reset queue.
	 * @param next A flag indicating if the condition is expressed on the next
	 * state of the processor
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition backPorchValues(boolean next)
	{
		Conjunction and = new Conjunction();
		ProcessorQueue front_porch = getFrontPorch(0);
		ProcessorQueue back_porch = getBackPorch();
		if (next)
		{
			front_porch = front_porch.next();
			back_porch = back_porch.next();
		}
		for (int i = 0; i <= back_porch.getSize() - 1; i++)
		{
			Implication imp = new Implication();
			imp.add(front_porch.hasAt(i));
			{
				Conjunction in_and = new Conjunction();
				{
					Implication in_imp = new Implication();
					in_imp.add(isResetAt(next, 0, i));
					in_imp.add(m_function.getCondition(new Constant(front_porch.getDomain().getDefaultValue()), front_porch.valueAt(i), back_porch.valueAt(i)));
					in_and.add(in_imp);
				}
				{
					Implication in_imp = new Implication();
					in_imp.add(new Negation(isResetAt(next, 0, i)));
					if (i == 0)
					{
						in_imp.add(m_function.getCondition(m_counter, front_porch.valueAt(i), back_porch.valueAt(i)));
					}
					else
					{
						in_imp.add(m_function.getCondition(back_porch.valueAt(i - 1), front_porch.valueAt(i), back_porch.valueAt(i)));
					}
					in_and.add(in_imp);
				}
				imp.add(in_and);
			}
			and.add(imp);
		}
		return and;
	}
	
	/**
	 * Produces the condition determining the value of the internal counter in
	 * the next computation step, based on the current value of the internal
	 * counter, and the contents of the front porch and reset queue.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextCounter()
	{
		ProcessorQueue back_porch = getBackPorch();
		Conjunction big_and = new Conjunction();
		for (int nf = 1; nf <= back_porch.getSize(); nf++)
		{
			Implication imp = new Implication();
			imp.add(hasInputs(nf));
			imp.add(new Equality(
					m_counter.next(),
					back_porch.valueAt(nf - 1)));
			big_and.add(imp);
		}
		{
			// Counter stays unchanged if no inputs
			Implication imp = new Implication();
			imp.add(hasInputs(0));
			imp.add(new Equality(
					m_counter.next(),
					m_counter));
			big_and.add(imp);
		}
		return big_and;
	}
}
