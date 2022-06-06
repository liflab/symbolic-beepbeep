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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.ScalarVariable;

public class FunctionEqualsTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void test1()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "a").set(z, true);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void test2()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "a").set(z, false);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test3()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "b").set(z, true);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test4()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "b").set(z, false);
		assertEquals(true, c.evaluate(a));
	}
}
