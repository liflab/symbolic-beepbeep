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

import ca.uqac.lif.nusmv4j.Domain;

/**
 * Processor module that transforms each input event into the same output
 * event. 
 */
public class TurnIntoModule extends UnaryApplyFunctionModule
{
	public TurnIntoModule(String name, Domain in_d, Domain out_d, Object value, int Q_in, int Q_out)
	{
		super(name, new TurnIntoFunction(in_d, out_d, value), Q_in, Q_out);
	}
	
	@Override
	public TurnIntoModule duplicate() 
	{
		return new TurnIntoModule(m_name, m_function.getInputDomain(), m_function.getOutputDomain(), ((TurnIntoFunction) m_function).getValue(), getFrontPorch(0).getSize(), getBackPorch(0).getSize());
	}
	
	@Override
	public String toString()
	{
		return "Turn into " + ((TurnIntoFunction) m_function).getValue();
	}
}
