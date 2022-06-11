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

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;
import ca.uqac.lif.nusmv4j.Solver;

public class CountDecimateModuleTest 
{
	protected static Domain s_domNumbers = new IntegerRange(0, 3);
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void testShouldBeOutput1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.shouldBeOutput(false, 0);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput2()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.shouldBeOutput(false, 1);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput3()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.shouldBeOutput(false, 2);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput4()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 2);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput5()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 1);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput6()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(true).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 2);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput10()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.shouldBeOutput(false, 1);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void testNumOutputs1()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		assertEquals(true, mod.numOutputs(false, 0, 0).evaluate(a));
		assertEquals(false, mod.numOutputs(false, 1, 0).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 1, 1).evaluate(a));
	}
	
	@Test
	public void testNumOutputs2()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		assertEquals(true, mod.numOutputs(false, 0, 0).evaluate(a));
		assertEquals(false, mod.numOutputs(false, 1, 0).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 1, 1).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 2, 1).evaluate(a));
		assertEquals(false, mod.numOutputs(false, 2, 2).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 3, 1).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 3, 1).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 4, 2).evaluate(a));
		assertEquals(true, mod.numOutputs(false, 5, 2).evaluate(a));
		assertEquals(false, mod.numOutputs(false, 5, 3).evaluate(a));
	}
	
	@Test
	public void testBackPorchLength1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		mod.getBackPorch(0).set(2, 2).assign(a);
		assertEquals(true, mod.backPorchLength(false).evaluate(a));
		mod.getBackPorch(0).set(2).assign(a);
		assertEquals(false, mod.backPorchLength(false).evaluate(a));
		mod.getBackPorch(0).set(2, 2, 1).assign(a);
		assertEquals(false, mod.backPorchLength(false).evaluate(a));
	}
	
	@Test
	public void testIsOutputAt1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.isOutputAt(false, 2, 0);
		assertEquals(true, c.evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 1, 0).evaluate(a));
		assertEquals(true, mod.isOutputAt(false, 2, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 3, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 4, 1).evaluate(a));
		assertEquals(true, mod.isOutputAt(false, 5, 1).evaluate(a));
	}
	
	@Test
	public void testIsOutputAt2()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		assertEquals(false, mod.isOutputAt(false, 0, 0).evaluate(a));
		assertEquals(true, mod.isOutputAt(false, 1, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 2, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 3, 1).evaluate(a));
	}
	
	@Test
	public void testIsOutputAt3()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		assertEquals(true, mod.isOutputAt(false, 0, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 1, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 1, 1).evaluate(a));
	}
	
	@Test
	public void testBackPorchValues1()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(2).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues2()
	{
		int Q_in = 3, Q_out = 3;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(2, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues3()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(2, 2).assign(a);
		assertTrue(c.evaluate(a));
		mod.getBackPorch(0).set(2, 3).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set().assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 2);
		assertTrue(c.evaluate(a));
		a.set(mod.getCounter().next(), 1);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter2()
	{
		int Q_in = 4, Q_out = 4;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 2);
		assertTrue(c.evaluate(a));
		a.set(mod.getCounter().next(), 1);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter4()
	{
		int Q_in = 4, Q_out = 4;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 2).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 0);
		assertTrue(c.evaluate(a));
		a.set(mod.getCounter().next(), 1);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testPrint1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrettyPrintStream ps = new PrettyPrintStream(baos);
		mod.print(ps);
		String out = baos.toString();
		assertNotNull(out);
		assertFalse(out.contains("ERROR")); // Indicating a problem with the transition relation
		System.out.println(out);
	}
}
