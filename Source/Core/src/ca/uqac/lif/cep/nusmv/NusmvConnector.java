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
		m_queueCounter = 0;
		m_queuePrefix = "q_";
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
		ProcessorConnection conn = new ProcessorConnection(p1, i, p2, j);
		{
			// Register output of p1
			Map<Integer,ProcessorConnection> entries = null;
			if (m_outputConnections.containsKey(p1))
			{
				entries = m_outputConnections.get(p1);
			}
			else
			{
				entries = new HashMap<Integer,ProcessorConnection>(p1.getOutputArity());
				m_outputConnections.put(p1, entries);
			}
			entries.put(i, conn);
		}
		{
			// Register input of p2
			Map<Integer,ProcessorConnection> entries = null;
			if (m_inputConnections.containsKey(p1))
			{
				entries = m_inputConnections.get(p1);
			}
			else
			{
				entries = new HashMap<Integer,ProcessorConnection>(p2.getInputArity());
				m_inputConnections.put(p2, entries);
			}
			entries.put(j, conn);
		}
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
			arguments[index++] = conn.m_pipe.m_arrayContents;
			arguments[index++] = conn.m_pipe.m_arrayFlags;
		}
		arguments[index++] = m_resetFlag;
		return new ModuleDomain(p, arguments);
	}

	/**
	 * An object registering the connection between an output pipe and an
	 * input pipe of two processor modules.
	 */
	protected class ProcessorConnection
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
		 * The processor pipe instance that is shared by both modules.
		 */
		/*@ non_null @*/ protected ProcessorQueue m_pipe;

		/**
		 * Creates a new processor connection.
		 * @param p1 The source module
		 * @param i The index of its output
		 * @param p2 The destination module
		 * @param j The index of its input
		 */
		public ProcessorConnection(ProcessorModule p1, int i, ProcessorModule p2, int j)
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
			m_pipe = new ProcessorQueue(m_queuePrefix + q_id, "qc", "qb", out.getSize(), out.getDomain());
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
