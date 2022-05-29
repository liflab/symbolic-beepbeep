package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.ArrayAccess;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;
import ca.uqac.lif.nusmv4j.Term;

public class BinaryApplyFunctionModule extends BeepBeepModule
{
	/**
	 * The function to be applied on event fronts.
	 */
	/*@ non_null @*/ protected final BinaryFunctionCall m_function;

	public BinaryApplyFunctionModule(String name, BinaryFunctionCall f, int Q_in, int Q_b, int Q_out)
	{
		super(name, 2, f.getInputDomain(0), f.getOutputDomain(), Q_in, Q_b, Q_out);
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
	 * Generates the condition stipulating that internal buffers are empty.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition emptyBuffers()
	{
		Conjunction and = new Conjunction();
		and.add(getBuffer(0).hasLength(0));
		and.add(getBuffer(1).hasLength(0));
		return and;
	}

	/**
	 * Generates the condition asserting that the input pipes contain exactly
	 * n complete event fronts.
	 * @param n The number of event fronts
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition numFronts(int n)
	{
		Disjunction or = new Disjunction();
		{
			Conjunction and = new Conjunction();
			and.add(hasTotalPipe(0, n));
			and.add(minTotalPipe(1, n));
			or.add(and);
		}
		{
			Conjunction and = new Conjunction();
			and.add(minTotalPipe(0, n));
			and.add(hasTotalPipe(1, n));
			or.add(and);
		}
		return or;
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
		for (int i = 0; i <= back_porch.getSize() - 1; i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(numFronts(i));
			eq.add(back_porch.hasLength(i));
			and.add(eq);
		}
		return and;
	}

	/**
	 * Generates the condition associating back porch cells with the
	 * appropriate value, by applying the binary function to the corresponding
	 * input front.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition backPorchValues()
	{
		int Q_in = Math.min(getFrontPorch(0).getSize(), getFrontPorch(1).getSize());
		Conjunction and = new Conjunction();
		for (QueueType sigma1 : new QueueType[] {QueueType.PORCH, QueueType.BUFFER})
		{
			for (QueueType sigma2 : new QueueType[] {QueueType.PORCH, QueueType.BUFFER})
			{
				for (int n = 0; n <= Q_in - 1; n++)
				{
					for (int m1 = 0; m1 <= getSize(sigma1, 0) - 1; m1++)
					{
						for (int m2 = 0; m2 <= getSize(sigma2, 1) - 1; m2++)
						{
							Implication imp = new Implication();
							{
								Conjunction left = new Conjunction();
								left.add(at(sigma1, 0, m1, n));
								left.add(at(sigma2, 1, m2, n));
								imp.add(left);
							}
							{
								Condition right = m_function.getCondition(
									at(sigma1, 0, m1), // first argument of f
									at(sigma2, 1, m2), // second argument of f
									getBackPorch().valueAt(n) // cell of the back porch to store value
								);
								imp.add(right);
							}
						}
					}
				}
			}
		}
		return and;
	}

	/**
	 * Generates the condition specifying the size of both internal buffers in
	 * the next state, based on the size of their respective input pipe and the
	 * number of complete fronts in the current state.
	 * @param pipe_index The index of the input pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextBufferSizes()
	{
		Conjunction big_and = new Conjunction();
		big_and.add(nextBufferSize(0));
		big_and.add(nextBufferSize(1));
		return big_and;
	}

	/**
	 * Generates the condition specifying the size of an internal buffer in the
	 * next state, based on the size of the input pipe and the number of
	 * complete fronts in the current state.
	 * @param pipe_index The index of the input pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextBufferSize(int pipe_index)
	{
		int Q = getBackPorch().getSize();
		//Q=1;
		Conjunction and = new Conjunction();
		for (int nf = 0; nf <= Q - 1; nf++)
		{
			for (int nq = nf; nq <= Q - 1; nq++)
			{
				Implication imp = new Implication();
				{
					Conjunction left = new Conjunction();
					left.add(numFronts(nf));
					left.add(hasTotalPipe(pipe_index, nq));
					imp.add(left);
				}
				imp.add(getBuffer(pipe_index).next().hasLength(nq - nf));
				and.add(imp);
			}
		}
		return and;
	}
}
