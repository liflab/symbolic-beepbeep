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

/**
 * Unit tests for {@link TrimModule}.
 */
public class TrimModuleTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 3);
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void testShouldBeOutput1()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 2, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 4).evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput2()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 4).evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput3()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 4).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 5).evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput4()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 10, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 4).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 5).evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput5()
	{
		int Q_in = 1, Q_out = 1;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		assertEquals(true, mod.shouldBeOutput(false, 0).evaluate(a));
	}
	
	@Test
	public void testBackPorchValues1()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set(2, 3, 1, 2, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues2()
	{
		int Q_in = 1, Q_out = 1;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0).assign(a);
		mod.getFrontPorch(0).next().set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getResetFlag().next().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(0).assign(a);
		mod.getCounter().next().set(1).assign(a);
		{
			Condition c = mod.shouldBeOutput(true, 0);
			assertEquals(true, c.evaluate(a));
		}
		{
			Condition c = mod.backPorchLength(true);
			List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch(0).next().isWellFormed());
			assertEquals(4, solutions.size()); // Values can be 0, 1, 2, 3
		}
		{
			Condition c = mod.backPorchValues(true);
			List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch(0).next().isWellFormed(), mod.backPorchLength(true));
			assertEquals(1, solutions.size());
		}
	}
	
	@Test
	public void testBackPorchValues3()
	{
		int Q_in = 2, Q_out = 2;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0).assign(a);
		mod.getFrontPorch(0).next().set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getResetFlag().next().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(0).assign(a);
		mod.getCounter().next().set(1).assign(a);
		{
			Condition c = mod.shouldBeOutput(true, 0);
			assertEquals(true, c.evaluate(a));
		}
		{
			mod.getBackPorch(0).next().set().assign(a);
			Condition c = mod.backPorchLength(true);
			assertEquals(false, c.evaluate(a));
			mod.getBackPorch(0).next().set(1).assign(a);
		}
		{
			Condition c = mod.backPorchLength(true);
			List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch(0).next().isWellFormed());
			assertEquals(1, solutions.size());
		}
		{
			Condition c = mod.backPorchValues(true);
			List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch(0).next().isWellFormed(), mod.backPorchLength(true));
			assertEquals(1, solutions.size());
		}
	}
	
	@Test
	public void testBackPorchValues4()
	{
		int Q_in = 1, Q_out = 1;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0).assign(a);
		mod.getFrontPorch(0).next().set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getResetFlag().next().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getBackPorch(0).next().set().assign(a);
		mod.getCounter().set(0).assign(a);
		mod.getCounter().next().set(1).assign(a);
		{
			Condition c1 = mod.backPorchLength(true);
			Condition c2 = mod.backPorchValues(true);
			assertEquals(false, c1.evaluate(a));
			assertEquals(false, c2.evaluate(a));
		}
	}
	
	@Test
	public void testBackPorchValues5()
	{
		int Q_in = 1, Q_out = 1;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set().assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(1).assign(a);
		mod.getBackPorch(0).set(0).assign(a);
		{
			Condition c1 = mod.shouldBeOutput(false, 0);
			assertEquals(false, c1.evaluate(a));
		}
		{
			Condition c1 = mod.numOutputs(false, 0, 1);
			assertEquals(false, c1.evaluate(a));
		}
		{
			Condition c1 = mod.numOutputs(false, 0, 0);
			assertEquals(true, c1.evaluate(a));
		}
		{
			Condition c1 = mod.backPorchLength(false);
			assertEquals(false, c1.evaluate(a));
		}
		{
			mod.getBackPorch(0).set().assign(a);
			Condition c1 = mod.backPorchLength(false);
			assertEquals(true, c1.evaluate(a));
		}
	}
	
	@Test
	public void testBackPorchValues6()
	{
		int Q_in = 2, Q_out = 2;
		TrimModule mod = new TrimModule("trim", 1, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(0).assign(a);
		mod.getBackPorch(0).set();
		// Next state
		mod.getFrontPorch(0).next().set(1).assign(a);
		mod.getCounter().next().set(1).assign(a);
		mod.getResetFlag().next().set(false).assign(a);
		//mod.getBackPorch(0).next().set(1, 2).assign(a);
		List<Assignment> solutions = s_solver.solveAll(Condition.simplify(mod.backPorchValues(true)), a, mod.getBackPorch(0).next().isWellFormed(), mod.backPorchLength(true));
		assertEquals(1, solutions.size());
		/*
		{
			Condition c1 = mod.shouldBeOutput(true, 0);
			assertEquals(true, c1.evaluate(a));
		}
		{
			Condition c1 = mod.numOutputs(true, 1, 1);
			assertEquals(true, c1.evaluate(a));
		}
		{
			Condition c1 = mod.numOutputs(true, 1, 2);
			assertEquals(false, c1.evaluate(a));
		}
		{
			Condition c1 = mod.backPorchLength(true);
			assertEquals(true, c1.evaluate(a));
		}
		{
			mod.getBackPorch(0).next().set(1, 1).assign(a);
			Condition c1 = mod.backPorchLength(true);
			assertEquals(false, c1.evaluate(a));
		}*/
	}
	
	@Test
	public void testNextCounter1()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(2, 3, 1, 2, 3).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(0).assign(a);
		assertEquals(false, c.evaluate(a));
		mod.getCounter().next().set(3).assign(a);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void testNextCounter2()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set(2, 3, 1, 2, 3).assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter3()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(1).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter4()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(2).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter5()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(3).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter7()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetFlag().set(true).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getCounter().set(3).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getCounter().next().set(1).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testPrint1()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("trim", 3, s_domNumbers, Q_in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrettyPrintStream ps = new PrettyPrintStream(baos);
		mod.print(ps);
		String out = baos.toString();
		assertNotNull(out);
		assertFalse(out.contains("ERROR")); // Indicating a problem with the transition relation
		System.out.println(out);
	}
}
