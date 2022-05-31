package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.Implication;

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
		for (int i = 0; i <= back_porch.getSize(); i++)
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
		int Q_in = getFrontPorch(pipe_index).getSize();
		int Q_b = getBuffer(pipe_index).getSize();
		int Q_out = getBackPorch().getSize();
		Conjunction big_and = new Conjunction();
		for (int nf = 0; nf <= Q_out; nf++)
		{
			for (int nq = nf; nq <= Q_in + Q_b; nq++)
			{
				Implication imp = new Implication();
				{
					Conjunction left = new Conjunction();
					left.add(numFronts(nf));
					left.add(hasTotalPipe(pipe_index, nq));
					imp.add(left);
				}
				imp.add(getBuffer(pipe_index).next().hasLength(nq - nf));
				big_and.add(imp);
			}
		}
		return big_and;
	}

	/**
	 * Generates the condition specifying the value of each cell of an
	 * internal buffer in the next state, based on the size of the input pipe
	 * and the number of complete fronts in the current state.
	 * @param pipe_index The index of the input pipe
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition nextBufferValues(int pipe_index)
	{
		int Q_in = getFrontPorch(pipe_index).getSize();
		int Q_b = getBuffer(pipe_index).getSize();
		int Q_out = getBackPorch().getSize();
		Conjunction big_and = new Conjunction();
		for (int nf = 0; nf <= Q_out; nf++)
		{
			for (int nq = nf; nq <= Q_in + Q_b; nq++)
			{
				Implication imp = new DebugImplication();
				{
					Conjunction left = new Conjunction();
					left.add(numFronts(nf));
					left.add(hasTotalPipe(pipe_index, nq));
					imp.add(left);
				}
				{
					Conjunction in_and = new Conjunction();
					for (int i = 0; i <= nq - nf - 1; i++)
					{
						for (QueueType sigma : new QueueType[] {QueueType.PORCH, QueueType.BUFFER})
						{
							for (int j = 0; j <= length(sigma, pipe_index) - 1; j++)
							{
								Implication in_imp = new Implication();
								in_imp.add(at(sigma, pipe_index, j, nf + i));
								in_imp.add(new Equality(
										at(sigma, pipe_index, j), // buffer/porch at position j
										getBuffer(pipe_index).next().valueAt(i) // next buffer at position i
										));
								in_and.add(in_imp);
							}
						}
					}
					imp.add(in_and);
				}
				big_and.add(imp);
			}
		}
		return big_and;
	}

	protected static class DebugImplication extends Implication
	{
		public Boolean evaluate(Assignment a)
		{
			boolean b_left = m_operands.get(0).evaluate(a);
			boolean b_right =  m_operands.get(1).evaluate(a);
			return !b_left || b_right;
		}
	}
}
