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

import ca.uqac.lif.nusmv4j.Comment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;
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

	public CumulateModule(String name, BinaryFunctionCall f, int Q_in, int Q_out)
	{
		super(name, f.getInputDomain(0), f.getOutputDomain(), Q_in, 0, Q_out);
		m_function = f;
		m_counter = new ScalarVariable("cnt", f.getOutputDomain());
		add(m_counter);
	}
	
	@Override
	protected void addToComment(Comment c)
	{
		c.addLine("Module: Cumulate(" + m_function + ")");
	}

	/**
	 * Gets the internal variable acting as the processor's counter.
	 * @return The variable
	 */
	/*@ pure non_null @*/ public ScalarVariable getCounter()
	{
		return m_counter;
	}
	
	@Override
	protected void addToInit(Conjunction c)
	{
		c.add(new Equality(m_counter, new Constant(m_counter.getDomain().getDefaultValue())));
		c.add(backPorchSize(false));
		c.add(backPorchValues(false));
	}
	
	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(nextCounter());
		c.add(backPorchSize(true));
		c.add(backPorchValues(true));
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
		ProcessorQueue back_porch = getBackPorch(0);
		for (int i = 0; i <= front_porch.getSize(); i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(front_porch.hasLength(next, i));
			eq.add(back_porch.hasLength(next, i));
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
		ProcessorQueue back_porch = getBackPorch(0);
		for (int i = 0; i <= back_porch.getSize() - 1; i++)
		{
			Implication imp = new Implication();
			imp.add(front_porch.hasAt(next, i));
			{
				Conjunction in_and = new Conjunction();
				{
					if (i == 0)
					{
						{
							Implication in_imp = new Implication();
							in_imp.add(new IsReset(next));
							in_imp.add(m_function.getCondition(new Constant(m_function.getInputDomain(0).getDefaultValue()), front_porch.valueAt(next, i), back_porch.valueAt(next, i)));
							in_and.add(in_imp);
						}
						{
							Implication in_imp = new Implication();
							in_imp.add(new NoReset(next));
							in_imp.add(m_function.getCondition(m_counter, front_porch.valueAt(next, i), back_porch.valueAt(next, i)));
							in_and.add(in_imp);
						}
					}
					else
					{
						in_and.add(m_function.getCondition(back_porch.valueAt(next, i - 1), front_porch.valueAt(next, i), back_porch.valueAt(next, i)));
					}
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
		ProcessorQueue back_porch = getBackPorch(0);
		Conjunction big_and = new Conjunction();
		for (int nf = 1; nf <= back_porch.getSize(); nf++)
		{
			Implication imp = new Implication();
			imp.add(hasInputs(false, nf));
			imp.add(new Equality(
					m_counter.next(),
					back_porch.valueAt(false, nf - 1)));
			big_and.add(imp);
		}
		{
			// Counter stays unchanged if no inputs
			Implication imp = new Implication();
			imp.add(hasInputs(false, 0));
			imp.add(new Equality(
					m_counter.next(),
					m_counter));
			big_and.add(imp);
		}
		return big_and;
	}

	@Override
	public CumulateModule duplicate()
	{
		CumulateModule m = new CumulateModule(getName(), m_function, getFrontPorch(0).getSize(), getBackPorch(0).getSize());
		super.copyInto(m);
		return m;
	}
	
	@Override
	public String toString()
	{
		return "Cumulate(" + m_function + ")";
	}
}
