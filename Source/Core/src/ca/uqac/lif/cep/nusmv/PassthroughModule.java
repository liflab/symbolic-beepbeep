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
 * Processor module that directly relays its input events to its output. 
 */
public class PassthroughModule extends UnaryApplyFunctionModule
{
	public PassthroughModule(String name, Domain d, int Q_in, int Q_out)
	{
		super(name, new IdentityFunction(d), Q_in, Q_out);
	}
	
	@Override
	public PassthroughModule duplicate() 
	{
		return new PassthroughModule(m_name, m_function.getInputDomain(), getFrontPorch(0).getSize(), getBackPorch(0).getSize());
	}

}
