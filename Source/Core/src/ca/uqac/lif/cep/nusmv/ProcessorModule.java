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

import static ca.uqac.lif.nusmv4j.ConstantFalse.FALSE;

import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.BooleanVariableCondition;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.LogicModule;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Abstract representation of a BeepBeep processor as a collection of queues
 * called front porch, internal buffer and back porch.
 */
public abstract class ProcessorModule extends LogicModule
{
	protected enum QueueType {PORCH, BUFFER}

	/**
	 * The front porches of this processor.
	 */
	protected final ProcessorQueue[] m_frontPorches;

	/**
	 * The reset porches of this processor.
	 */
	protected final ScalarVariable m_resetFlag;

	/**
	 * The internal buffers of this processor.
	 */
	protected final ProcessorQueue[] m_buffers;

	/**
	 * The back porch of this processor.
	 */
	protected final ProcessorQueue m_backPorch;


	public ProcessorModule(String name, int in_arity, Domain[] in_domains, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name);
		m_frontPorches = new ProcessorQueue[in_arity];
		m_resetFlag = new ScalarVariable("rst", BooleanDomain.instance);
		for (int i = 0; i < in_arity; i++)
		{
			m_frontPorches[i] = new ProcessorQueue("in_" + i, new ArrayVariable("inc_" + i, in_domains[i], Q_in), new ArrayVariable("inb_" + i, BooleanDomain.instance, Q_in));
		}
		m_buffers = new ProcessorQueue[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_buffers[i] = new ProcessorQueue("bf_" + i, new ArrayVariable("bfc_" + i, m_frontPorches[i].m_arrayContents.getDomain(), Q_b), new ArrayVariable("bfb_" + i, BooleanDomain.instance, Q_b));
		}
		m_backPorch = new ProcessorQueue("ou", new ArrayVariable("ouc", out_domain, Q_out), new ArrayVariable("oub", BooleanDomain.instance, Q_out));
	}
	
	@Override
	public Conjunction getInit()
	{
		Conjunction and_init = new Conjunction();
		for (int i = 0; i < getInputArity(); i++)
		{
			and_init.add(m_buffers[i].hasLength(false, 0));
		}
		return and_init;
	}
	
	@Override
	public Conjunction getTrans()
	{
		Conjunction and = new Conjunction();
		return and;
	}
	
	/**
	 * Gets the input arity of this processor module.
	 * @return The input arity
	 */
	public int getInputArity()
	{
		return m_buffers.length;
	}

	/**
	 * Sets the processor queue corresponding to the processor's front porch
	 * at a given position in its input pipes.
	 * @param q The processor queue
	 * @param position The position
	 * @return This module
	 */
	public ProcessorModule setFrontPorch(ProcessorQueue q, int position)
	{
		m_frontPorches[position] = q;
		return this;
	}

	/**
	 * Sets the processor queue corresponding to the processor's internal
	 * buffer at a given position in its input pipes.
	 * @param q The processor queue
	 * @param position The position
	 * @return This module
	 */
	public ProcessorModule setBuffer(ProcessorQueue q, int position)
	{
		m_buffers[position] = q;
		return this;
	}

	/**
	 * Gets the processor queue corresponding to the processor's front porch
	 * at a given position.
	 * @param index The index of the input pipe
	 * @return The queue
	 */
	public ProcessorQueue getFrontPorch(int index)
	{
		return m_frontPorches[index];
	}

	/**
	 * Gets the processor variable corresponding to the processor's reset flag.
	 * @return The variable
	 */
	public ScalarVariable getResetFlag()
	{
		return m_resetFlag;
	}
	
	/**
	 * Gets the processor variable corresponding to the processor's reset flag.
	 * @param next A flag to get the variable in the current or the next state
	 * @return The variable
	 */
	protected ScalarVariable getResetFlag(boolean next)
	{
		if (next)
		{
			return m_resetFlag.next();			
		}
		return m_resetFlag;
	}

	/**
	 * Gets the processor queue corresponding to the processor's internal
	 * buffer at a given position.
	 * @param index The index of the input pipe
	 * @return The queue
	 */
	public ProcessorQueue getBuffer(int index)
	{
		return m_buffers[index];
	}

	/**
	 * Gets the processor queue corresponding to the processor's back porch.
	 * @return The queue
	 */
	public ProcessorQueue getBackPorch()
	{
		return m_backPorch;
	}

	public int length(QueueType t, int pipe_index)
	{
		if (t == QueueType.PORCH)
		{
			return getFrontPorch(pipe_index).getSize();
		}
		return getBuffer(pipe_index).getSize();
	}

	public Condition at(boolean next, QueueType t, int pipe_index, int m, int n)
	{
		if (t == QueueType.PORCH)
		{
			return atPorch(next, pipe_index, m, n);
		}
		return atBuffer(next, pipe_index, m, n);
	}

	public Term<?> valueAt(boolean next, QueueType t, int pipe_index, int m)
	{
		if (t == QueueType.PORCH)
		{
			return m_frontPorches[pipe_index].valueAt(next, m);
		}
		return m_buffers[pipe_index].valueAt(next, m);
	}

	public Condition booleanValueAt(QueueType t, int pipe_index, int m)
	{
		if (t == QueueType.PORCH)
		{
			return m_frontPorches[pipe_index].booleanValueAt(m);
		}
		return m_buffers[pipe_index].booleanValueAt(m);
	}

	public int getSize(QueueType t, int pipe_index)
	{
		if (t == QueueType.PORCH)
		{
			return getFrontPorch(pipe_index).getSize();
		}
		return getBuffer(pipe_index).getSize();
	}

	/**
	 * Produces the condition stipulating that for a given input pipe,
	 * the n-th event of this pipe is at position m in the processor's
	 * front porch.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the porch
	 * @param n The position in the pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition atPorch(boolean next, int pipe_index, int m, int n)
	{
		return new AtPorch(next, pipe_index, m, n);
	}

	public class AtPorch extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_pipeIndex;

		protected final int m_m;

		protected final int m_n;

		public AtPorch(boolean next, int pipe_index, int m, int n)
		{
			super();
			m_next = next;
			m_pipeIndex = pipe_index;
			m_m = m;
			m_n = n;
			ProcessorQueue porch = m_frontPorches[pipe_index];
			ProcessorQueue buffer = m_buffers[pipe_index];
			if (next)
			{
				porch = porch.next();
				buffer = buffer.next();
			}
			if (n < 0 || n >= buffer.getSize() + porch.getSize() || m < 0 || m >= porch.getSize())
			{
				add(FALSE); // Impossible
			}
			else
			{
				add(porch.hasAt(next, m));
				add(buffer.hasLength(next, n - m));
			}
		}

		@Override
		public String toString()
		{
			return m_pipeIndex + "[" + m_n + "]@" + m_frontPorches[m_pipeIndex].getName() + "[" + m_m + "]";
		}
	}
	
	public class NoReset extends Negation
	{
		protected final boolean m_next;
		
		public NoReset(boolean next)
		{
			super(new IsReset(next));
			m_next = next;
		}
		
		@Override
		public String toString()
		{
			return "NoReset" + (m_next ? "'" : "");
		}
	}
	
	public class IsReset extends BooleanVariableCondition
	{
		protected final boolean m_next;
		
		public IsReset(boolean next)
		{
			super(getResetFlag(next));
			m_next = next;
		}
		
		@Override
		public String toString()
		{
			return "IsReset" + (m_next ? "'" : "");
		}
	}

	public class AtBuffer extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_pipeIndex;

		protected final int m_m;

		protected final int m_n;

		public AtBuffer(boolean next, int pipe_index, int m, int n)
		{
			super();
			m_next = next;
			m_pipeIndex = pipe_index;
			m_m = m;
			m_n = n;
			ProcessorQueue buffer = m_buffers[pipe_index];
			if (m != n)
			{
				add(FALSE); // Impossible
			}
			else
			{
				add(buffer.hasAt(next, m));
			}
		}

		@Override
		public String toString()
		{
			return m_pipeIndex + "[" + m_n + "]@" + m_buffers[m_pipeIndex].getName() + "[" + m_m + "]";
		}
	}

	/**
	 * Produces the condition stipulating that for a given input pipe,
	 * the n-th event of this pipe is at position m in the processor's
	 * internal buffer.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the buffer
	 * @param n The position in the pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition atBuffer(boolean next, int pipe_index, int m, int n)
	{
		return new AtBuffer(next, pipe_index, m, n);
	}

	/**
	 * Produces the condition stipulating that a given input pipe (buffer +
	 * porch combined) contains at least n events. 
	 * @param next A flag indicating if the condition is expressed in the
	 * current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param n The number of events
	 * @return The condition
	 */
	/*@ non_null @*/ public Disjunction minTotalPipe(boolean next, int pipe_index, int n)
	{
		return new MinTotalPipe(next, pipe_index, n);
	}

	public class MinTotalPipe extends Disjunction
	{
		protected final int m_pipeIndex;

		protected final int m_length;

		protected boolean m_next;

		public MinTotalPipe(boolean next, int pipe_index, int n)
		{
			super();
			m_next = next;
			m_length = n;
			m_pipeIndex = pipe_index;
			ProcessorQueue porch = m_frontPorches[pipe_index];
			ProcessorQueue buffer = m_buffers[pipe_index];
			if (next)
			{
				porch = porch.next();
				buffer = buffer.next();
			}
			for (int j = 0; j <= n; j++)
			{
				int k = n - j;
				Conjunction and = new Conjunction();
				and.add(buffer.minLength(j));
				and.add(porch.minLength(k));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "|" + m_pipeIndex + "| >= " + m_length;
		}
	}

	/**
	 * Produces the condition stipulating that a given input pipe contains
	 * exactly n events. 
	 * @param pipe_index The index of the input pipe
	 * @param n The number of events
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasTotalPipe(boolean next, int pipe_index, int n)
	{
		return new HasTotalPipe(next, pipe_index, n);
	}

	public class HasTotalPipe extends Disjunction
	{
		protected final int m_pipeIndex;

		protected final int m_length;

		protected boolean m_next;

		public HasTotalPipe(boolean next, int pipe_index, int n)
		{
			super();
			m_next = next;
			m_length = n;
			m_pipeIndex = pipe_index;
			ProcessorQueue porch = m_frontPorches[pipe_index];
			ProcessorQueue buffer = m_buffers[pipe_index];
			for (int j = 0; j <= n; j++)
			{
				int k = n - j;
				Conjunction and = new Conjunction();
				and.add(buffer.hasLength(next, j));
				and.add(porch.hasLength(next, k));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "|" + m_pipeIndex + "| = " + m_length;
		}
	}

	/**
	 * Copies the state of the current processor module into another one.
	 * @param pm The other processor module
	 */
	protected void copyInto(ProcessorModule pm)
	{
		// Nothing to do. Only there so that descendants can call
		// super.copyInto().
		return;
	}

	/**
	 * Creates a copy of the current processor module.
	 * @return A new, identical instance of the current processor module
	 */
	/*@ non_null @*/ public abstract ProcessorModule duplicate();
}
