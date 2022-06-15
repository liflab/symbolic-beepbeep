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
import static ca.uqac.lif.nusmv4j.ConstantTrue.TRUE;

import java.util.List;

import ca.uqac.lif.nusmv4j.ArrayAccess;
import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BooleanArrayAccessCondition;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.Term;
import ca.uqac.lif.nusmv4j.Variable;

/**
 * An event queue modeled as a pair of NuSMV array variables. Concretely,
 * this class is implemented as a descendant of {@link NusmvQueue} with an
 * extra array variable field.
 */
public class ProcessorQueue extends NusmvQueue
{
	/**
	 * The name given to this queue.
	 */
	protected String m_name;
	
	/**
	 * The array variable that stores the actual events of the queue.
	 */
	/*@ non_null @*/ protected final ArrayVariable m_arrayContents;

	/**
	 * A reference to an instance of the same variable in the next state. This
	 * reference is null if the current object is already a "next" variable.
	 */
	/*@ null @*/ protected ProcessorQueue m_next;

	public ProcessorQueue(String name, ArrayVariable contents, ArrayVariable flags)
	{
		super(flags);
		m_arrayContents = contents;
		m_name = name;
		m_next = new ProcessorQueue(name, contents.next(), flags.next(), true);
	}

	protected ProcessorQueue(String name, ArrayVariable contents, ArrayVariable flags, boolean is_next)
	{
		super(flags);
		m_name = name;
		m_arrayContents = contents;
		m_next = null;
	}

	public ProcessorQueue(String name, String contents, String flags, int size, Domain d)
	{
		super(new ArrayVariable(flags, BooleanDomain.instance, size));
		m_name = name;
		m_arrayContents = new ArrayVariable(contents, d, size);
		m_next = new ProcessorQueue(name, m_arrayContents.next(), m_arrayFlags.next(), true);
	}
	
	/**
	 * Adds conditions to the initial states of the module that contains this
	 * queue.
	 * @param c The conjunction representing all the initial conditions of the
	 * module
	 */
	public void addToInit(Conjunction c)
	{
		c.add(isWellFormed());
	}
	
	/**
	 * Adds conditions to the transition formula of the module that contains this
	 * queue.
	 * @param c The conjunction representing all the conditions of the
	 * transition formula for this module
	 */
	public void addToTrans(Conjunction c)
	{
		c.add(next().isWellFormed());
	}

	/**
	 * Gets an instance representing the processor queue in its next state.
	 * @return The next instance, or <tt>null</tt> if the current object is
	 * already the next instance.
	 */
	/*@ pure non_null @*/ public ProcessorQueue next()
	{
		return m_next;
	}

	/*@ pure non_null @*/ public Domain getDomain()
	{
		return m_arrayContents.getDomain();
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}
	
	/**
	 * Gets the name of this queue.
	 * @return The name
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * Sets the maximum size of this processor queue to a new value.
	 * @param size The new queue size
	 * @return This queue
	 */
	public ProcessorQueue setSize(int size)
	{
		m_arrayFlags.setDimension(size);
		m_arrayContents.setDimension(size);
		return this;
	}
	
	public class IsWellFormed extends Conjunction
	{
		protected final boolean m_next;
		
		public IsWellFormed()
		{
			this(false);
		}
		
		public IsWellFormed(boolean next)
		{
			super();
			m_next = next;
			Object v = m_arrayContents.getDomain().getDefaultValue();
			Constant cv = new Constant(v);
			for (int i = 0; i < getSize(); i++)
			{
				if (i > 0)
				{
					Implication imp = new Implication();
					imp.add(hasAt(m_next, i));
					imp.add(hasAt(m_next, i - 1));
					add(imp);
				}
				Implication imp = new Implication();
				Negation neg = new Negation();
				neg.add(hasAt(m_next, i));
				imp.add(neg);
				Equality eq = new Equality(valueAt(m_next, i), cv);
				imp.add(eq);
				add(imp);
			}
		}
		
		@Override
		public String toString()
		{
			return "WellFormed(" + m_name + ")";
		}
	}

