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

/**
 * Unary processor applying a function to each event of the input porch.
 */
public abstract class UnaryApplyFunctionModule extends UnaryProcessorModule
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
	protected void addToInit(Conjunction c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addToTrans(Conjunction c) {
		// TODO Auto-generated method stub
		
	}

}
