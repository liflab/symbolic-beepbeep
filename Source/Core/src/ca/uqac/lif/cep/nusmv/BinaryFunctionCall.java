package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Interface implemented by binary functions that can express the relation
 * between their two arguments and their output as a Boolean condition.  
 */
public interface BinaryFunctionCall
{
	/**
	 * Gets the domain of one of the arguments of the function.
	 * @param index The index of the argument
	 * @return The domain
	 */
	public Domain getInputDomain(int index);
	
	/**
	 * Gets the domain of the return value of the function.
	 * @return The domain
	 */
	public Domain getOutputDomain();
	
	/**
	 * Produces the condition linking the arguments of the function to its
	 * output value. The condition is expressed in terms of x, y and z,
	 * and is expected to be true exactly for those values of x, y and z where
	 * f(x,y) = z. 
	 * @param x The first argument of the function
	 * @param y The second argument of the function
	 * @param z The output of the function
	 * @return The condition
	 */
	public Condition getCondition(Term<?> x, Term<?> y, Term<?> z);		
}