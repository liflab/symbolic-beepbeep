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

import ca.uqac.lif.nusmv4j.Addition;
import ca.uqac.lif.nusmv4j.Addition.AdditionModulo;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.nusmv4j.ArrayVariable;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.InvalidAssignmentException;
import ca.uqac.lif.nusmv4j.LessThan;
import ca.uqac.lif.nusmv4j.ScalarVariable;
import ca.uqac.lif.nusmv4j.Variable;

/**
 * A processor queue that outputs events at each computation step according
 * to a predefined sequence.
 */
public class PresetProcessorQueue extends ProcessorQueue
{
	/**
	 * A counter indicating the current state in the sequence.
	 */
	protected final ScalarVariable m_stateCounter;

	/**
	 * The number of computation steps for which events are defined.
	 */
	protected final int m_numSteps;

	/**
	 * An array of objects corresponding to the inputs to produce at each
	 * computation step.
	 */
	protected List<Object[]> m_steps;

	/**
	 * A flag determining if the events should loop from the beginning once the
	 * end of the sequence has been reached.
	 */
	protected boolean m_loop;

	public PresetProcessorQueue(String name, ArrayVariable contents, ArrayVariable flags, int num_steps, boolean loop)
	{
		super(name, contents, flags);
		m_numSteps = num_steps;
		m_loop = loop;
		if (m_loop)
		{
			m_stateCounter = new ScalarVariable("cnt", new IntegerRange(0, num_steps - 1));
		}
		else
		{
			m_stateCounter = new ScalarVariable("cnt", new IntegerRange(0, num_steps));
		}
		m_steps = new ArrayList<Object[]>();
	}
	
	public PresetProcessorQueue(String name, String contents, String flags, String counter, int size, Domain d, int num_steps, boolean loop)
	{
		super(name, contents, flags, size, d);
		m_numSteps = num_steps;
		m_stateCounter = new ScalarVariable(counter, new IntegerRange(0, num_steps));
		m_loop = loop;
		m_steps = new ArrayList<Object[]>();
	}
	
	/**
	 * Gets the instance of scalar variable representing the queue's state
	 * counter.
	 * @return The variable
	 */
	/*@ pure non_null @*/ public ScalarVariable getStateCounter()
	{
		return m_stateCounter;
	}

	/**
	 * Adds events for the next computation step.
	 * @param events The events to output at the next computation step
	 * @return This queue
	 */
	/*@ non_null @*/ public PresetProcessorQueue addStep(Object ... events)
	{
		if (events.length > getSize())
		{
			throw new QueueOutOfBoundsException("Too many events (" + events.length + ") for queue size (" + getSize() + ")");
		}
		Domain d = m_arrayContents.getDomain();
		for (Object o : events)
		{
			if (!d.contains(o))
			{
				throw new InvalidAssignmentException("Invalid value " + o);
			}
		}
		m_steps.add(events);
		return this;
	}

	@Override
	public void addToInit(Conjunction c)
	{
		super.addToInit(c);
		c.add(new Equality(m_stateCounter, new Constant(0)));
		c.add(eventsForStep(false, 0));
	}

	/**
	 * Produces the conjunction that defines the contents of the buffer at
	 * a given computation step.
	 * @param next A flag indicating if the condition designates the buffer
	 * in its current state or in the next state
	 * @param step The index of the computation step
	 * @return The conjunction
	 */
	protected Conjunction eventsForStep(boolean next, int step)
	{
		Conjunction c = new Conjunction();
		Object[] events = m_steps.get(step);
		c.add(hasLength(next, events.length));
		for (int i = 0; i < events.length; i++)
		{
			c.add(new Equality(valueAt(next, i), new Constant(events[i])));
		}
		return c;
	}

	@Override
	public void addToTrans(Conjunction c)
	{
		super.addToTrans(c);
		// First, set value of state counter
		if (m_loop)
		{
			c.add(new Equality(m_stateCounter.next(), new AdditionModulo(m_numSteps, m_stateCounter, new Constant(1))));
		}
		else
		{
			{
				Implication imp = new Implication();
				imp.add(new LessThan(m_stateCounter, new Constant(m_numSteps)));
				if (m_loop)
				{
					imp.add(new Equality(m_stateCounter.next(), new AdditionModulo(m_numSteps, m_stateCounter, new Constant(1))));
				}
				else
				{
					imp.add(new Equality(m_stateCounter.next(), new Addition(m_stateCounter, new Constant(1))));
				}
				c.add(imp);
			}
			{
				Implication imp = new Implication();
				imp.add(new Equality(m_stateCounter, new Constant(m_numSteps)));
				imp.add(new Equality(m_stateCounter.next(), new Constant(m_numSteps)));
				c.add(imp);
			}
		}
		for (int i = 0; i < m_steps.size(); i++)
		{
			Implication imp = new Implication();
			imp.add(new Equality(m_stateCounter.next(), new Constant(i)));
			imp.add(eventsForStep(true, i));
			c.add(imp);
		}
		if (!m_loop)
		{
			Implication imp = new Implication();
			imp.add(new Equality(m_stateCounter.next(), new Constant(m_numSteps)));
			imp.add(hasLength(true, 0));
			c.add(imp);
		}
	}

	@Override
	/*@ pure non_null @*/ public List<Variable> getVariables()
	{
		List<Variable> vars = super.getVariables();
		vars.add(m_stateCounter);
		return vars;
	}
}
