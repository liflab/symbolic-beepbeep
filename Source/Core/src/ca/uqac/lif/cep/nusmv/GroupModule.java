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
import java.util.List;
import java.util.Map;

import ca.uqac.lif.cep.nusmv.NusmvConnector.InputProcessorConnection;
import ca.uqac.lif.cep.nusmv.NusmvConnector.OutputProcessorConnection;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.ModuleDomain;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class GroupModule extends ProcessorModule
{
	/**
	 * The processor modules that are "contained" within this group. The map
	 * links module instances with the unique identifier given to them when
	 * added to the group.
	 */
	/*@ non_null @*/ protected final Map<ProcessorModule,String> m_contents;
	
	/**
	 * A counter used to give each inner processors module a unique identifier.
	 */
	protected int m_moduleCounter;
	
	/**
	 * The prefix given to each processor module variable.
	 */
	protected String m_prefix;
	
	/**
	 * A connector to link processor modules in the group.
	 */
	/*@ non_null @*/ protected final NusmvConnector m_connector;
	
	/**
	 * Associations between a group's front porches and internal processor
	 * modules contained in the group.
	 */
	/*@ non_null @*/ protected final Map<Integer,InputProcessorConnection> m_inputConnections;
	
	/**
	 * Associations between a group's back porches and internal processor
	 * modules contained in the group.
	 */
	/*@ non_null @*/ protected final Map<Integer,OutputProcessorConnection> m_outputConnections;
	
	public GroupModule(String name, int in_arity, Domain[] in_domains, int out_arity, Domain[] out_domains, int Q_in, int Q_out)
	{
		super(name, in_arity, in_domains, out_arity, out_domains, Q_in, 0, Q_out);
		m_moduleCounter = 0;
		m_prefix = "p_";
		m_contents = new HashMap<ProcessorModule,String>();
		m_connector = new NusmvConnector(m_resetFlag);
		m_inputConnections = new HashMap<Integer,InputProcessorConnection>(in_arity);
		m_outputConnections = new HashMap<Integer,OutputProcessorConnection>(out_arity);
	}
	
	/**
	 * Adds one or more processor modules to this group.
	 * @param modules The modules to add
	 * @return This group
	 */
	public GroupModule add(ProcessorModule ... modules)
	{
		for (ProcessorModule m : modules)
		{
			String var_name = m_prefix + m_moduleCounter;
			m_moduleCounter++;
			m_contents.put(m, var_name);
		}
		return this;
	}
	
	/**
	 * Registers the connection between the output i of module p1 and the input
	 * j of module p2.
	 * @param p1 The source module
	 * @param i The index of its output
	 * @param p2 The destination module
	 * @param j The index of its input
	 * @return This group
	 */
	public GroupModule connect(ProcessorModule p1, int i, ProcessorModule p2, int j)
	{
		m_connector.connect(p1, i, p2, j);
		return this;
	}
	
	/**
	 * Associates input pipe i of the group to input pipe j of an internal
	 * processor module.
	 * @param i The index of the group's input pipe
	 * @param p The internal processor module instance
	 * @param j The index of that processor's input pipe
	 * @return This group
	 */
	public GroupModule associateInput(int i, ProcessorModule p, int j)
	{
		InputProcessorConnection conn = m_connector.setInput(p, j, getFrontPorch(i));
		m_inputConnections.put(i, conn);
		return this;
	}
	
	/**
	 * Associates output pipe i of an internal processor module to output pipe
	 * j of the group.
	 * @param i The index of the internal processor's output pipe
	 * @param p The internal processor module instance
	 * @param j The index of the group's output pipe
	 * @return This group
	 */
	public GroupModule associateOutput(int i, ProcessorModule p, int j)
	{
		OutputProcessorConnection conn = m_connector.setOutput(p, i, getBackPorch(j));
		m_outputConnections.put(j, conn);
		return this;
	}
	
	@Override
	public void print(PrettyPrintStream ps)
	{
		// Add any new queue variables from the connector before printing
		updateVariables();
		// Update the definition of each inner module to reflect connections
		updateModules();
		super.print(ps);
	}
	
	/**
	 * Updates the list of internal queues of the processor to their most
	 * up-to-date state.
	 */
	protected void updateVariables()
	{
		List<ProcessorQueue> queues = m_connector.getInnerQueues();
		for (ProcessorQueue q : queues)
		{
			if (!m_variables.contains(q.m_arrayContents))
			{
				m_variables.add(q.m_arrayContents);
			}
			if (!m_variables.contains(q.m_arrayFlags))
			{
				m_variables.add(q.m_arrayFlags);
			}
		}
	}
	
	/**
	 * Updates the list of internal modules of the processor to their most
	 * up-to-date state.
	 */
	protected void updateModules()
	{
		for (Map.Entry<ProcessorModule,String> e : m_contents.entrySet())
		{
			String var_name = e.getValue();
			ModuleDomain d = m_connector.getDomain(e.getKey());
			m_subModules.put(var_name, d);
		}
	}

	@Override
	public ProcessorModule duplicate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addToInit(Conjunction c) 
	{
		c.add(frontPorchMatches(false));
		c.add(backPorchMatches(false));
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(frontPorchMatches(true));
		c.add(backPorchMatches(true));
	}
	
	public Condition frontPorchMatches(boolean next)
	{
		Conjunction and = new Conjunction();
		for (int i = 0; i < getInputArity(); i++)
		{
			and.add(new FrontPorchMatch(next, i));
		}
		return and;
	}
	
	public Condition backPorchMatches(boolean next)
	{
		Conjunction and = new Conjunction();
		for (int i = 0; i < getOutputArity(); i++)
		{
			and.add(new BackPorchMatch(next, i));
		}
		return and;
	}
	
	/**
	 * Writes the condition stipulating that a front porch of the group has
	 * the same length and same contents as the front porch of the inner
	 * processor it is associated to.
	 */
	public class FrontPorchMatch extends Conjunction
	{
		protected final boolean m_next;
		
		protected final int m_index;
		
		public FrontPorchMatch(boolean next, int in_index)
		{
			super();
			m_next = next;
			m_index = in_index;
			ProcessorQueue group_porch = getFrontPorch(in_index);
			ProcessorQueue in_porch = m_inputConnections.get(in_index).getQueue();
			for (int len = 0; len <= group_porch.getSize(); len++)
			{
				Implication imp = new Implication();
				imp.add(group_porch.hasLength(next, len));
				Conjunction in_and = new Conjunction();
				in_and.add(in_porch.hasLength(next, len));
				for (int i = 0; i < len; i++)
				{
					in_and.add(new Equality(
							group_porch.valueAt(next, i),
							in_porch.valueAt(next, i)));
				}
				imp.add(in_and);
				add(imp);
			}
		}
		
		@Override
		public String toString()
		{
			InputProcessorConnection conn = m_inputConnections.get(m_index);
			return conn.m_module.getName() + "?[" + conn.m_index + "]" + " = " + getName() + "?[" + m_index + "]";
		}
	}
	
	/**
	 * Writes the condition stipulating that a back porch of the group has
	 * the same length and same contents as the back porch of the inner
	 * processor it is associated to.
	 */
	public class BackPorchMatch extends Conjunction
	{
		protected final boolean m_next;
		
		protected final int m_index;
		
		public BackPorchMatch(boolean next, int out_index)
		{
			super();
			m_next = next;
			m_index = out_index;
			ProcessorQueue group_porch = getBackPorch(out_index);
			ProcessorQueue out_porch = m_outputConnections.get(out_index).getQueue();
			for (int len = 0; len <= group_porch.getSize(); len++)
			{
				Implication imp = new Implication();
				imp.add(group_porch.hasLength(next, len));
				Conjunction in_and = new Conjunction();
				in_and.add(out_porch.hasLength(next, len));
				for (int i = 0; i < len; i++)
				{
					in_and.add(new Equality(
							group_porch.valueAt(next, i),
							out_porch.valueAt(next, i)));
				}
				imp.add(in_and);
				add(imp);
			}
		}
		
		@Override
		public String toString()
		{
			InputProcessorConnection conn = m_inputConnections.get(m_index);
			return getName() + "![" + m_index + "] = " + conn.m_module.getName() + "![" + conn.m_index + "]";
		}
	}
}
