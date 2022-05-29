package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.ConstantTrue;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Term;

/**
 * Binary function representing equality between two values.
 */
public class FunctionEquals implements BinaryFunctionCall
{
	/**
	 * The input domain for a particular instance of the function.
	 */
	protected Domain m_inDomain;
	
	/**
	 * Creates a new instance of the function.
	 * @param in_domain The input domain for a particular instance of the
	 * function
	 */
	public FunctionEquals(Domain in_domain)
	{
		super();
		m_inDomain = in_domain;
	}
	
	@Override
	public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
	{
		// Written as: (z = true) <-> (x = y)
		Equivalence eqv = new Equivalence();
		eqv.add(new Equality(z, ConstantTrue.TRUE));
		eqv.add(new Equality(x, y));
		return eqv;
	}

	@Override
	public Domain getInputDomain(int index) 
	{
		return m_inDomain;
	}

	@Override
	public Domain getOutputDomain() 
	{
		return BooleanDomain.instance;
	}

}
