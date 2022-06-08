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

import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Implication;

public abstract class BinaryModule extends ProcessorModule
{
	public BinaryModule(String name, Domain d_in1, Domain d_in2, Domain d_out, int Q_in, int Q_b, int Q_out)
	{
		super(name, 2, new Domain[] {d_in1, d_in2}, d_out, Q_in, Q_b, Q_out);
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
			and.add(minTotalPipe(false, 1, n));
			or.add(and);
		}
		{
			Conjunction and = new Conjunction();
			and.add(minTotalPipe(false, 0, n));
			and.add(hasTotalPipe(1, n));
			or.add(and);
		}
		return or;
	}

	/**
	 * Generates the condition stipulating that position m1 in sigma1 and
	 * position m2 in sigma2 make the n-th input front of the processor's
	 * input.
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the input front
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isNthFront(QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		Conjunction left = new Conjunction();
		left.add(at(sigma1, 0, m1, n));
		left.add(at(sigma2, 1, m2, n));
		return left;
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
				Implication imp = new Implication();
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
							imp.add(isFrontToOutput(sigma1, m1, sigma2, m2, n));
							imp.add(getOutputCondition(sigma1, m1, sigma2, m2, n));
							and.add(imp);
						}
					}
				}
			}
		}
		return and;
	}

	/**
	 * Generates the condition stipulating that position m1 in sigma1 and
	 * position m2 in sigma2 is the input front composing the n-th output
	 * value.
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the output value
	 * @return The condition
	 */
	public abstract Condition isFrontToOutput(QueueType sigma1, int m1, QueueType sigma2, int m2, int n);

	/**
	 * Generates the condition describing the value of the n-th output element
	 * based on the 
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the output value
	 * @return The condition
	 */
	public abstract Condition getOutputCondition(QueueType sigma1, int m1, QueueType sigma2, int m2, int n);
}
