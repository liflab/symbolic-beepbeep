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

import ca.uqac.lif.nusmv4j.ArrayVariable;

public class NusmvQueue 
{
	/*@ non_null @*/ protected final ArrayVariable m_arrayFlags;
	
	public NusmvQueue(ArrayVariable flags)
	{
		super();
		m_arrayFlags = flags;
	}
	
	/**
	 * Sets the maximum size of this queue to a new value.
	 * @param size The new queue size
	 * @return This queue
	 */
	public NusmvQueue setSize(int size)
	{
		m_arrayFlags.setDimension(size);
		return this;
	}
	
	/**
	 * Gets the size of this queue.
	 * @return
	 */
	public int getSize()
	{
		return m_arrayFlags.getDimension();
	}
	
	/**
	 * Gets the internal array variable representing this queue.
	 * @return The variable
	 */
	/*@ pure non_null @*/ public ArrayVariable getVariable()
	{
		return m_arrayFlags;
	}
}