	/**
	 * Returns the condition stipulating that the processor queue is well
	 * formed. This is the case when:
	 * <ol>
	 * <li>If the flag array is true at position i, then it is also true
	 * at position i&minus;1</li>
	 * <li>If the flag array is false at position i, then the content array at
	 * that same position contains the default value for the queue's
	 * domain</li> 
	 * </ol>
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isWellFormed()
	{
		return new IsWellFormed();
	}

	/**
	 * Returns the condition stipulating that the queue has an element at
	 * a given position. 
	 * @param next A flag determining if the condition is expressed in the
	 * current state or the next state
	 * @param index The index
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasAt(boolean next, int index)
	{
		if (index < 0 || index >= getSize())
		{
			return FALSE;
		}
		ArrayVariable q = m_arrayFlags;
		if (next)
		{
			q = q.next();
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(q, index));
	}

	/**
	 * Returns the condition stipulating that the queue has an element at
	 * a given position in the <em>next</em> state.
	 * @param index The index
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextHasAt(int index)
	{
		if (index < 0 || index >= getSize())
		{
			return FALSE;
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(m_arrayFlags.next(), index));
	}

	/**
	 * Returns the term asserting that the value an element of the queue at a
	 * given position is true. This applies only to queues whose domain is
	 * Boolean.
	 * @param index The index
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition booleanValueAt(boolean next, int index)
	{
		if (index < 0 || index >= getSize())
		{
			return FALSE;
		}
		if (next)
		{
			return BooleanArrayAccessCondition.get(ArrayAccess.get(m_arrayContents.next(), index));	
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(m_arrayContents, index));
	}

	/**
	 * Returns the term designating the value of an element of the queue at a
	 * given position. 
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param index The index
	 * @return The term
	 */
	/*@ non_null @*/ public Term<?> valueAt(boolean next, int index)
	{
		if (index < 0 || index >= getSize())
		{
			return FALSE;
		}
		if (next)
		{
			return ArrayAccess.get(m_arrayContents.next(), index);
		}
		return ArrayAccess.get(m_arrayContents, index);
	}

	/**
	 * Returns the term designating the value of an element of the queue at a
	 * given position in the <em>next</em> state.
	 * @param index The index
	 * @return The term
	 */
	/*@ non_null @*/ public Term<?> nextValueAt(int index)
	{
		if (index < 0 || index >= getSize())
		{
			return FALSE;
		}
		return ArrayAccess.get(m_arrayContents.next(), index);
	}

	/**
	 * Returns the condition stipulating that the queue contains at least
	 * <i>n</i> elements.
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition minLength(boolean next, int n)
	{
		int Q = getSize();
		if (n < 0 || n > Q)
		{
			return FALSE;
		}
		else if (n == 0)
		{
			return TRUE;
		}
		return new MinLength(next, n);
	}

	public class MinLength extends Conjunction
	{
		protected final boolean m_next;

		protected final int m_n;

		public MinLength(int n)
		{
			this(false, n);
		}

		public MinLength(boolean next, int n)
		{
			super();
			m_next = next;
			m_n = n;
			for (int i = 0; i <= n - 1; i++)
			{
				add(hasAt(m_next, i));
			}
		}

		@Override
		public String toString()
		{
			return "|" + getName() + (m_next ? "'" : "") + "| >= " + m_n;
		}
	}

	/**
	 * Returns the condition stipulating that the queue contains exactly
	 * <i>n</i> elements.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasLength(boolean next, int n)
	{
		return new HasLength(next, n);
	}

	public class HasLength extends Conjunction
	{
		protected final int m_length;
		
		protected final boolean m_next;

		public HasLength(boolean next, int n)
		{
			super();
			m_length = n;
			m_next = next;
			int Q = getSize();
			if (n < 0 || n > Q)
			{
				add(FALSE);
			}
			for (int i = 0; i <= n - 1; i++)
			{
				add(hasAt(m_next, i));
			}
			for (int i = n; i <= Q - 1; i++)
			{
				Negation not = new Negation();
				not.add(hasAt(m_next, i));
				add(not);
			}
		}

		@Override
		public String toString()
		{
			return "|" + getName() + (m_next ? "'" : "") + "| = " + m_length;
		}
	}

	/**
	 * Returns the condition stipulating that the queue contains exactly
	 * <i>n</i> elements in its <em>next</em> state.
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextHasLength(int n)
	{
		return new HasLength(true, n);
	}

	/**
	 * Sets the contents of the queue by giving a list of elements. The method
	 * takes care of assigning the correct values of the cells in both the
	 * "contents" and the "flags" array based on the input.
	 * @param elements
	 * @return
	 */
	public ProcessorQueue set(Object ... elements)
	{
		Object[] values = new Object[getSize()];
		Object[] flags = new Object[values.length];
		Object default_v = m_arrayContents.getDomain().getDefaultValue();
		for (int i = 0; i < values.length; i++)
		{
			if (i < elements.length)
			{
				values[i] = elements[i];
				flags[i] = true;
			}
			else
			{
				values[i] = default_v;
				flags[i] = false;
			}
		}
		m_arrayContents.setValues(values);
		m_arrayFlags.setValues(flags);
		return this;
	}

	/**
	 * Adds to an assignment the values of the queue's variables.
	 * @param a The assignment
	 */
	public void assign(Assignment a)
	{
		m_arrayContents.assign(a);
		m_arrayFlags.assign(a);
	}
	
	@Override
	/*@ pure non_null @*/ public List<Variable> getVariables()
	{
		List<Variable> vars = super.getVariables();
		vars.add(m_arrayContents);
		return vars;
	}
}
