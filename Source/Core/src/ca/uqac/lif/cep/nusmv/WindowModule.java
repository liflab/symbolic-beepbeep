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

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;

import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.BUFFER;
import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.PORCH;

public class WindowModule extends ProcessorModule
{
	/**
	 * The width of the window.
	 */
	protected final int m_width;

	/**
	 * The processor to run on each window.
	 */
	protected final ProcessorModule m_processor;

	/**
	 * A map associating front porches of each inner processor instance to
	 * their offset index.
	 */
	protected final Map<Integer,ProcessorQueue> m_innerFrontPorches;

	/**
	 * A map associating back porches of each inner processor instance to
	 * their offset index.
	 */
	protected final Map<Integer,ProcessorQueue> m_innerBackPorches;

	/**
	 * A map associating reset flags of each inner processor instance to
	 * their offset index.
	 */
	protected final Map<Integer,ScalarVariable> m_resetFlags;

	/**
	 * An array of processor instances, one for each offset of the processor
	 * to run on each window.
	 */
	protected final ProcessorModule[] m_processors;

	public WindowModule(String name, ProcessorModule processor, int width, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, 1, new Domain[] {in_domain}, out_domain, Q_in, Q_b, Q_out);
		m_width = width;
		m_processor = processor;
		m_processors = new ProcessorModule[Q_in + width];
		for (int i = 0; i < m_processors.length; i++)
		{
			m_processors[i] = m_processor.duplicate();
		}
		m_innerFrontPorches = new HashMap<Integer,ProcessorQueue>();
		for (int i = 0; i < Q_in + width; i++)
		{
			m_innerFrontPorches.put(i, new ProcessorQueue("inner_in", "innerfc_" + i, "innerfb_" + i, width, in_domain));
		}
		m_innerBackPorches = new HashMap<Integer,ProcessorQueue>();
		for (int i = 0; i < Q_in + width; i++)
		{
			m_innerBackPorches.put(i, new ProcessorQueue("inner_ou", "innerbc_" + i, "innerbb_" + i, width, out_domain));
		}
		m_resetFlags = new HashMap<Integer,ScalarVariable>();
		for (int i = 0; i < Q_in + width; i++)
		{
			m_resetFlags.put(i, new ScalarVariable("innerr_" + i, BooleanDomain.instance));
		}
	}

	/**
	 * Produces the condition stipulating that the n-th inner processor
	 * instance is active in the current (or the next) computation step. 
	 */
	public class IsActive extends Disjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		/**
		 * 
		 * @param next A flag indicating if the condition is expressed in the
		 * current state or the next state
		 * @param n The index of the processor instance
		 */
		public IsActive(boolean next, int n)
		{
			super();
			m_next = next;
			m_n = n;
			add(minTotalPipe(next, 0, m_width + n));
		}

		@Override
		public String toString()
		{
			return "IsActive(" + m_n + ")";
		}
	}

	/**
	 * Produces the condition stipulating that for all inner processor
	 * instances:
	 * <ol>
	 * <li>if the inner processor instance at position i is active, its front
	 * porch has exactly <i>width</i> elements</li>
	 * <li>otherwise, it has exactly 0 element</li>
	 * </ol>
	 * @param next A flag indicating if the condition is expressed in the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition innerFrontPorchSizes(boolean next)
	{
		Conjunction and = new Conjunction();
		for (int i = 0; i < m_innerFrontPorches.size(); i++)
		{
			ProcessorQueue fp = m_innerFrontPorches.get(i);
			Disjunction or = new Disjunction();
			{
				Conjunction in_and = new Conjunction();
				in_and.add(new IsActive(next, i));
				in_and.add(fp.hasLength(next, m_width));
				or.add(in_and);
			}
			{
				Conjunction in_and = new Conjunction();
				in_and.add(new Negation(new IsActive(next, i)));
				in_and.add(fp.hasLength(next, 0));
				or.add(in_and);
			}
			and.add(or);
		}
		return and;
	}

	/**
	 * Produces the condition stipulating that a given element at position m
	 * in either the front porch or the internal buffer is the n-th event of
	 * the front porch of a given internal processor instance.
	 * @param next A flag indicating if the condition is expressed in the
	 * current state or the next state
	 * @param offset The index of the internal processor instance
	 * @param sigma A flag specifying if one talks about the window's internal
	 * buffer or front porch
	 * @param m The position of the event in the window's input queue
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition innerFrontPorchCellContents(boolean next, int offset, QueueType sigma, int m, int n)
	{
		return at(next, sigma, 0, m, n + offset);
	}

	/**
	 * Produces the condition stipulating that a given element at position m
	 * in either the front porch or the internal buffer is the n-th event of
	 * the front porch of a given internal processor instance, <em>and</em>
	 * that the two matching cells contain the same event. 
	 * @param next
	 * @param offset
	 * @param sigma
	 * @param m
	 * @param n
	 * @return
	 */
	/*@ non_null @*/ public Condition setFrontPorchContents(boolean next, int offset, QueueType sigma, int m, int n)
	{
		return new Equality(
				valueAt(next, sigma, 0, m), // m-th event of sigma
				m_innerFrontPorches.get(offset).valueAt(next, n) // n-th event of front porch of offset
				);
	}

	/**
	 * Produces the condition stipulating that a given element at position m
	 * in either the front porch or the internal buffer is the n-th event of
	 * the front porch of a given internal processor instance, <em>and</em>
	 * that the two matching cells contain the same event.
	 */
	public class InnerFrontPorchCellContents extends Equality
	{
		protected final boolean m_next;

		protected final int m_offset;

		protected final QueueType m_sigma;

		protected final int m_m;

		protected final int m_n;

		public InnerFrontPorchCellContents(boolean next, int offset, QueueType sigma, int m, int n)
		{
			super(valueAt(next, sigma, 0, m), // m-th event of sigma
					m_innerFrontPorches.get(offset).valueAt(next, n) // n-th event of front porch of offset
					);
			m_next = next;
			m_offset = offset;
			m_sigma = sigma;
			m_m = m;
			m_n = n;
		}

		@Override
		public String toString()
		{
			return m_sigma + "[" + m_m + "] = inner_" + m_offset + "[" + m_n + "]";
		}
	}

	/**
	 * Produces the condition associating values of the input buffer/porch to
	 * specific positions of the front porches of inner processor instances.
	 * @param next A flag indicating if the condition is expressed in the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition innerFrontPorchContents(boolean next)
	{
		Conjunction and = new Conjunction();
		for (int offset = 0; offset < m_innerFrontPorches.size(); offset++)
		{
			Implication imp = new Implication();
			imp.add(new IsActive(next, offset));
			imp.add(new InnerFrontPorchContents(next, offset));
			and.add(imp);
		}
		return and;
	}

	/**
	 * Produces the condition associating values of the input buffer/porch to
	 * specific positions of the front porches of a specific inner processor
	 * instance.
	 */
	public class InnerFrontPorchContents extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_offset;

		/**
		 * 
		 * @param next A flag indicating if the condition is expressed in the
		 * current state or the next state
		 * @param offset The index of the inner processor instance
		 */
		public InnerFrontPorchContents(boolean next, int offset)
		{
			super();
			m_next = next;
			m_offset = offset;
			for (int n = 0; n < m_width; n++)
			{
				for (QueueType sigma : new QueueType[] {PORCH, BUFFER})
				{
					for (int m = 0; m < getSize(sigma, 0); m++)
					{
						Implication in_imp = new Implication();
						in_imp.add(innerFrontPorchCellContents(next, offset, sigma, m, n));
						in_imp.add(setFrontPorchContents(next, offset, sigma, m, n));
						add(in_imp);
					}
				}
			}
		}

		@Override
		public Boolean evaluate(Assignment a)
		{
			// Only there to get a hook to insert a breakpoint if needed
			return super.evaluate(a);
		}

		@Override
		public String toString()
		{
			return "InnerFrontPorchContents(" + m_next + "," + m_offset + ")"; 
		}
	}

	public class BackPorchContents extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_offset;

		public BackPorchContents(boolean next, int offset)
		{
			super();
			m_next = next;
			m_offset = offset;
			ProcessorQueue in_porch = m_innerBackPorches.get(offset);
			for (int m = 1; m <= in_porch.getSize(); m++)
			{
				Implication imp = new Implication();
				imp.add(in_porch.hasLength(next, m));
				imp.add(new Equality(
						in_porch.valueAt(next, m - 1),
						getBackPorch().valueAt(next, offset)));
				add(imp);
			}
		}

		@Override
		public Boolean evaluate(Assignment a)
		{
			// Only there to get a hook to insert a breakpoint if needed
			return super.evaluate(a);
		}

		@Override
		public String toString()
		{
			return "BackPorchContents(" + m_next + "," + m_offset + ")"; 
		}
	}

	public class BackPorchLength extends Conjunction
	{
		protected final boolean m_next;

		public BackPorchLength(boolean next)
		{
			super();
			m_next = next;
			ProcessorQueue back_porch = getBackPorch();
			for (int i = 0; i < back_porch.getSize(); i++)
			{
				Equivalence eq = new Equivalence();
				eq.add(back_porch.hasAt(next, i));
				eq.add(m_innerFrontPorches.get(i).hasLength(next, m_width));
				add(eq);
			}
		}

		@Override
		public Boolean evaluate(Assignment a)
		{
			return super.evaluate(a);
		}

		@Override
		public String toString()
		{
			return "BackPorchLength";
		}
	}

	public class NextBufferLength extends Conjunction
	{
		public NextBufferLength()
		{
			super();
			ProcessorQueue buffer = getBuffer(0);
			for (int nf = 0; nf < m_width; nf++)
			{
				Implication imp = new Implication();
				imp.add(hasTotalPipe(false, 0, nf));
				imp.add(buffer.nextHasLength(nf));
				add(imp);
			}
			Implication eq = new Implication();
			eq.add(minTotalPipe(false, 0, m_width));
			eq.add(buffer.nextHasLength(m_width - 1));
			add(eq);
		}
		
		@Override
		public Boolean evaluate(Assignment a)
		{
			return super.evaluate(a);
		}

		@Override
		public String toString()
		{
			return "NextBufferLength";
		}
	}
	
	public class NextBufferContents extends Conjunction
	{
		public NextBufferContents()
		{
			super();
			for (int len_pipe = 1; len_pipe <= getFrontPorch(0).getSize() + getBuffer(0).getSize(); len_pipe++)
			//int len_pipe = 4;
			{
				Implication imp = new Implication();
				imp.add(hasTotalPipe(false, 0, len_pipe));
				imp.add(new NextBufferContentsLength(len_pipe));
				add(imp);
			}
		}
		
		@Override
		public String toString()
		{
			return "NextBufferContents";
		}
		
		@Override
		public Boolean evaluate(Assignment a)
		{
			return super.evaluate(a);
		}
	}
	
	public class NextBufferContentsLength extends Conjunction
	{
		protected final int m_lenPipe;
		
		public NextBufferContentsLength(int len_pipe)
		{
			super();
			m_lenPipe = len_pipe;
			for (QueueType sigma : new QueueType[] {BUFFER, PORCH})
			{
				for (int m = 0; m < getSize(sigma, 0); m++)
				{
					if (len_pipe < m_width)
					{
						for (int n = 0; n < m_width - 1; n++)
						{
							Implication in_imp = new Implication();
							in_imp.add(at(false, sigma, 0, m, n));
							in_imp.add(new Equality(
									valueAt(false, sigma, 0, m),
									valueAt(true, BUFFER, 0, n)
									));
							add(in_imp);
						}
					}
					else
					{
						for (int n = 0; n < m_width - 1; n++)
						{
							Implication in_imp = new Implication();
							in_imp.add(at(false, sigma, 0, m, n + len_pipe - m_width + 1));
							in_imp.add(new Equality(
									valueAt(false, sigma, 0, m),
									valueAt(true, BUFFER, 0, n)
									));
							add(in_imp);
						}
					}						
				}
			}
		}
		
		@Override
		public Boolean evaluate(Assignment a)
		{
			return super.evaluate(a);
		}
	}
	
	public class NextBufferCellContent extends Conjunction
	{
		public NextBufferCellContent(int m, int n)
		{
			super();
		}
	}
	

	@Override
	public WindowModule duplicate()
	{
		WindowModule m = new WindowModule(getName(), m_processor, m_width, getFrontPorch(0).getDomain(), getBackPorch().getDomain(), getFrontPorch(0).getSize(), getBuffer(0).getSize(), getBackPorch().getSize());
		super.copyInto(m);
		return m;
	}
}
