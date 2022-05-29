package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Conjunction;
import ca.uqac.lif.symbolif.Disjunction;
import ca.uqac.lif.symbolif.Domain;
import ca.uqac.lif.symbolif.LogicModule;

import static ca.uqac.lif.symbolif.ConstantFalse.FALSE;

import ca.uqac.lif.symbolif.ArrayVariable;
import ca.uqac.lif.symbolif.BooleanDomain;

/**
 * Abstract representation of a BeepBeep processor as a collection of queues
 * called front porch, internal buffer and back porch.
 */
public abstract class BeepBeepModule extends LogicModule
{
	/**
	 * The front porches of this processor.
	 */
	protected ProcessorQueue[] m_frontPorches;
	
	/**
	 * The internal buffers of this processor.
	 */
	protected ProcessorQueue[] m_buffers;
	
	/**
	 * The back porch of this processor.
	 */
	protected ProcessorQueue m_backPorch;
	
	
	public BeepBeepModule(String name, int in_arity, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name);
		m_frontPorches = new ProcessorQueue[in_arity];
		for (int i = 0; i < in_arity; i++)
		{
			m_frontPorches[i] = new ProcessorQueue(new ArrayVariable("inc_" + i, in_domain, Q_in), new ArrayVariable("inb_" + i, BooleanDomain.instance, Q_in));
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
	public BeepBeepModule setFrontPorch(ProcessorQueue q, int position)
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
	public BeepBeepModule setBuffer(ProcessorQueue q, int position)
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
}
