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

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
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
		TrimModule mod = new TrimModule("dec", 2, s_domNumbers, Q_in, Q_out);
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
		TrimModule mod = new TrimModule("dec", 3, s_domNumbers, Q_in, Q_out);
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
		TrimModule mod = new TrimModule("dec", 3, s_domNumbers, Q_in, Q_out);
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
		TrimModule mod = new TrimModule("dec", 10, s_domNumbers, Q_in, Q_out);
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
	public void testBackPorchValues1()
	{
		int Q_in = 6, Q_out = 6;
		TrimModule mod = new TrimModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 1, 2, 3).assign(a);
		mod.getResetFlag().set(false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch().isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(2, 3, 1, 2, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
}
