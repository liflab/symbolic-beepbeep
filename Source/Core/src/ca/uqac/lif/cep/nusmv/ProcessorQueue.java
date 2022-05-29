package ca.uqac.lif.cep.nusmv;

import static ca.uqac.lif.symbolif.ConstantFalse.FALSE;
import static ca.uqac.lif.symbolif.ConstantTrue.TRUE;

import ca.uqac.lif.symbolif.ArrayAccess;
import ca.uqac.lif.symbolif.ArrayVariable;
import ca.uqac.lif.symbolif.Assignment;
import ca.uqac.lif.symbolif.BooleanArrayAccessCondition;
import ca.uqac.lif.symbolif.BooleanDomain;
import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Conjunction;
import ca.uqac.lif.symbolif.ConstantFalse;
import ca.uqac.lif.symbolif.Domain;
import ca.uqac.lif.symbolif.Negation;

/**
 * An event queue modeled as a pair of NuSMV array variables.
 */
public class ProcessorQueue
{
	/*@ non_null @*/ protected final ArrayVariable m_arrayContents;
	
	/*@ non_null @*/ protected final ArrayVariable m_arrayFlags;
	
	public ProcessorQueue(ArrayVariable contents, ArrayVariable flags)
	{
		super();
		m_arrayContents = contents;
		m_arrayFlags = flags;
	}
	
	public ProcessorQueue(String contents, String flags, int size, Domain d)
	{
		super();
		m_arrayContents = new ArrayVariable(contents, d, size);
		m_arrayFlags = new ArrayVariable(flags, BooleanDomain.instance, size);
	}
	
	/**
	 * Gets the size of this queue.
	 * @return
	 */
	public int getSize()
	{
		return m_arrayContents.getDimension();
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
	 * Returns the condition stipulating that the queue contains at least
	 * <i>n</i> elements.
	 * @param n The number of elements
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition minLength(int n)
	{
		Conjunction and = new Conjunction();
		int Q = getSize();
		if (n < 0 || n >= Q)
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
		Conjunction and = new Conjunction();
		int Q = getSize();
		if (n < 0 || n >= Q)
		{
			return FALSE;
		}
		for (int i = 0; i <= n - 1; i++)
		{
			and.add(hasAt(i));
		}
		for (int i = n; i <= Q - 1; i++)
		{
			Negation not = new Negation();
			not.add(hasAt(i));
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
