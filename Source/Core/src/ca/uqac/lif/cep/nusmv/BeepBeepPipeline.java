package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.BooleanArrayAccessCondition;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.BooleanVariableCondition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.ScalarVariable;

/**
 * The main module of a NuSMV model containing a pipeline of BeepBeep
 * processor modules.
 */
public class BeepBeepPipeline extends ContainerModule
{
	/*@ non_null @*/ protected final ProcessorQueue[] m_inputs;
	
	/*@ non_null @*/ protected final ProcessorQueue[] m_outputs;
	
	protected final static Domain s_falseDomain = new Domain(new Object[] {false});
	
	public BeepBeepPipeline(ProcessorQueue[] inputs, ProcessorQueue[] outputs)
	{
		super("main", 0, new Domain[0], 0, new Domain[0], false, 0, 0);
		m_inputs = inputs;
		for (ProcessorQueue in : inputs)
		{
			add(in.m_arrayContents);
			add(in.m_arrayFlags);
		}
		m_outputs = outputs;
		for (ProcessorQueue out : outputs)
		{
			add(out.m_arrayContents);
			add(out.m_arrayFlags);
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
	protected void addToInit(Conjunction c)
	{
		c.add(new Negation(new BooleanVariableCondition(m_resetFlag)));
		//Not necessary
		for (int i = 0; i < m_inputs.length; i++)
		{
			c.add(m_inputs[i].isWellFormed());
		}
		for (int i = 0; i < m_outputs.length; i++)
		{
			c.add(m_outputs[i].isWellFormed());
		}
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(new Negation(new BooleanVariableCondition(m_resetFlag.next())));
	}
	
	@Override
	public ProcessorModule duplicate()
	{
		// Never happens, so don't implement
		return null;
	}
}
