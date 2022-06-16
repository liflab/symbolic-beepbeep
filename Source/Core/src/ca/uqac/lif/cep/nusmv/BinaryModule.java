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
		super(name, 2, new Domain[] {d_in1, d_in2}, 1, new Domain[] {d_out}, true, Q_in, Q_b, Q_out);
	}

	/**
	 * Generates the condition stipulating that internal buffers are empty.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition emptyBuffers(boolean next)
	{
		return new EmptyBuffers(next);
	}
	
	public class EmptyBuffers extends Conjunction
	{
		protected final boolean m_next;
		
		public EmptyBuffers(boolean next)
		{
			super();
			m_next = next;
			add(getBuffer(0).hasLength(next, 0));
			add(getBuffer(1).hasLength(next, 0));
		}
		
		@Override
		public String toString()
		{
			return "EmptyBuffers" + (m_next ? "'" : "");
		}
	}
	
	@Override
	protected void addToInit(Conjunction c)
	{
		c.add(emptyBuffers(false));
		c.add(backPorchValues(false));
	}
	
	@Override
	protected void addToTrans(Conjunction c)
	{
		// Buffers size = 0 if reset, otherwise follow definition
		{
			Implication imp = new Implication();
			imp.add(new IsReset(true));
			imp.add(emptyBuffers(true));
			c.add(imp);
		}
		{
			Implication imp = new Implication();
			imp.add(new NoReset(true));
			Conjunction and = new Conjunction();
			and.add(nextBufferSizes());
			imp.add(and);
			c.add(imp);
		}
		// Conditions that dictate the content of each buffer in the next state
		c.add(nextBufferValues(0));
		c.add(nextBufferValues(1));
		// Conditions that dictate the content of the back porch in the next state
		c.add(backPorchValues(true));
	}

	/**
	 * Generates the condition asserting that the input pipes contain exactly
	 * n complete event fronts.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param n The number of event fronts
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition numFronts(boolean next, int n)
	{
		Disjunction or = new Disjunction();
		{
			Conjunction and = new Conjunction();
			and.add(hasTotalPipe(next, 0, n));
			and.add(minTotalPipe(next, 1, n));
			or.add(and);
		}
		{
			Conjunction and = new Conjunction();
			and.add(minTotalPipe(next, 0, n));
			and.add(hasTotalPipe(next, 1, n));
			or.add(and);
		}
		return or;
	}

	/**
	 * Generates the condition stipulating that position m1 in sigma1 and
	 * position m2 in sigma2 make the n-th input front of the processor's
	 * input.
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the input front
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition isNthFront(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n)
	{
		Conjunction left = new Conjunction();
		left.add(at(next, sigma1, 0, m1, n));
		left.add(at(next, sigma2, 1, m2, n));
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
		int Q_out = getBackPorch(0).getSize();
		Conjunction big_and = new Conjunction();
		for (int nf = 0; nf <= Q_out; nf++)
		{
			for (int nq = nf; nq <= Q_in + Q_b; nq++)
			{
				if (nq - nf > getBuffer(pipe_index).getSize())
				{
					continue;
				}
				Implication imp = new Implication();
				{
					Conjunction left = new Conjunction();
					left.add(numFronts(false, nf));
					left.add(hasTotalPipe(false, pipe_index, nq));
					imp.add(left);
				}
				imp.add(getBuffer(pipe_index).hasLength(true, nq - nf));
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
		int Q_out = getBackPorch(0).getSize();
		Conjunction big_and = new Conjunction();
		for (int nf = 0; nf <= Q_out; nf++)
		{
			for (int nq = nf; nq <= Q_in + Q_b; nq++)
			{
				Implication imp = new Implication();
				{
					Conjunction left = new Conjunction();
					left.add(numFronts(false, nf));
					left.add(hasTotalPipe(false, pipe_index, nq));
					imp.add(left);
				}
				{
					Conjunction in_and = new Conjunction();
					for (int i = 0; i <= nq - nf - 1 && i < getBuffer(pipe_index).getSize(); i++)
					{
						for (QueueType sigma : new QueueType[] {QueueType.PORCH, QueueType.BUFFER})
						{
							for (int j = 0; j <= length(sigma, pipe_index) - 1; j++)
							{
								Implication in_imp = new Implication();
								in_imp.add(at(false, sigma, pipe_index, j, nf + i));
								in_imp.add(new Equality(
										valueAt(false, sigma, pipe_index, j), // buffer/porch at position j
										getBuffer(pipe_index).valueAt(true, i) // next buffer at position i
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
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition backPorchValues(boolean next)
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
							imp.add(isFrontToOutput(next, sigma1, m1, sigma2, m2, n));
							imp.add(getOutputCondition(next, sigma1, m1, sigma2, m2, n));
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
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the output value
	 * @return The condition
	 */
	public abstract Condition isFrontToOutput(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n);

	/**
	 * Generates the condition describing the value of the n-th output element
	 * based on the 
	 * @param next A flag indicating if the condition applies to the
	 * current state or the next state
	 * @param sigma1 A variable indicating if the first pipe designates the
	 * front porch or the internal buffer
	 * @param m1 The position in the corresponding queue
	 * @param sigma2 A variable indicating if the second pipe designates the
	 * front porch or the internal buffer
	 * @param m2 The position in the corresponding queue
	 * @param n The index of the output value
	 * @return The condition
	 */
	public abstract Condition getOutputCondition(boolean next, QueueType sigma1, int m1, QueueType sigma2, int m2, int n);
}
