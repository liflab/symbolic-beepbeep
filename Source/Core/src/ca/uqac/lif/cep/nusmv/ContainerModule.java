package ca.uqac.lif.cep.nusmv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.cep.nusmv.NusmvConnector.InputProcessorConnection;
import ca.uqac.lif.cep.nusmv.NusmvConnector.OutputProcessorConnection;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ModuleDomain;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

/**
 * A processor module that contains other processors connected by pipes.
 */
public abstract class ContainerModule extends ProcessorModule implements CompositeProcessorModule
{
	/**
	 * The processor modules that are "contained" within This container module. The map
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
	
	public ContainerModule(String name, int in_arity, Domain[] in_domains, int out_arity, Domain[] out_domains, boolean has_reset, int Q_in, int Q_out)
	{
		super(name, in_arity, in_domains, out_arity, out_domains, has_reset, Q_in, 0, Q_out);
		m_moduleCounter = 0;
		m_prefix = "p_";
		m_contents = new HashMap<ProcessorModule,String>();
		m_connector = new NusmvConnector(m_resetFlag);
		m_inputConnections = new HashMap<Integer,InputProcessorConnection>(in_arity);
		m_outputConnections = new HashMap<Integer,OutputProcessorConnection>(out_arity);
	}
	
	/**
	 * Adds one or more processor modules to This container module.
	 * @param modules The modules to add
	 * @return This container module
	 */
	public ContainerModule add(ProcessorModule ... modules)
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
	 * @return This container module
	 */
	public ContainerModule connect(ProcessorModule p1, int i, ProcessorModule p2, int j)
	{
		m_connector.connect(p1, i, p2, j);
		return this;
	}
	
	@Override
	public void addModules(/*@ non_null @*/ Set<ProcessorModule> modules)
	{
		for (ProcessorModule mod : m_contents.keySet())
		{
			if (mod instanceof CompositeProcessorModule)
			{
				((CompositeProcessorModule) mod).addModules(modules);
			}
			modules.add(mod);
		}
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
	

	/*@Override
	public IntegerRange getOutputRange(IntegerRange ... ranges)
	{
		for (int i = 0)
		m_connector.m_inputConnections.get(arg0);
				return null;
	}*/
	
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

}
