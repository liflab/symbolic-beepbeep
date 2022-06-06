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

import ca.uqac.lif.nusmv4j.ArrayAccess;
import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.BooleanArrayAccessCondition;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.ConstantFalse;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.LogicModule;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Abstract representation of a BeepBeep processor as a collection of queues
 * called front porch, internal buffer and back porch.
 */
public abstract class ProcessorModule extends LogicModule
{
	protected enum QueueType {PORCH, BUFFER, RESET}
	
	/**
	 * The front porches of this processor.
	 */
	protected final ProcessorQueue[] m_frontPorches;
	
	/**
	 * The reset porches of this processor.
	 */
	protected final NusmvQueue[] m_resetPorches;
	
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
		m_resetPorches = new NusmvQueue[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_frontPorches[i] = new ProcessorQueue(new ArrayVariable("inc_" + i, in_domains[i], Q_in), new ArrayVariable("inb_" + i, BooleanDomain.instance, Q_in));
			m_resetPorches[i] = new NusmvQueue(new ArrayVariable("inr_" + i, BooleanDomain.instance, Q_in));
		}
		m_buffers = new ProcessorQueue[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_buffers[i] = new ProcessorQueue(new ArrayVariable("bfc_" + i, m_frontPorches[i].m_arrayContents.getDomain(), Q_b), new ArrayVariable("bfb_" + i, BooleanDomain.instance, Q_b));
		}
		m_backPorch = new ProcessorQueue(new ArrayVariable("ouc", out_domain, Q_out), new ArrayVariable("oub", BooleanDomain.instance, Q_out));
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
	 * Gets the processor queue corresponding to the processor's reset porch
	 * at a given position.
	 * @param index The index of the input pipe
	 * @return The queue
	 */
	public NusmvQueue getResetPorch(int index)
	{
		return m_resetPorches[index];
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
	
	public Condition at(QueueType t, int pipe_index, int m, int n)
	{
		if (t == QueueType.PORCH)
		{
			return atPorch(pipe_index, m, n);
		}
		return atBuffer(pipe_index, m, n);
	}
	
	public Term<?> at(QueueType t, int pipe_index, int m)
	{
		if (t == QueueType.PORCH)
		{
			return m_frontPorches[pipe_index].valueAt(m);
		}
		return m_buffers[pipe_index].valueAt(m);
	}
	
	public Condition booleanAt(QueueType t, int pipe_index, int m)
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
	 * Produces the condition stipulating that the reset queue for a given
	 * input pipe contains no true value up to and including position m.
	 * @param pipe_index The index of the input pipe
	 */
	public Condition noResetBefore(int pipe_index, int m)
	{
		NusmvQueue queue = getResetPorch(pipe_index);
		ArrayVariable v = queue.m_arrayFlags;
		if (m == 0)
		{
			return new Negation(BooleanArrayAccessCondition.get(ArrayAccess.get(v, 0)));
		}
		Conjunction and = new Conjunction();
		for (int i = 0; i <= m; i++)
		{
			and.add(new Negation(BooleanArrayAccessCondition.get(ArrayAccess.get(v, i))));
		}
		return and;
	}
	
	/**
	 * Produces the condition stipulating that the reset queue for a given
	 * input pipe is set to true at a given index.
	 * @param next A flag indicating if the condition applies to the reset
	 * vector in the current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param m The index in the reset queue
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isResetAt(boolean next, int pipe_index, int m)
	{
		ArrayVariable q = m_resetPorches[pipe_index].m_arrayFlags;
		if (next)
		{
			q = q.next();
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(q, m));
	}
	
	/**
	 * Produces the condition stipulating that for two array indices m and n
	 * such that m &leq; n, the reset buffer is true at position m, but for
	 * no other position in the interval [m + 1, n].
	 * @param next A flag indicating if the condition applies to the reset
	 * vector in the current state or the next state
	 * @param pipe_index The index of the input pipe
	 * @param m The first position
	 * @param n The second position
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isLastResetAt(boolean next, int pipe_index, int m, int n)
	{
		if (m > n)
		{
			return ConstantFalse.FALSE;
		}
		if (m == n)
		{
			return isResetAt(next, pipe_index, m);
		}
		Conjunction and = new Conjunction();
		and.add(isResetAt(next, pipe_index, m));
		for (int i = m + 1; i <= n; i++)
		{
			and.add(new Negation(isResetAt(next, pipe_index, i)));
		}
		return and;
	}
	
	/**
	 * Produces the condition stipulating that for a given input pipe,
	 * the n-th event of this pipe is at position m in the processor's
	 * front porch.
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the porch
	 * @param n The position in the pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition atPorch(int pipe_index, int m, int n)
	{
		ProcessorQueue porch = m_frontPorches[pipe_index];
		ProcessorQueue buffer = m_buffers[pipe_index];
		if (n < 0 || n >= buffer.getSize() + porch.getSize() || m < 0 || m >= porch.getSize())
		{
			return FALSE; // Impossible
		}
		Conjunction and = new Conjunction();
		and.add(porch.hasAt(m));
		and.add(buffer.hasLength(n - m));
		return and;
	}
	
	/**
	 * Produces the condition stipulating that for a given input pipe,
	 * the n-th event of this pipe is at position m in the processor's
	 * internal buffer.
	 * @param pipe_index The index of the input pipe
	 * @param m The position in the buffer
	 * @param n The position in the pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition atBuffer(int pipe_index, int m, int n)
	{
		ProcessorQueue buffer = m_buffers[pipe_index];
		if (m != n)
		{
			return FALSE; // Impossible
		}
		return buffer.hasAt(m);
	}
	
	/**
	 * Produces the condition stipulating that a given input pipe contains at
	 * least n events. 
	 * @param pipe_index The index of the input pipe
	 * @param n The number of events
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition minTotalPipe(int pipe_index, int n)
	{
		Disjunction or = new Disjunction();
		ProcessorQueue porch = m_frontPorches[pipe_index];
		ProcessorQueue buffer = m_buffers[pipe_index];
		for (int j = 0; j <= n; j++)
		{
			int k = n - j;
			Conjunction and = new Conjunction();
			and.add(buffer.minLength(j));
			and.add(porch.minLength(k));
			or.add(and);
		}
		return or;
	}
	
	/**
	 * Produces the condition stipulating that a given input pipe contains
	 * exactly n events. 
	 * @param pipe_index The index of the input pipe
	 * @param n The number of events
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasTotalPipe(int pipe_index, int n)
	{
		Disjunction or = new Disjunction();
		ProcessorQueue porch = m_frontPorches[pipe_index];
		ProcessorQueue buffer = m_buffers[pipe_index];
		for (int j = 0; j <= n; j++)
		{
			int k = n - j;
			Conjunction and = new Conjunction();
			and.add(buffer.hasLength(j));
			and.add(porch.hasLength(k));
			or.add(and);
		}
		return or;
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
