package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Domain;

public class WindowModule extends ProcessorModule
{
	/**
	 * The width of the window.
	 */
	protected final int m_width;
	
	/**
	 * The processor to run on each window.
	 */
	protected final ProcessorModule m_processor;
	
	/**
	 * An array of processor instances, one for each offset of the processor
	 * to run on each window.
	 */
	protected final ProcessorModule[] m_processors;
	
	public WindowModule(String name, ProcessorModule processor, int width, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, 1, new Domain[] {in_domain}, out_domain, Q_in, Q_b, Q_out);
		m_width = width;
		m_processor = processor;
		m_processors = new ProcessorModule[Q_in + width];
		for (int i = 0; i < m_processors.length; i++)
		{
			m_processors[i] = m_processor.duplicate();
		}
	}
	
	@Override
	public WindowModule duplicate()
	{
		WindowModule m = new WindowModule(getName(), m_processor, m_width, getFrontPorch(0).getDomain(), getBackPorch().getDomain(), getFrontPorch(0).getSize(), getBuffer(0).getSize(), getBackPorch().getSize());
		super.copyInto(m);
		return m;
	}
	
	

}
