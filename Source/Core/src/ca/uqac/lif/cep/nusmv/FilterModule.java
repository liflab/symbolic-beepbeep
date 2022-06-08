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

import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.ConstantFalse;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Negation;

/**
 * <strong>Caveat emptor:</strong> this modeling relies on the fact that
 * &bot; is the default value for the Boolean domain. This entails that:
 * <ol>
 * <li>Well-formed queues will contain &bot; as their value in empty
 * cells</li>
 * <li>Thus value &top; in the content array is sufficient to conclude that
 * this cell is non empty (no need to check for the flag array as well)
 * </ol>
 */
public class FilterModule extends BinaryModule
{
	public FilterModule(String name, Domain d, int Q_in, int Q_b, int Q_out)
	{
		super(name, d, BooleanDomain.instance, d, Q_in, Q_b, Q_out);
	}

	/*@ non_null @*/ public Condition buildInitialState(int Q_up, int Q_b)
	{
		Conjunction big_and = new Conjunction();
		big_and.add(emptyBuffers(false));
		// TODO
		return big_and;
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
		ProcessorQueue back_porch = getBackPorch();
		Conjunction and = new Conjunction();
		for (int i = 0; i <= back_porch.getSize(); i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(numTrueFronts(next, i));
			eq.add(back_porch.hasLength(next, i));
			and.add(eq);
		}
		return and;
	}
	
	/**
	 * Generates the condition stipulating that there are exactly n cells
	 * of the queue that contain the value &top;, up to and including index m.
	 * @param q The processor queue
	 * @param m The position in the queue
	 * @param n The number of true values
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasNTrue(ProcessorQueue q, int m, int n)
	{
		if (n - 1 > m)
		{
			// Impossible to have more than n-1 true events at position n
			return ConstantFalse.FALSE;
		}
		if (n == 0)
		{
			// Works if input is well-formed and ⊥ is default value
			if (m == 0)
			{
				return new Negation(q.booleanValueAt(0)); 	
			}
			Conjunction and = new Conjunction();
			for (int i = 0; i < m; i++)
			{
				and.add(new Negation(q.booleanValueAt(i)));
			}
			return and;
		}
		if (m == 0)
		{
			return q.booleanValueAt(0); 	
		}
		// n > 0, m > 0
		Disjunction or = new Disjunction();
		{
			Conjunction and = new Conjunction();
			and.add(q.booleanValueAt(m));
			and.add(hasNTrue(q, m - 1, n - 1));
			or.add(and);
		}
		{
			Conjunction and = new Conjunction();
			and.add(new Negation(q.booleanValueAt(m)));
			and.add(hasNTrue(q, m - 1, n));
			or.add(and);
		}
		return or;
	}
	
	/**
	 * Generates the condition stipulating that there are exactly n cells
	 * of the buffer that contain the true value, up to and including index m.
	 * @param queue_type 
	 * @param pipe_index The index of the input pipe
	 * @param m1 The position in the queue
	 * @param n The number of true values
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasNTrue(QueueType sigma, int pipe_index, int m, int n)
	{
		if (sigma == QueueType.BUFFER)
		{
			return hasNTrue(getBuffer(pipe_index), m, n);
		}
		return hasNTruePorch(pipe_index, m, n);
	}
	
	/**
	 * Generates the condition stipulating that there are exactly n cells
	 * of the input pipe (buffer + porch) that contain the true value, up to
	 * and including index m of the front porch.
	 * @param pipe_index The index of the input pipe
	 * @param m1 The position in the porch
	 * @param n The number of true values
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasNTruePorch(int pipe_index, int m, int n)
	{
		int Q_in = getFrontPorch(pipe_index).getSize();
		int Q_b = getBuffer(pipe_index).getSize();
		Disjunction or = new Disjunction();
		for (int i = 0; i <= n; i++)
		{
			int j = n - i;
			// i = num true in buffer, j = num true in porch
			if (!(j > m + 1 || j > Q_in || i > Q_b))
			{
				Conjunction and = new Conjunction();
				and.add(hasNTrue(getBuffer(pipe_index), Q_b - 1, i));
				and.add(hasNTrue(getFrontPorch(pipe_index), m, j));
				or.add(and);
			}
		}
		return or;
	}
	
	/**
	 * Generates the condition stipulating that there are exactly n cells
	 * of the input pipe (porch + buffer) that contain the true value, up 
	 * to the event at index m
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the queue
	 * @param n The number of true values
	 * @return The condition
	 */
	public Condition hasNTrue(boolean next, int pipe_index, int m, int n)
	{
		if (n - 1 > m)
		{
			// Impossible
			return ConstantFalse.FALSE;
		}
		ProcessorQueue buffer = getBuffer(pipe_index);
		int Q_b = buffer.getSize();
		Disjunction or = new Disjunction();
		for (int s_b = 0; s_b <= Q_b; s_b++)
		{
			Conjunction and = new Conjunction();
			and.add(buffer.hasLength(next, s_b));
			if (s_b > m)
			{
				// All events in buffer
				and.add(hasNTrue(QueueType.BUFFER, pipe_index, m, n));
			}
			else
			{
				int offset = m - s_b;
				and.add(hasNTrue(QueueType.PORCH, pipe_index, offset, n));
			}
			or.add(and);
		}
		return or;
	}
	
