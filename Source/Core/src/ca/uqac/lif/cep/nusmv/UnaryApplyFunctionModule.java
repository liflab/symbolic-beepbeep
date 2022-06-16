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

import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Implication;

/**
 * Unary processor applying a function to each event of the input porch.
 */
public class UnaryApplyFunctionModule extends UnaryProcessorModule
{
	/**
	 * The unary function to apply on each input event.
	 */
	/*@ non_null @*/ protected final UnaryFunctionCall m_function;
	
	public UnaryApplyFunctionModule(String name, UnaryFunctionCall f, int Q_in, int Q_out) 
	{
		super(name, f.getInputDomain(), f.getOutputDomain(), Q_in, 0, Q_out);
		m_function = f;
	}

	@Override
	protected void addToInit(Conjunction c)
	{
		c.add(new MatchingPorches(false));
	}

	@Override
	protected void addToTrans(Conjunction c)
	{
		c.add(new MatchingPorches(true));
	}
	
	protected class MatchingPorches extends Conjunction
	{
		protected final boolean m_next;
		
		public MatchingPorches(boolean next)
		{
			super();
			m_next = next;
			ProcessorQueue front = getFrontPorch(0);
			ProcessorQueue back = getBackPorch(0);
			for (int len = 0; len <= front.getSize(); len++)
			{
				Implication imp = new Implication();
				imp.add(front.hasLength(next, len));
				Conjunction and = new Conjunction();
				and.add(back.hasLength(next, len));
				for (int i = 0; i < len; i++)
				{
					and.add(m_function.getCondition(front.valueAt(next, i), back.valueAt(next, i)));
				}
				imp.add(and);
				add(imp);
			}
		}
		
		@Override
		public String toString()
		{
			return "![0] = f(?[0])";
		}
	}

	@Override
	public ProcessorModule duplicate()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
