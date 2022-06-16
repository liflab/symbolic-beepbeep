package ca.uqac.lif.cep.nusmv;

import java.util.Set;

/**
 * Module that references other processors.
 */
public interface CompositeProcessorModule
{
	/**
	 * Recursively adds to a set all the processor module instances referred to
	 * in this processor module.
	 * @param modules The set where module instance are added
	 */
	public void addModules(/*@ non_null @*/ Set<ProcessorModule> modules);
}