	/**
	 * Generates the condition stipulating that there are exactly n cells
	 * of the input pipe that contain the true value, up to and including
	 * index m of a given queue
	 * @param queue_type 
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the queue
	 * @param n The number of true values
	 * @return The condition
	 */
	public Condition isNthTrue(QueueType sigma, int pipe_index, int m, int n)
	{
		if (m > 0)
		{
			Conjunction and = new Conjunction();
			and.add(hasNTrue(sigma, pipe_index, m, n));
			and.add(hasNTrue(sigma, pipe_index, m - 1, n - 1));
			return and;
		}
		// m == 0
		if (sigma == QueueType.PORCH)
		{
			Conjunction and = new Conjunction();
			and.add(booleanAt(QueueType.PORCH, pipe_index, 0));
			and.add(hasNTrue(QueueType.BUFFER, pipe_index, getBuffer(pipe_index).getSize() - 1, n - 1));
			return and;
		}
		else
		{
			if (n > 1)
			{
				// n > 1, m == 0, sigma = buffer: impossible
				return ConstantFalse.FALSE;
			}
			return hasNTrue(getBuffer(pipe_index), m, n);
		}
	}

	@Override
	public Condition isFrontToOutput(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		int Q_out = getBackPorch().getSize();
		Disjunction or = new Disjunction();
		for (int nf = n; nf <= Q_out; nf++)
		{
			Conjunction left = new Conjunction();
			left.add(at(next, sigma1, 0, m1, nf));
			left.add(at(next, sigma2, 1, m2, nf));
			left.add(isNthTrue(sigma2, 1, m2, n + 1));
			or.add(left);
		}
		return or;
	}
	
	/**
	 * Produces the condition stipulating that 
	 * @param n The number of true fronts
	 * @return
	 */
	public Condition numTrueFronts(boolean next, int n)
	{
		int Q_out = getBackPorch().getSize();
		Disjunction or = new Disjunction();
		for (int nf = n; nf <= Q_out; nf++)
		{
			Conjunction and = new Conjunction();
			and.add(numFronts(next, nf));
			and.add(hasNTrue(next, 1, nf, n));
			or.add(and);
		}
		return or;
	}

	@Override
	public Condition getOutputCondition(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		return new Equality(getBackPorch().valueAt(next, n), at(next, sigma1, 0, m1));
	}
	
	@Override
	public FilterModule duplicate()
	{
		FilterModule m = new FilterModule(getName(), getFrontPorch(0).getDomain(), getFrontPorch(0).getSize(), getBuffer(0).getSize(), getBackPorch().getSize());
		super.copyInto(m);
		return m;
	}
}
