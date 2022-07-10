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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;

import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.BUFFER;
import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.PORCH;

public class FilterModuleTest 
{
	@Before
	public void setup()
	{
		// For debugging
		Conjunction.s_merge = false;
		Disjunction.s_merge = false;
	}
	
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	protected static BruteSolver s_solver = new BruteSolver();

	@Test
	public void testHasNTrueBuffer1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a);
		mod.getFrontPorch(1).set().assign(a);
		assertEquals(false, mod.hasNTrue(false, BUFFER, 1, 0, 0).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, BUFFER, 1, 0, 1).evaluate(a));
		assertEquals(true, mod.hasNTrueQueue(false, BUFFER, 1, 1, 1).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, BUFFER, 1, 2, 2).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, BUFFER, 1, 3, 3).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, BUFFER, 1, 4, 3).evaluate(a));
		assertEquals(false, mod.hasNTrue(false, BUFFER, 1, 4, 0).evaluate(a));
	}
	
	/*@Test
	public void testHasNTrueBuffer2()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set().assign(a);
		Condition c = mod.hasNTrue(false, BUFFER, 1, 4, 1);
		assertEquals(true, c.evaluate(a));
	}*/

	@Test
	public void testHasNTruePorch1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a); // 3 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(false, mod.hasNTrue(false, PORCH, 1, 0, 3).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, PORCH, 1, 0, 4).evaluate(a));
		assertEquals(true, mod.hasNTrueQueue(false, PORCH, 1, 1, 1).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, PORCH, 1, 1, 4).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, PORCH, 1, 2, 5).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, PORCH, 1, 3, 6).evaluate(a));
		assertEquals(false, mod.hasNTrue(false, PORCH, 1, 4, 5).evaluate(a));
		assertEquals(false, mod.hasNTrue(false, PORCH, 1, 4, 5).evaluate(a));
		assertEquals(true, mod.hasNTrue(false, PORCH, 1, 4, 6).evaluate(a));
	}
	
	@Test
	public void testHasNTrueQueuePorch2()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a); // 3 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		Condition c = mod.hasNTrueQueue(false, PORCH, 1, 0, 1);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void testHasNTruePorch2()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a); // 3 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		Condition c = mod.hasNTrue(false, PORCH, 1, 0, 4);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void testQueueTotalNTrue1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, true, true).assign(a); // 3 true
		Condition c = mod.totalNTrueQueue(false, BUFFER, 1, 3);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void testQueueTotalNTrue2()
	{
		int Q_in = 2, Q_b = 2, Q_out = 2;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false).assign(a);
		Condition c = mod.totalNTrueQueue(false, BUFFER, 1, 1);
		assertEquals(true, c.evaluate(a));
	}
	
	/*
	@Test
	public void testHasNTrueTotal1()
	{
		int Q_in = 1, Q_b = 1, Q_out = 1;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set(true).assign(a);
		assertEquals(false, mod.hasNTrueTotal(false, 1, 0).evaluate(a));
		assertEquals(true, mod.hasNTrueTotal(false, 1, 1).evaluate(a));
	}
	*/

	@Test
	public void testIsNthTrue1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a); // 3 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(true, mod.isNthTrue(false, BUFFER, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 1, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 1, 2).evaluate(a));
		assertEquals(true, mod.isNthTrue(false, BUFFER, 1, 2, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 2, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 2, 3).evaluate(a));
		assertEquals(true, mod.isNthTrue(false, BUFFER, 1, 3, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 3, 4).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 4, 3).evaluate(a));
		Condition c = mod.isNthTrue(false, PORCH, 1, 0, 4);
		assertEquals(true, c.evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 0, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 0, 5).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 1, 4).evaluate(a));
		assertEquals(true, mod.isNthTrue(false, PORCH, 1, 2, 5).evaluate(a));
	}

	@Test
	public void testIsNthTrue2()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(false, false, false, false, false).assign(a); // 0 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 1, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 1, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 2, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 2, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 2, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 3, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 3, 4).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, BUFFER, 1, 4, 3).evaluate(a));
		assertEquals(true, mod.isNthTrue(false, PORCH, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 0, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 0, 0).evaluate(a));
		assertEquals(false, mod.isNthTrue(false, PORCH, 1, 1, 2).evaluate(a));
		assertEquals(true, mod.isNthTrue(false, PORCH, 1, 2, 2).evaluate(a));
	}
	
	@Test
	public void testIsNthTrue3()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		Condition c = mod.isNthTrue(false, PORCH, 1, 0, 2);
		assertEquals(true, c.evaluate(a));
	}

	@Test
	public void testIsFrontToOutput1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		mod.getResetFlag().set(false).assign(a);
		assertEquals(true, mod.isFrontToOutput(false, BUFFER, 0, BUFFER, 0, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(false, BUFFER, 0, BUFFER, 1, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(false, BUFFER, 1, BUFFER, 0, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(false, BUFFER, 0, BUFFER, 0, 1).evaluate(a));
		assertEquals(true, mod.isFrontToOutput(false, BUFFER, 3, PORCH, 0, 1).evaluate(a));
		//assertEquals(true, mod.isFrontToOutput(false, PORCH, 0, PORCH, 1, 2).evaluate(a));
	}
	
	@Test
	public void testNumTrueFronts1()
	{
		// There are 5 complete fronts in this test case, 3 true fronts
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		mod.getResetFlag().set(false).assign(a);
		Condition c = mod.numTrueFronts(false, 1, 0);
		assertEquals(false, c.evaluate(a));
		assertEquals(false, mod.numTrueFronts(false, 1, 1).evaluate(a));
		assertEquals(false, mod.numTrueFronts(false, 1, 2).evaluate(a));
		Condition cond = mod.numTrueFronts(false, 1, 3);
		assertEquals(true, cond.evaluate(a));
		assertEquals(false, mod.numTrueFronts(false, 1, 4).evaluate(a));
	}
	
	@Test
	public void testNumTrueFronts2()
	{
		// There are 5 complete fronts in this test case, 4 true fronts
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, true).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		mod.getResetFlag().set(false).assign(a);
		Condition c = mod.numTrueFronts(false, 1, 0);
		assertEquals(false, c.evaluate(a));
		assertEquals(false, mod.numTrueFronts(false, 1, 1).evaluate(a));
		assertEquals(false, mod.numTrueFronts(false, 1, 2).evaluate(a));
		{
			Condition cond = mod.totalNTrueQueue(false, BUFFER, 1, 1);
			assertEquals(false, cond.evaluate(a));
		}
		Condition cond = mod.numTrueFronts(false, 1, 3);
		assertEquals(false, cond.evaluate(a));
		assertEquals(true, mod.numTrueFronts(false, 1, 4).evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch1()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		mod.getResetFlag().set(false).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set("a", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch2()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, true).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set("a", "c", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch3()
	{
		// There is 1 complete front in this test case
		int Q_in = 1, Q_b = 1, Q_out = 1;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set(true).assign(a);
		/*{
			Condition ntrue = mod.numTrueFronts(false, 1, 1);
			assertEquals(true, ntrue.evaluate(a));
		}*/
		{
			Condition ntrue = mod.numTrueFronts(false, 1, 0);
			assertEquals(false, ntrue.evaluate(a));
		}
		mod.getBackPorch(0).set("a").assign(a);
		assertTrue(c.evaluate(a));
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set("a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch4()
	{
		// There is 1 complete front in this test case
		int Q_in = 1, Q_b = 1, Q_out = 1;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(true);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).next().set().assign(a);
		mod.getFrontPorch(0).next().set("a").assign(a);
		mod.getBuffer(1).next().set().assign(a);
		mod.getFrontPorch(1).next().set(true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).next().set("a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testGetInit1()
	{
		int Q_in = 2, Q_b = 2, Q_out = 2;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.getInit();
		assertNotNull(c);
		Assignment a = new Assignment();
		// Current
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set(false).assign(a);
		mod.getResetFlag().set(false).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, 20);
		assertEquals(1, solutions.size());
	}

	@Test
	public void testGetTrans1()
	{
		int Q_in = 2, Q_b = 2, Q_out = 2;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.getTrans();
		assertNotNull(c);
		Assignment a = new Assignment();
		// Current
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set(false).assign(a);
		mod.getBackPorch(0).set().assign(a);
		mod.getResetFlag().set(false).assign(a);
		// Next
		//mod.getBuffer(0).next().set().assign(a);
		mod.getFrontPorch(0).next().set("b").assign(a);
		//mod.getBuffer(1).next().set().assign(a);
		mod.getFrontPorch(1).next().set(true).assign(a);
		mod.getBackPorch(0).next().set("b").assign(a);
		mod.getResetFlag().next().set(false).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, 20);
		assertEquals(1, solutions.size());
	}

	@Test(timeout = 1000)
	public void testBackPorchValues1()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, true).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch(0).isWellFormed(false), mod.frontsVsBackPorch(false));
		assertEquals(1, solutions.size());
		mod.getBackPorch(0).set("a", "c", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}
}
