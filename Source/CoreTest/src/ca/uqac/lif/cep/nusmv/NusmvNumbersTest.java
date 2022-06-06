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

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.cep.nusmv.NusmvNumbers.Maximum;
import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ScalarVariable;

public class NusmvNumbersTest 
{
	protected static Domain s_domNumbers = new IntegerRange(0, 4);
	
	@Test
	public void testMaximum1()
	{
		ScalarVariable x = new ScalarVariable("x", s_domNumbers);
		ScalarVariable y = new ScalarVariable("y", s_domNumbers);
		ScalarVariable z = new ScalarVariable("z", s_domNumbers);
		Maximum f = new Maximum(s_domNumbers);
		Condition c = f.getCondition(x, y, z);
		Assignment a = new Assignment();
		a.set(x, 0).set(y, 1).set(z, 1);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testMaximum2()
	{
		ScalarVariable x = new ScalarVariable("x", s_domNumbers);
		ScalarVariable y = new ScalarVariable("y", s_domNumbers);
		ScalarVariable z = new ScalarVariable("z", s_domNumbers);
		Maximum f = new Maximum(s_domNumbers);
		Condition c = f.getCondition(x, y, z);
		Assignment a = new Assignment();
		a.set(x, 0).set(y, 1).set(z, 0);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testMaximum3()
	{
		ScalarVariable x = new ScalarVariable("x", s_domNumbers);
		ScalarVariable y = new ScalarVariable("y", s_domNumbers);
		ScalarVariable z = new ScalarVariable("z", s_domNumbers);
		Maximum f = new Maximum(s_domNumbers);
		Condition c = f.getCondition(x, y, z);
		Assignment a = new Assignment();
		a.set(x, 1).set(y, 0).set(z, 1);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testMaximum4()
	{
		ScalarVariable x = new ScalarVariable("x", s_domNumbers);
		ScalarVariable y = new ScalarVariable("y", s_domNumbers);
		ScalarVariable z = new ScalarVariable("z", s_domNumbers);
		Maximum f = new Maximum(s_domNumbers);
		Condition c = f.getCondition(x, y, z);
		Assignment a = new Assignment();
		a.set(x, 0).set(y, 0).set(z, 0);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testMaximum5()
	{
		ScalarVariable x = new ScalarVariable("x", s_domNumbers);
		ScalarVariable y = new ScalarVariable("y", s_domNumbers);
		ScalarVariable z = new ScalarVariable("z", s_domNumbers);
		Maximum f = new Maximum(s_domNumbers);
		Condition c = f.getCondition(x, y, z);
		Assignment a = new Assignment();
		a.set(x, 0).set(y, 1).set(z, 2);
		assertFalse(c.evaluate(a));
	}
}
