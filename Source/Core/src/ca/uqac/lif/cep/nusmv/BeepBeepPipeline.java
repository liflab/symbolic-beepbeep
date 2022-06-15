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

import ca.uqac.lif.nusmv4j.BooleanVariableCondition;
import ca.uqac.lif.nusmv4j.Comment;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Negation;

/**
 * The main module of a NuSMV model containing a pipeline of BeepBeep
 * processor modules.
 */
public class BeepBeepPipeline extends ContainerModule
{
	/*@ non_null @*/ protected final ProcessorQueue[] m_inputs;
	
	/*@ non_null @*/ protected final ProcessorQueue[] m_outputs;
	
	/*@ non_null @*/ protected final static Domain s_falseDomain = new Domain(new Object[] {false});
	
	/*@ non_null @*/ protected final String m_mainName;
	
	public BeepBeepPipeline(String name, ProcessorQueue[] inputs, ProcessorQueue[] outputs)
	{
		super("main", 0, new Domain[0], 0, new Domain[0], false, 0, 0);
		m_inputs = inputs;
		m_mainName = name;
		for (ProcessorQueue in : inputs)
		{
			add(in.getVariables());
		}
		m_outputs = outputs;
		for (ProcessorQueue out : outputs)
		{
			add(out.getVariables());
		}
		add(m_resetFlag);
	}
	
	public void setInput(ProcessorModule p, int i, int j)
	{
		m_connector.setInput(p, i, m_inputs[j]);
	}
	
	public void setOutput(ProcessorModule p, int i, int j)
	{
		m_connector.setOutput(p, i, m_outputs[j]);
	}
	
	@Override
	protected void addToComment(Comment c)
	{
		c.addLine("Module: " + m_mainName);
	}

	@Override
	protected void addToInit(Conjunction c)
	{
		c.add(new Negation(new BooleanVariableCondition(m_resetFlag)));
		for (int i = 0; i < m_inputs.length; i++)
		{
			m_inputs[i].addToInit(c);
		}
		for (int i = 0; i < m_outputs.length; i++)
		{
			m_outputs[i].addToInit(c);
		}
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(new Negation(new BooleanVariableCondition(m_resetFlag.next())));
		for (int i = 0; i < m_inputs.length; i++)
		{
			m_inputs[i].addToTrans(c);
		}
		for (int i = 0; i < m_outputs.length; i++)
		{
			m_outputs[i].addToTrans(c);
		}
	}
	
	@Override
	public ProcessorModule duplicate()
	{
		// Never happens, so don't implement
		return null;
	}
}
