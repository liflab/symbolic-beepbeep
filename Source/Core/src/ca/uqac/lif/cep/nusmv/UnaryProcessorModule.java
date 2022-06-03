package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;

/**
 * NuSMV module representing processors having an input arity of 1. These
 * processors ingest all the events of their front porch at any computation
 * step, hence they never need an internal buffer. This simplifies the
 * expression of some conditions; for instance, asserting that the processor
 * has <i>n</i> inputs ready to be consumed is equivalent to stating that
 * its front porch contains exactly <i>n</i> elements. 
 */
public class UnaryProcessorModule extends ProcessorModule
{
	public UnaryProcessorModule(String name, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, 1, in_domain, out_domain, Q_in, Q_b, Q_out);
	}
	
	/**
	 * Produces the condition stipulating that the unary processor has at least
	 * <i>n</i> inputs ready to be consumed.
	 * @param n The number of input events
	 * @return The condition
	 */
	/*@ pure non_null @*/ public Condition minInputs(int n)
	{
		return m_frontPorches[0].minLength(n);
	}
	
	/**
	 * Produces the condition stipulating that the unary processor has exactly
	 * <i>n</i> inputs ready to be consumed.
	 * @param n The number of input events
	 * @return The condition
	 */
	/*@ pure non_null @*/ public Condition hasInputs(int n)
	{
		return m_frontPorches[0].hasLength(n);
	}

}
