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
import ca.uqac.lif.nusmv4j.Comment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.LogicModule;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;
import ca.uqac.lif.nusmv4j.Term;
import ca.uqac.lif.nusmv4j.Variable;

/**
 * Abstract representation of a BeepBeep processor as a collection of queues
 * called front porch, internal buffer and back porch.
 */
public abstract class ProcessorModule extends LogicModule
{
	/**
	 * Dashes to be added to the comments of the NuSMV files.
	 */
	protected static String s_dashes = "------------------------------------------------------------";
	
	/**
	 * A flag determining if the formulas of the transition relation are
	 * simplified before being printed.
	 */
	public static boolean s_simplify = true;
	
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
	 * The back porches of this processor.
	 */
	protected final ProcessorQueue[] m_backPorches;
	
	/**
	 * A flag indicating if the processor has a reset flag as one of its
	 * parameters.
	 */
	protected boolean m_hasReset;


	public ProcessorModule(String name, int in_arity, Domain[] in_domains, int out_arity, Domain[] out_domains, boolean has_reset, int Q_in, int Q_b, int Q_out)
	{
		super(name);
		m_hasReset = has_reset;
		m_frontPorches = new ProcessorQueue[in_arity];
		m_resetFlag = instantiateResetFlag();
		for (int i = 0; i < in_arity; i++)
		{
			m_frontPorches[i] = new ProcessorQueue("in_" + i, new ArrayVariable("inc_" + i, in_domains[i], Q_in), new ArrayVariable("inb_" + i, BooleanDomain.instance, Q_in));
		}
		m_buffers = new ProcessorQueue[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_buffers[i] = new ProcessorQueue("bf_" + i, new ArrayVariable("bfc_" + i, m_frontPorches[i].m_arrayContents.getDomain(), Q_b), new ArrayVariable("bfb_" + i, BooleanDomain.instance, Q_b));
		}
		m_backPorches = new ProcessorQueue[out_arity];
		for (int i = 0; i < out_arity; i++)
		{
			m_backPorches[i] = new ProcessorQueue("ou" + i, new ArrayVariable("ouc_" + i, out_domains[i], Q_out), new ArrayVariable("oub_" + i, BooleanDomain.instance, Q_out));
		}
		int num_params = 2 * (in_arity) + 2 * (out_arity) + (has_reset ? 1 : 0);
		Variable[] params = new Variable[num_params];
		int index = 0;
		for (int i = 0; i < in_arity; i++)
		{
			params[index++] = m_frontPorches[i].m_arrayContents;
			params[index++] = m_frontPorches[i].m_arrayFlags;
		}
		for (int i = 0; i < out_arity; i++)
		{
			params[index++] = m_backPorches[i].m_arrayContents;
			params[index++] = m_backPorches[i].m_arrayFlags;
		}
		if (has_reset)
		{
			params[index++] = m_resetFlag;
		}
		setParameters(params);
		if (Q_b > 0)
		{
			for (int i = 0; i < in_arity; i++)
			{
				add(m_buffers[i].m_arrayContents);
				add(m_buffers[i].m_arrayFlags);
			}
		}
	}
	
	/**
	 * Instantiates the reset flag for this processor.
	 * @return The variable for the reset flag
	 */
	/*@ non_null @*/ protected ScalarVariable instantiateResetFlag()
	{
		return new ScalarVariable("rst", BooleanDomain.instance);
	}
	
	@Override
	protected Comment getComment()
	{
		Comment c = new Comment(s_dashes);
		addToComment(c);
		String line = "";
		if (m_frontPorches.length > 0)
		{
			line += "Q_in = " + m_frontPorches[0].getSize() + " ";
		}
		if (m_buffers.length > 0)
		{
			line += "Q_b = " + m_buffers[0].getSize() + " ";
		}
		if (m_backPorches.length > 0)
		{
			line += "Q_out = " + m_backPorches[0].getSize() + " ";	
		}
		c.addLine(line);
		c.addLine(s_dashes);
		return c;
	}
	
	/**
	 * Adds new lines to the top comment for this module.
	 * @param c The comment
	 */
	protected void addToComment(Comment c)
	{
		// Do nothing
	}
	
