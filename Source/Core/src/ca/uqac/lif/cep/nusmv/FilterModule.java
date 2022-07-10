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

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.BooleanVariableCondition;
import ca.uqac.lif.nusmv4j.Comment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.ConstantFalse;
import ca.uqac.lif.nusmv4j.ConstantTrue;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.IntegerRange;
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

	@Override
	protected void addToComment(Comment c)
	{
		c.addLine("Module: Filter");
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
			eq.add(numTrueFronts(next, i));
			eq.add(back_porch.hasLength(next, i));
			and.add(eq);
		}
		return and;
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
	public Condition isFrontToOutput(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		return new IsFrontToOutput(next, sigma1, m1, sigma2, m2, n);
	}

	protected class IsFrontToOutput extends Disjunction
	{
		protected final int m_m1;

		protected final int m_m2;

		protected final int m_n;

		protected final QueueType m_sigma1;

		protected final QueueType m_sigma2;

		protected final boolean m_next;

		public IsFrontToOutput(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
		{
			super();
			m_m1 = m1;
			m_m2 = m2;
			m_n = n;
			m_sigma1 = sigma1;
			m_sigma2 = sigma2;
			m_next = next;
			int Q_in = getFrontPorch(0).getSize();
			int Q_b = getBuffer(0).getSize();
			int Q_out = getBackPorch(0).getSize();
			for (int nf = n; nf <= Q_in + Q_b; nf++)
			{
				Conjunction left = new Conjunction();
				left.add(at(next, sigma1, 0, m1, nf));
				left.add(at(next, sigma2, 1, m2, nf));
				left.add(isNthTrue(next, sigma2, 1, m2, n + 1));
				add(left);
			}
		}

		@Override
		public String toString()
		{
			return "IsFrontToOutput((" + m_sigma1 + "," + m_m1 + "),(" + m_sigma2 + "," + m_m2 + ")," + m_n + ")" + (m_next ? "'" : "");
		}
	}

	/**
	 * Condition that stipulates that the m-th event of
	 * the input pipe (porch + buffer) is the n-th event with the
	 * value true.
	 */
	protected class IsNthTrue extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		protected final int m_m;

		protected final QueueType m_type;

		protected final int m_pipeIndex;

		public IsNthTrue(boolean next, QueueType type, int pipe_index, int m, int n)
		{
			super();
			m_m = m;
			m_n = n;
			m_next = next;
			m_type = type;
			m_pipeIndex = pipe_index;
			if (type == QueueType.PORCH && m == 0)
			{
				add(hasNTrue(next, type, pipe_index, m, 1));
				add(totalNTrueQueue(next, QueueType.BUFFER, pipe_index, n - 1));
			}
			else
			{
				add(hasNTrue(next, type, pipe_index, m, n));
				add(hasNTrue(next, type, pipe_index, m - 1, n - 1));
			}
		}

		@Override
		public String toString()
		{
			return "IsNthTrue(#" + m_pipeIndex + "," + m_type + "," + m_m + "," + m_n + ")" + (m_next ? "'" : "");
		}
	}

	public Condition hasNTrueQueue(boolean next, QueueType type, int pipe_index, int m, int n)
	{
		if (n > m + 1)
		{
			// Impossible
			return ConstantFalse.FALSE;
		}
		ProcessorQueue queue = (type == QueueType.BUFFER ? getBuffer(pipe_index) : getFrontPorch(pipe_index));
		if (m >= queue.getSize())
		{
			throw new QueueOutOfBoundsException("Queue has size " + queue.getSize() + ", asking for index " + m);
		}
		if (m == 0)
		{
			Conjunction and = new Conjunction();
			and.add(queue.minLength(next, 1));
			if (n == 0)
			{
				and.add(new Negation(queue.booleanValueAt(next, 0)));
			}
			else
			{
				and.add(queue.booleanValueAt(next, 0));
			}
			return and;
		}
		if (n == 0)
		{
			Conjunction and = new Conjunction();
			for (int i = 0; i <= m; i++)
			{
				and.add(new Negation(queue.booleanValueAt(next, i)));
			}
			return and;
		}
		return new HasNTrueQueue(next, type, pipe_index, m, n);
	}

	/**
	 * Condition that stipulates that up to and including the m-th event of
	 * a queue (porch or buffer), there are exactly n events in that queue
	 * with the value true.
	 */
	protected class HasNTrueQueue extends Disjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		protected final int m_m;

		protected final QueueType m_type;

		protected final int m_pipeIndex;

		public HasNTrueQueue(boolean next, QueueType type, int pipe_index, int m, int n)
		{
			super();
			m_m = m;
			m_n = n;
			m_next = next;
			m_type = type;
			m_pipeIndex = pipe_index;
			ProcessorQueue queue = (type == QueueType.BUFFER ? getBuffer(pipe_index) : getFrontPorch(pipe_index));
			{
				Conjunction and = new Conjunction();
				and.add(queue.minLength(next, m + 1));
				and.add(queue.booleanValueAt(next, m));
				and.add(hasNTrueQueue(next, type, pipe_index, m - 1, n - 1));
				add(and);
			}
			{
				Conjunction and = new Conjunction();
				and.add(queue.minLength(next, m + 1));
				and.add(new Negation(queue.booleanValueAt(next, m)));
				and.add(hasNTrueQueue(next, type, pipe_index, m - 1, n));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "QueueHasNTrue(#" + m_pipeIndex + "," + m_type + "," + m_m + "," + m_n + ")" + (m_next ? "'" : "");
		}

		@Override
		public Boolean evaluate(Assignment a) 
		{
			boolean b = super.evaluate(a);
			return b;
		}
	}

	protected Condition totalNTrueQueue(boolean next, QueueType type, int pipe_index, int n)
	{
		ProcessorQueue queue = (type == QueueType.BUFFER ? getBuffer(pipe_index) : getFrontPorch(pipe_index));
		return totalNTrueQueue(next, type, pipe_index, queue.getSize() - 1, n);
	}

	protected Condition totalNTrueQueue(boolean next, QueueType type, int pipe_index, int m, int n)
	{
		if (n > m + 1)
		{
			// Impossible
			return ConstantFalse.FALSE;
		}
		ProcessorQueue queue = (type == QueueType.BUFFER ? getBuffer(pipe_index) : getFrontPorch(pipe_index));
		if (m > queue.getSize())
		{
			throw new QueueOutOfBoundsException("Queue has size " + queue.getSize() + ", asking for index " + m);
		}
		if (m == 0)
		{
			if (n > 1)
			{
				return ConstantFalse.FALSE;
			}
			else if (n == 1)
			{
				return queue.booleanValueAt(next, 0);
			}
			else
			{
				return new Negation(queue.booleanValueAt(next, 0));
			}
		}
		if (n == 0)
		{
			Conjunction and = new Conjunction();
			for (int i = 0; i < queue.getSize(); i++)
			{
				and.add(new Negation(queue.booleanValueAt(next, i)));
			}
			return and;
		}
		return new TotalNTrueQueue(next, type, pipe_index, m, n);
	}

	/**
	 * Condition that stipulates that a queue contains exactly n events
	 * with the value true.
	 */
	protected class TotalNTrueQueue extends Disjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		protected final int m_m;

		protected final QueueType m_type;

		protected final int m_pipeIndex;

		public TotalNTrueQueue(boolean next, QueueType type, int pipe_index, int m, int n)
		{
			super();
			m_m = m;
			m_n = n;
			m_next = next;
			m_type = type;
			m_pipeIndex = pipe_index;
			ProcessorQueue queue = (type == QueueType.BUFFER ? getBuffer(pipe_index) : getFrontPorch(pipe_index));
			{
				Conjunction in_and = new Conjunction();
				in_and.add(queue.booleanValueAt(next, m));
				in_and.add(totalNTrueQueue(next, type, pipe_index, m - 1, n - 1));
				add(in_and);
			}
			{
				Conjunction in_and = new Conjunction();
				in_and.add(new Negation(queue.booleanValueAt(next, m)));
				in_and.add(totalNTrueQueue(next, type, pipe_index, m - 1, n));
				add(in_and);
			}
		}

		@Override
		public String toString()
		{
			return "QueueTotalNTrue(#" + m_pipeIndex + "," + m_type + "," + m_m + "," + m_n + ")" + (m_next ? "'" : "");
		}

		@Override
		public Boolean evaluate(Assignment a) 
		{
			boolean b = super.evaluate(a);
			return b;
		}
	}

	/**
	 * Condition that stipulates that up to and including the m-th event of
	 * a queue (porch or buffer), there are exactly n events in the full
	 * input pipe (porch + buffer) with the value true.
	 */
	public Condition hasNTrue(boolean next, QueueType type, int pipe_index, int m, int n)
	{
		if (type == QueueType.BUFFER)
		{
			return hasNTrueQueue(next, QueueType.BUFFER, pipe_index, m, n);
		}
		return new HasNTruePorch(next, pipe_index, m, n);
	}

	protected class HasNTruePorch extends Disjunction
	{
		protected final boolean m_next;

		protected final int m_pipeIndex;

		protected final int m_m;

		protected final int m_n;

		public HasNTruePorch(boolean next, int pipe_index, int m, int n)
		{
			super();
			m_next = next;
			m_pipeIndex = pipe_index;
			m_m = m;
			m_n = n;
			int buffer_size = getBuffer(pipe_index).getSize();
			for (int bf_t = 0; bf_t < n; bf_t++)
			{
				Conjunction and = new Conjunction();
				// The queue contains bf_t true events
				and.add(totalNTrueQueue(next, QueueType.BUFFER, pipe_index, buffer_size - 1, bf_t));
				int remaining_t = n - bf_t;
				// The porch up to position m contains the remaining ones
				and.add(hasNTrueQueue(next, QueueType.PORCH, pipe_index, m, remaining_t));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "HasNTruePorch(#" + m_pipeIndex + "," + m_m + "," + m_n + ")" + (m_next ? "'" : "");
		}
	}

	public Condition isNthTrue(boolean next, QueueType type, int pipe_index, int m, int n)
	{
		if (m > n + 1)
		{
			return ConstantFalse.FALSE;
		}
		return new IsNthTrue(next, type, pipe_index, m, n);
	}

	/**
	 * Produces the condition stipulating that 
	 * @param n The number of true fronts
	 * @return
	 */
	public Condition numTrueFronts(boolean next, int n)
	{
		return new NumTrueFronts(next, n);
	}

	protected class NumTrueFronts extends Disjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		public NumTrueFronts(boolean next, int n)
		{
			super();
			m_next = next;
			m_n = n;
			int Q_in = getFrontPorch(0).getSize();
			int Q_out = getBackPorch(0).getSize();
			int Q_b = getBuffer(0).getSize();
			for (int nf = n; nf <= Q_out; nf++)
			{
				Conjunction and = new Conjunction();
				and.add(numFronts(next, nf));
				Disjunction or = new Disjunction();
				for (int i = 0; i < nf; i++)
				{
					Conjunction in_and = new Conjunction();
					in_and.add(hasNTrueQueue(next, QueueType.BUFFER, 1, Q_b - 1, i));
					in_and.add(hasNTrueQueue(next, QueueType.PORCH, 1, Q_in - 1, nf - i));
					or.add(in_and);
				}
				and.add(or);
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "NumTrueFronts(" + m_n + ")" + (m_next ? "'" : "");
		}

		@Override
		public Boolean evaluate(Assignment a)
		{
			boolean b = super.evaluate(a);
			return b;
		}
	}

	@Override
	public Condition getOutputCondition(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		return new Equality(getBackPorch(0).valueAt(next, n), valueAt(next, sigma1, 0, m1));
	}

	@Override
	public FilterModule duplicate()
	{
		FilterModule m = new FilterModule(getName(), getFrontPorch(0).getDomain(), getFrontPorch(0).getSize(), getBuffer(0).getSize(), getBackPorch(0).getSize());
		super.copyInto(m);
		return m;
	}

	@Override
	public IntegerRange getOutputRange(IntegerRange... ranges)
	{
		return new IntegerRange(0, Math.min(ranges[0].getUpperBound(), ranges[1].getUpperBound()) + getBuffer(0).getSize());
	}
}
