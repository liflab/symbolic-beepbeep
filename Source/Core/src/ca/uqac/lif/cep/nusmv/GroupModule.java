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

import ca.uqac.lif.cep.nusmv.NusmvConnector.InputProcessorConnection;
import ca.uqac.lif.cep.nusmv.NusmvConnector.OutputProcessorConnection;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;

/**
 * Container module associating its own input/output pipes to input/output
 * pipes of some of its contained modules.
 */
public class GroupModule extends ContainerModule
{
	public GroupModule(String name, int in_arity, Domain[] in_domains, int out_arity, Domain[] out_domains, int Q_in, int Q_out)
	{
		super(name, in_arity, in_domains, out_arity, out_domains, true, Q_in, Q_out);
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
		// Actually not necessary
		//c.add(frontPorchMatches(false));
		//c.add(backPorchMatches(false));
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		// Actually not necessary
		//c.add(frontPorchMatches(true));
		//c.add(backPorchMatches(true));
	}
	
	/**
	 * Associates input pipe i of the group to input pipe j of an internal
	 * processor module.
	 * @param i The index of the group's input pipe
	 * @param p The internal processor module instance
	 * @param j The index of that processor's input pipe
	 * @return This container module
	 */
	public ContainerModule associateInput(int i, ProcessorModule p, int j)
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
	 * @return This container module
	 */
	public ContainerModule associateOutput(int i, ProcessorModule p, int j)
	{
		OutputProcessorConnection conn = m_connector.setOutput(p, i, getBackPorch(j));
		m_outputConnections.put(j, conn);
		return this;
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
