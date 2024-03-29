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
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.IntegerRange;

public class BinaryApplyFunctionModule extends BinaryModule
{
	/**
	 * The function to be applied on event fronts.
	 */
	/*@ non_null @*/ protected final BinaryFunctionCall m_function;

	public BinaryApplyFunctionModule(String name, BinaryFunctionCall f, int Q_in, int Q_b, int Q_out)
	{
		super(name, f.getInputDomain(0), f.getInputDomain(1), f.getOutputDomain(), Q_in, Q_b, Q_out);
		m_function = f;
	}
	
	@Override
	protected void addToComment(Comment c)
	{
		c.addLine("Module: ApplyFunction(" + m_function + ")");
	}

	/**
	 * Generates the condition stipulating that the size of the back porch is
	 * equal to the number of complete event fronts in the input pipes.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition frontsVsBackPorch(boolean next)
	{
		ProcessorQueue back_porch = getBackPorch(0);
		Conjunction and = new Conjunction();
		for (int i = 0; i <= back_porch.getSize(); i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(numFronts(next, i));
			eq.add(back_porch.hasLength(next, i));
			and.add(eq);
		}
		return and;
	}

	@Override
	public Condition isFrontToOutput(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		return isNthFront(next, sigma1, m1, sigma2, m2, n);
	}

	@Override
	public Condition getOutputCondition(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		Condition right = m_function.getCondition(
				valueAt(next, sigma1, 0, m1), // first argument of f
				valueAt(next, sigma2, 1, m2), // second argument of f
				getBackPorch(0).valueAt(next, n) // cell of the back porch to store value
				);
		return right;
	}
	
	@Override
	public BinaryApplyFunctionModule duplicate()
	{
		BinaryApplyFunctionModule m = new BinaryApplyFunctionModule(getName(), m_function, getFrontPorch(0).getSize(), getBuffer(0).getSize(), getBackPorch(0).getSize());
		super.copyInto(m);
		return m;
	}

	@Override
	protected void addToInit(Conjunction c)
	{
		super.addToInit(c);
		c.add(frontsVsBackPorch(false));
	}

	@Override
	protected void addToTrans(Conjunction c) 
	{
		super.addToTrans(c);
		c.add(frontsVsBackPorch(true));		
	}

	@Override
	public IntegerRange getOutputRange(IntegerRange ... ranges)
	{
		int lower = Math.min(ranges[0].getLowerBound(), ranges[1].getLowerBound());
		int upper = Math.min(ranges[0].getUpperBound(), ranges[1].getUpperBound() + getBuffer(0).getSize());
		return new IntegerRange(lower, upper);
	}
}
