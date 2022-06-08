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
import ca.uqac.lif.nusmv4j.ConstantFalse;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.Negation;

/**
 * Base module for all 1:1 processors whose output is made from a subset of the
 * input. Descendants of this class simply need to implement
 * {@link #shouldBeOutput(boolean, int)}, which defines the logic by which a
 * specific processor picks the events of the input are part of the output.
 */
public abstract class SubsetProcessorModule extends UnaryProcessorModule
{
	public SubsetProcessorModule(String name, Domain d, int Q_in, int Q_out)
	{
		super(name, d, d, Q_in, 0, Q_out);
	}
	
	/**
	 * Produces the condition stipulating that up to and including position m,
	 * there are n events that are sent to the output.
	 * @param m The position in the input queue
	 * @param n The number of input events
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition numOutputs(boolean next, int m, int n)
	{
		if (n > m)
		{
			// Impossible to output n events from m inputs if n > m
			return ConstantFalse.FALSE;
		}
		if (n == 0)
		{
			// n = 0, so no event should be output from index 0 up to m
			if (m == 0)
			{
				return new Negation(shouldBeOutput(next, 0));
			}
			Conjunction and = new Conjunction();
			for (int i = 0; i <= m; i++)
			{
				and.add(new Negation(shouldBeOutput(next, i)));
			}
			return and;
		}
		// n > 0
		Disjunction or = new Disjunction();
		{
			// Either m should be output, and there are n-1 outputs up until m-1
			Conjunction and = new Conjunction();
			and.add(shouldBeOutput(next, m));
			and.add(numOutputs(next, m - 1, n - 1));
			or.add(and);
		}
		{
			// Or m should not be output, and then there are n outputs up to m-1
			Conjunction and = new Conjunction();
			and.add(new Negation(shouldBeOutput(next, m)));
			and.add(numOutputs(next, m - 1, n));
			or.add(and);
		}
		return or;
	}
	
	/**
	 * Produces the condition stipulating that the number of elements in the
	 * back porch is equal to the number of events in the input that should be
	 * output.
	 * @param next
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition backPorchLength(boolean next)
	{
		ProcessorQueue back_porch = getBackPorch();
		if (next)
		{
			back_porch = back_porch.next();
		}
		Conjunction and = new Conjunction();
		int last_pos = back_porch.getSize() - 1;
		for (int i = 0; i <= back_porch.getSize(); i++)
		{
			Implication imp = new Implication();
			imp.add(numOutputs(next, last_pos, i));
			imp.add(back_porch.hasLength(next, i));
			and.add(imp);
		}
		return and;
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
		if (n > m)
		{
			// Impossible to output n events from m inputs if n > m
			return ConstantFalse.FALSE;
		}
		if (m == 0 && n == 0)
		{
			return shouldBeOutput(next, m);
		}
		Conjunction and = new Conjunction();
		and.add(numOutputs(next, m, n + 1));
		and.add(numOutputs(next, m - 1, n));
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
					in_and.add(back_porch.hasAt(next, i));
					in_and.add(new Equality(front_porch.valueAt(next, j), back_porch.valueAt(next, i)));
				}
				eq.add(in_and);
				and.add(eq);
			}
		}
		return and;
	}
	
	/**
	 * Produces the condition stipulating that the element in the input porch
	 * at position m should be output by the processor.
	 * @param m The index in the input porch
	 * @return The condition
	 */
	/*@ non_null @*/ public abstract Condition shouldBeOutput(boolean next, int m);
}
