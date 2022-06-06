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

import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ScalarVariable;

public class UnaryMetricOperator extends UnaryProcessorModule
{
	/**
	 * The internal variable keeping track of the current cumulative count.
	 */
	/*@ non_null @*/ protected final ScalarVariable m_counter;
	
	/**
	 * The interval after which the processor must emit a verdict
	 */
	protected final int m_interval;
	
	public UnaryMetricOperator(String name, int interval, int Q_in, int Q_out)
	{
		super(name, BooleanDomain.instance, BooleanDomain.instance, Q_in, 0, Q_out);
		m_interval = interval;
		m_counter = new ScalarVariable("cnt", new IntegerRange(0, interval - 1));
	}
	
	@Override
	public UnaryMetricOperator duplicate()
	{
		// TODO
		return null;
	}
	
}
