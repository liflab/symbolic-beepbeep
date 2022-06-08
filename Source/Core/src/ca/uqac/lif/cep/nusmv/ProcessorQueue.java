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

import ca.uqac.lif.nusmv4j.ArrayAccess;
import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BooleanArrayAccessCondition;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.ConstantFalse;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.Term;

/**
 * An event queue modeled as a pair of NuSMV array variables.
 */
public class ProcessorQueue extends NusmvQueue
{
	/*@ non_null @*/ protected final ArrayVariable m_arrayContents;

	protected ProcessorQueue m_next;

	public ProcessorQueue(ArrayVariable contents, ArrayVariable flags)
	{
		super(flags);
		m_arrayContents = contents;
		m_next = new ProcessorQueue(contents.next(), flags.next(), true);
	}

	protected ProcessorQueue(ArrayVariable contents, ArrayVariable flags, boolean is_next)
	{
		super(flags);
		m_arrayContents = contents;
		m_next = null;
	}

	public ProcessorQueue(String contents, String flags, int size, Domain d)
	{
		super(new ArrayVariable(flags, BooleanDomain.instance, size));
		m_arrayContents = new ArrayVariable(contents, d, size);
		m_next = new ProcessorQueue(m_arrayContents.next(), m_arrayFlags.next(), true);
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
		Object v = m_arrayContents.getDomain().getDefaultValue();
		Constant cv = new Constant(v);
		Conjunction and = new Conjunction();
		for (int i = 0; i < getSize(); i++)
		{
			if (i > 0)
			{
				Implication imp = new Implication();
				imp.add(hasAt(i));
				imp.add(hasAt(i - 1));
				and.add(imp);
			}
			Implication imp = new Implication();
			Negation neg = new Negation();
			neg.add(hasAt(i));
			imp.add(neg);
			Equality eq = new Equality(valueAt(i), cv);
			imp.add(eq);
			and.add(imp);
		}
		return and;
	}

	/**
	 * Returns the condition stipulating that the queue has an element at
	 * a given position. 
	 * @param index The index
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasAt(int index)
	{
		if (index < 0 || index >= getSize())
		{
			return ConstantFalse.FALSE;
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(m_arrayFlags, index));
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
			return ConstantFalse.FALSE;
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
	/*@ non_null @*/ public Condition booleanValueAt(int index)
	{
		if (index < 0 || index >= getSize())
		{
			return ConstantFalse.FALSE;
		}
		return BooleanArrayAccessCondition.get(ArrayAccess.get(m_arrayContents, index));
	}
	
	/**
	 * Returns the term designating the value of an element of the queue at a
	 * given position. 
	 * @param index The index
	 * @return The term
	 */
	/*@ non_null @*/ public Term<?> valueAt(int index)
	{
		if (index < 0 || index >= getSize())
		{
			return ConstantFalse.FALSE;
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
			return ConstantFalse.FALSE;
		}
		return ArrayAccess.get(m_arrayContents.next(), index);
	}

	/**
	 * Returns the condition stipulating that the queue contains at least
	 * <i>n</i> elements.
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition minLength(int n)
	{
		Conjunction and = new Conjunction();
		int Q = getSize();
		if (n < 0 || n > Q)
		{
			return FALSE;
		}
		if (n == 0)
		{
			return TRUE;
		}
		for (int i = 0; i <= n - 1; i++)
		{
			and.add(hasAt(i));
		}
		return and;
	}

	/**
	 * Returns the condition stipulating that the queue contains exactly
	 * <i>n</i> elements.
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition hasLength(int n)
	{
		return new HasLength(n);
	}
	
	public class HasLength extends Conjunction
	{
		protected final int m_length;
		
		public HasLength(int n)
		{
			super();
			m_length = n;
			int Q = getSize();
			if (n < 0 || n > Q)
			{
				add(ConstantFalse.FALSE);
			}
			for (int i = 0; i <= n - 1; i++)
			{
				add(hasAt(i));
			}
			for (int i = n; i <= Q - 1; i++)
			{
				Negation not = new Negation();
				not.add(hasAt(i));
				add(not);
			}
		}
		
		@Override
		public String toString()
		{
			return "HasLength(" + m_arrayContents.getName() + ", " + m_length + ")";
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
		Conjunction and = new Conjunction();
		int Q = getSize();
		if (n < 0 || n >= Q)
		{
			return FALSE;
		}
		for (int i = 0; i <= n - 1; i++)
		{
			and.add(nextHasAt(i));
		}
		for (int i = n; i <= Q - 1; i++)
		{
			Negation not = new Negation();
			not.add(nextHasAt(i));
			and.add(not);
		}
		return and;
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
}
