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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.nusmv4j.ModuleDomain;
import ca.uqac.lif.nusmv4j.ScalarVariable;
import ca.uqac.lif.nusmv4j.Variable;

/**
 * An object linking input/output pipes of processor modules by creating
 * instances of {@link ProcessorQueue} for each distinct connection.
 */
public class NusmvConnector
{
	/**
	 * A counter to give unique names to each new processor queue created by
	 * this connector.
	 */
	protected int m_queueCounter;

	/**
	 * A configurable prefix to name each instance of processor queue.
	 */
	/*@ non_null @*/ protected String m_queuePrefix;

	/**
	 * A map associating each processor module with a map, itself linking
	 * input pipe indices to processor connections.
	 */
	/*@ non_null @*/ protected final Map<ProcessorModule,Map<Integer,ProcessorConnection>> m_inputConnections;

	/**
	 * A map associating each processor module with a map, itself linking
	 * output pipe indices to processor connections.
	 */
	/*@ non_null @*/ protected final Map<ProcessorModule,Map<Integer,ProcessorConnection>> m_outputConnections;
	
	/**
	 * The list of inner pipes instantiated by this connector.
	 */
	/*@ non_null @*/ protected final List<ProcessorQueue> m_innerQueues;
	
	/**
	 * A reset flag shared by all processors connected by this connector
	 * instance.
	 */
	protected ScalarVariable m_resetFlag;

	/**
	 * Creates a new connector instance.
	 */
	public NusmvConnector(ScalarVariable reset_flag)
	{
		super();
		m_resetFlag = reset_flag;
		m_inputConnections = new HashMap<ProcessorModule,Map<Integer,ProcessorConnection>>();
		m_outputConnections = new HashMap<ProcessorModule,Map<Integer,ProcessorConnection>>();
		m_innerQueues = new ArrayList<ProcessorQueue>();
		m_queueCounter = 0;
		m_queuePrefix = "q_";
	}
	
	/**
	 * Assigns a queue to the input pipe of a processor module.
	 * @param p The processor module
	 * @param i The index of the input pipe
	 * @param q The queue
	 * @return The input processor connection
	 */
	public InputProcessorConnection setInput(ProcessorModule p, int i, ProcessorQueue q)
	{
		InputProcessorConnection conn = new InputProcessorConnection(p, i, q);
		registerInputConnection(p, i, conn);
		return conn;
	}
	
	/**
	 * Assigns a queue to the output pipe of a processor module.
	 * @param p The processor module
	 * @param i The index of the output pipe
	 * @param q The queue
	 * @return The output processor connection
	 */
	public OutputProcessorConnection setOutput(ProcessorModule p, int i, ProcessorQueue q)
	{
		OutputProcessorConnection conn = new OutputProcessorConnection(p, i, q);
		registerOutputConnection(p, i, conn);
		return conn;
	}

	/**
	 * Registers the connection between the output i of module p1 and the input
	 * j of module p2.
	 * @param p1 The source module
	 * @param i The index of its output
	 * @param p2 The destination module
	 * @param j The index of its input
	 */
	public void connect(ProcessorModule p1, int i, ProcessorModule p2, int j)
	{
		InnerProcessorConnection conn = new InnerProcessorConnection(p1, i, p2, j);
		registerOutputConnection(p1, i, conn);
		registerInputConnection(p2, j, conn);
		m_innerQueues.add(conn.getQueue());
	}
	
	/**
	 * Registers the connection to the input j of module p.
	 * @param p The module
	 * @param j The index of its input
	 * @param conn The connection to register
	 */
	protected void registerInputConnection(ProcessorModule p, int j, ProcessorConnection conn)
	{
		Map<Integer,ProcessorConnection> entries = null;
		if (m_inputConnections.containsKey(p))
		{
			entries = m_inputConnections.get(p);
		}
		else
		{
			entries = new HashMap<Integer,ProcessorConnection>(p.getInputArity());
			m_inputConnections.put(p, entries);
		}
		entries.put(j, conn);	
	}
	
	/**
	 * Registers the connection to the input j of module p.
	 * @param p The module
	 * @param j The index of its input
	 * @param conn The connection to register
	 */
	protected void registerOutputConnection(ProcessorModule p, int i, ProcessorConnection conn)
	{
		Map<Integer,ProcessorConnection> entries = null;
		if (m_outputConnections.containsKey(p))
		{
			entries = m_outputConnections.get(p);
		}
		else
		{
			entries = new HashMap<Integer,ProcessorConnection>(p.getOutputArity());
			m_outputConnections.put(p, entries);
		}
		entries.put(i, conn);
	}
	
