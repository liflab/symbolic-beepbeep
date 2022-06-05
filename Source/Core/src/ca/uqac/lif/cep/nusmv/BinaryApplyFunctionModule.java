package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Equivalence;

public class BinaryApplyFunctionModule extends BinaryModule
{
	/**
	 * The function to be applied on event fronts.
	 */
	/*@ non_null @*/ protected final BinaryFunctionCall m_function;

	public BinaryApplyFunctionModule(String name, BinaryFunctionCall f, int Q_in, int Q_b, int Q_out)
	{
		super(name, f.getInputDomain(0), f.getInputDomain(1), f.getOutputDomain(), Q_in, Q_b, Q_out);
		m_function = f;
	}

	/*@ non_null @*/ public Condition buildInitialState(int Q_up, int Q_b)
	{
		Conjunction big_and = new Conjunction();
		big_and.add(emptyBuffers());
		// TODO
		return big_and;
	}


	/**
	 * Generates the condition stipulating that the size of the back porch is
	 * equal to the number of complete event fronts in the input pipes.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition frontsVsBackPorch()
	{
		ProcessorQueue back_porch = getBackPorch();
		Conjunction and = new Conjunction();
		for (int i = 0; i <= back_porch.getSize(); i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(numFronts(i));
			eq.add(back_porch.hasLength(i));
			and.add(eq);
		}
		return and;
	}

	@Override
	public Condition isFrontToOutput(QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		return isNthFront(sigma1, m1, sigma2, m2, n);
	}

	@Override
	public Condition getOutputCondition(QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		Condition right = m_function.getCondition(
				at(sigma1, 0, m1), // first argument of f
				at(sigma2, 1, m2), // second argument of f
				getBackPorch().valueAt(n) // cell of the back porch to store value
				);
		return right;
	}
}
