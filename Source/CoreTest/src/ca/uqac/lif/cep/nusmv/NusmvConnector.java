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

import java.util.Map;

public class NusmvConnector
{
	/*@ non_null @*/ protected final Map<ProcessorModule,ProcessorConnection> m_connections;
	
	/**
	 * Registers the connection between the output i of module m1 and the input
	 * j of module m2.
	 * @param p1 The source module
	 * @param i The index of its output
	 * @param p2 The destination module
	 * @param j The index of its input
	 */
	public void connect(ProcessorModule p1, int i, ProcessorModule p2, int j)
	{
		ProcessorConnection conn = new ProcessorConnection(p1, i, p2, j);
		m_connections.put(p1, conn);
	}
	
	protected static class ProcessorConnection
	{
		/*@ non_null @*/ protected ProcessorModule m_p1;
		
		/*@ non_null @*/ protected ProcessorModule m_p2;
		
		protected int m_i;
		
		protected int m_j;
		
		/*@ non_null @*/ protected ProcessorQueue m_pipe;
		
		public ProcessorConnection(ProcessorModule p1, int i, ProcessorModule p2, int j)
		{
			super();
			m_p1 = p1;
			m_p2 = p2;
			m_i = i;
			m_j = j;
			m_pipe = new ProcessorQueue(); // TODO guess domain
		}
	}
}
