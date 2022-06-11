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

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.Solver;

public class CumulateModuleTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 4);
	
	protected static Solver s_solver = new BruteSolver();
	
	public interface NextCounterValue { };
	
	@Test
	public void testOutputValues1()
	{
		int Q_in = 1, Q_out = 1;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(1).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues2()
	{
		int Q_in = 2, Q_out = 2;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(1, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues3()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(1, 2, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues6()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		a.set(mod.getCounter(), 2);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(3, 4, 1).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues7()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(true).assign(a);
		a.set(mod.getCounter(), 2);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(1, 2, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues8()
	{
		// Test with another function
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("max", new NusmvNumbers.Maximum(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(3, 1, 4).assign(a);
		mod.getResetFlag().set(false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(3, 3, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues9()
	{
		// Test with another function
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("max", new NusmvNumbers.Maximum(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(3, 1, 4).assign(a);
		mod.getResetFlag().set(true).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(3, 3, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	@Category(NextCounterValue.class)
	public void testNextCounter1()
	{
		int Q_in = 1, Q_out = 1;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(1).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 1);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	@Category(NextCounterValue.class)
	public void testNextCounter2()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(1, 2, 4).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 4);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	@Category(NextCounterValue.class)
	public void testNextCounter3()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(1, 2, 4).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		a.set(mod.getCounter().next(), 3);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	@Category(NextCounterValue.class)
	public void testNextCounter4()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(true).assign(a);
		mod.getBackPorch(0).set(1, 1, 3).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 3);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	@Category(NextCounterValue.class)
	public void testNextCounter5()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetFlag().set(true).assign(a);
		mod.getBackPorch(0).set(1, 2, 4).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 4);
		assertTrue(c.evaluate(a));
	}
}