	/**
	 * Gets the domain for a processor module instance connected by this
	 * connector. The domain gives the list of all arguments of the module.
	 * @param p The processor module to look for
	 * @return The domain
	 */
	public ModuleDomain getDomain(ProcessorModule p)
	{
		Variable[] arguments = new Variable[2 * p.getInputArity() + 2 * p.getOutputArity() + 1];
		int index = 0;
		if (!m_inputConnections.containsKey(p))
		{
			throw new NusmvConnectorException("No inputs defined for " + p.getName());
		}
		Map<Integer,ProcessorConnection> in_conns = m_inputConnections.get(p);
		for (int i = 0; i < p.getInputArity(); i++)
		{
			if (!in_conns.containsKey(i))
			{
				throw new NusmvConnectorException("Input pipe " + i + " of " + p.getName() + " is not connected");
			}
			ProcessorConnection conn = in_conns.get(i);
			arguments[index++] = conn.m_pipe.m_arrayContents;
			arguments[index++] = conn.m_pipe.m_arrayFlags;
			
		}
		if (!m_outputConnections.containsKey(p))
		{
			throw new NusmvConnectorException("No outputs defined for " + p.getName());
		}
		Map<Integer,ProcessorConnection> out_conns = m_outputConnections.get(p);
		for (int i = 0; i < p.getOutputArity(); i++)
		{
			if (!out_conns.containsKey(i))
			{
				throw new NusmvConnectorException("Output pipe " + i + " of " + p.getName() + " is not connected");
			}
			ProcessorConnection conn = out_conns.get(i);
			ProcessorQueue q = conn.getQueue();
			arguments[index++] = q.m_arrayContents;
			arguments[index++] = q.m_arrayFlags;
		}
		arguments[index++] = m_resetFlag;
		return new ModuleDomain(p, arguments);
	}
	
	/**
	 * Gets the list of inner pipes instantiated by this connector.
	 * @return The list
	 */
	/*@ pure non_null @*/ public List<ProcessorQueue> getInnerQueues()
	{
		return m_innerQueues;
	}
	
	public abstract class ProcessorConnection
	{
		/**
		 * The processor pipe instance that is shared by both modules.
		 */
		protected ProcessorQueue m_pipe;
		
		protected ProcessorConnection()
		{
			super();
		}
		
		public ProcessorConnection(ProcessorQueue pipe)
		{
			super();
			m_pipe = pipe;
		}
		
		public ProcessorQueue getQueue()
		{
			return m_pipe;
		}
	}
	
	/**
	 * Connection associating a processor queue to an input pipe of a processor
	 * module.
	 */
	public class InputProcessorConnection extends ProcessorConnection
	{
		/**
		 * The processor module.
		 */
		protected ProcessorModule m_module;
		
		/**
		 * The index of the input pipe.
		 */
		protected int m_index;
		
		public InputProcessorConnection(ProcessorModule p, int index, ProcessorQueue pipe)
		{
			super();
			m_pipe = pipe;
			m_index = index;
			m_module = p;
		}
		
		public int getIndex()
		{
			return m_index;
		}
		
		@Override
		public String toString()
		{
			return m_module.getName() + "?" + m_index;
		}
	}
	
	/**
	 * Connection associating a processor queue to an output pipe of a
	 * processor module.
	 */
	public class OutputProcessorConnection extends ProcessorConnection
	{
		/**
		 * The processor module.
		 */
		protected ProcessorModule m_module;
		
		/**
		 * The index of the input pipe.
		 */
		protected int m_index;
		
		public OutputProcessorConnection(ProcessorModule p, int index, ProcessorQueue pipe)
		{
			super();
			m_pipe = pipe;
			m_index = index;
			m_module = p;
		}
		
		public int getIndex()
		{
			return m_index;
		}
		
		@Override
		public String toString()
		{
			return m_module.getName() + "!" + m_index;
		}
	}

	/**
	 * An object registering the connection between an output pipe and an
	 * input pipe of two processor modules.
	 */
	public class InnerProcessorConnection extends ProcessorConnection
	{
		/**
		 * The source module.
		 */
		/*@ non_null @*/ protected ProcessorModule m_p1;

		/**
		 * The destination module.
		 */
		/*@ non_null @*/ protected ProcessorModule m_p2;

		/**
		 * The index of the output pipe of the source module.
		 */
		protected int m_i;

		/**
		 * The index of the input pipe of the destination module.
		 */
		protected int m_j;

		/**
		 * Creates a new processor connection.
		 * @param p1 The source module
		 * @param i The index of its output
		 * @param p2 The destination module
		 * @param j The index of its input
		 */
		public InnerProcessorConnection(ProcessorModule p1, int i, ProcessorModule p2, int j)
		{
			super();
			m_p1 = p1;
			m_p2 = p2;
			m_i = i;
			m_j = j;
			ProcessorQueue out = p1.getBackPorch(i);
			ProcessorQueue in = p2.getFrontPorch(j);
			if (out.getSize() != in.getSize())
			{
				throw new NusmvConnectorException("Incompatible sizes for output " + i + " of " + p1 + " and input " + j + " of " + p2);
			}
			if (!out.getDomain().equals(in.getDomain()))
			{
				throw new NusmvConnectorException("Incompatible domains for output " + i + " of " + p1 + " and input " + j + " of " + p2);
			}
			int q_id = m_queueCounter++;
			m_pipe = new ProcessorQueue(m_queuePrefix + q_id, "qc_" + q_id, "qb_" + q_id, out.getSize(), out.getDomain());
		}
		
		@Override
		public String toString()
		{
			return m_p1.getName() + "!" + m_i + "\u2192" + m_p2.getName() + "?" + m_j;
		}
	}

	/**
	 * Exception thrown when attempting to connect incompatible processor
	 * modules.
	 */
	protected static class NusmvConnectorException extends RuntimeException
	{
		/**
		 * Dummy UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new exception with a specific message.
		 * @param message The message
		 */
		public NusmvConnectorException(String message)
		{
			super(message);
		}
	}
}
