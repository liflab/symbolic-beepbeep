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