	@Override
	public Condition getInit()
	{
		Conjunction and_init = new Conjunction();
		for (int i = 0; i < getInputArity(); i++)
		{
			and_init.add(m_buffers[i].hasLength(false, 0));
			m_buffers[i].addToInit(and_init);
			m_frontPorches[i].addToInit(and_init);
		}
		for (int i = 0; i < getOutputArity(); i++)
		{
			m_backPorches[i].addToInit(and_init);
		}
		addToInit(and_init);
		if (s_simplify)
		{
			return Condition.simplify(and_init);
		}
		return and_init;
	}
	
	@Override
	public Condition getTrans()
	{
		Conjunction and_trans = new Conjunction();
		for (int i = 0; i < getInputArity(); i++)
		{
			m_buffers[i].addToTrans(and_trans);
			m_frontPorches[i].addToTrans(and_trans);
		}
		for (int i = 0; i < getOutputArity(); i++)
		{
			m_backPorches[i].addToTrans(and_trans);	
		}
		addToTrans(and_trans);
		if (s_simplify)
		{
			return Condition.simplify(and_trans);
		}
		return and_trans;
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
	 * Gets the output arity of this processor module.
	 * @return The output arity
	 */
	public int getOutputArity()
	{
		return m_backPorches.length;
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
	 * Gets the processor queue corresponding to the processor's back porch at
	 * a given position.
	 * @param index The index of the output pipe
	 * @return The queue
	 */
	public ProcessorQueue getBackPorch(int index)
	{
		return m_backPorches[index];
	}
	
	/**
	 * Gets the min/max number of output events this processor can produce, in
	 * a single computation step, given ranges on the number of input events
	 * it can receive.
	 * @param ranges Ranges describing the min/max number of input events the
	 * processor can receive on each pipe. The number of such ranges must be
	 * equal to the processor's input arity.
	 * @return The range of output events the processor can produce
	 */
	/*@ pure non_null @*/ public abstract IntegerRange getOutputRange(IntegerRange ... ranges);

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

	public Condition booleanValueAt(boolean next, QueueType t, int pipe_index, int m)
	{
		if (t == QueueType.PORCH)
		{
			return m_frontPorches[pipe_index].booleanValueAt(next, m);
		}
		return m_buffers[pipe_index].booleanValueAt(next, m);
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
			if (n < 0 || n >= buffer.getSize() + porch.getSize() || m < 0 || m >= porch.getSize() || n - m < 0 || n - m > buffer.getSize())
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
	
	/**
	 * Condition that stipulates that the event at position m of a queue is the
	 * event at position n of the pipe.
	 */
	public class IsNthInPipe
	{
		protected final int m_pipeIndex;
		
		protected final int m_m;
		
		protected final int m_n;
		
		protected final boolean m_next;
		
		protected final QueueType m_type;
		
		public IsNthInPipe(boolean next, QueueType type, int pipe_index, int m, int n)
		{
			super();
			m_next = next;
			m_m = m;
			m_n = n;
			m_pipeIndex = pipe_index;
			m_type = type;
		}
		
		@Override
		public String toString()
		{
			return "IsNthInPipe(#" + m_pipeIndex + "," + m_type + "," + m_m + "," + m_n + ")" + (m_next ? "'" : "");
		}
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
			for (int j = 0; j <= n; j++)
			{
				int k = n - j;
				Conjunction and = new Conjunction();
				and.add(buffer.minLength(next, j));
				and.add(porch.minLength(next, k));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "|" + m_pipeIndex + (m_next ? "'" : "") + "| >= " + m_length;
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
				if (j > buffer.getSize() || k > porch.getSize())
				{
					continue; // Impossible condition
				}
				Conjunction and = new Conjunction();
				and.add(buffer.hasLength(next, j));
				and.add(porch.hasLength(next, k));
				add(and);
			}
		}

		@Override
		public String toString()
		{
			return "|" + m_pipeIndex + (m_next ? "'" : "") + "| = " + m_length;
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
	
	/**
	 * Adds any extra terms to the conjunction defining the initial states of
	 * the module. A processor module must override this method even if it has
	 * nothing to add to the conjunction.
	 * @param c The conjunction
	 */
	protected abstract void addToInit(/*@ non_null @*/ Conjunction c);
	
	/**
	 * Adds any extra terms to the conjunction defining the transition relation
	 * of the module. A processor module must override this method even if it
	 * has nothing to add to the conjunction.
	 * @param c The conjunction
	 */
	protected abstract void addToTrans(/*@ non_null @*/ Conjunction c);
	
}
