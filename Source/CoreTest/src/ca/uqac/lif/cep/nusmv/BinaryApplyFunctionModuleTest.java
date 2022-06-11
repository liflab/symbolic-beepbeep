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

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Solver;

import static org.junit.Assert.*;

import java.util.List;

public class BinaryApplyFunctionModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	protected static Solver s_solver = new BruteSolver();

	@Test
	public void testNumFronts1()
	{
		// There are 8 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 3);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a", "b", "c", "a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNumFronts2()
	{
		// There are 8 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 8);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a", "b", "c", "a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNumFronts3()
	{
		// There are 6 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 6);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a", "b", "c", "a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNumFronts4()
	{
		// There are 6 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 8);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a", "b", "c", "a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b").assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNumFronts5()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 5);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNumFronts6()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(false, 4);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch1()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		mod.getBackPorch(0).set("a", "b", "c", "a").assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch1OnlySolution()
	{
		// Checks that, for a given content of the buffers and porches, the size
		// of the back porch is uniquely determined
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		//mod.getBackPorch().set(true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
	}

	@Test
	public void testFrontsVsBackPorch3()
	{
		// There are 4 complete fronts in this test case
		int Q_in = 4, Q_b = 4, Q_out = 4;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set(true, true, true, true).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testFrontsVsBackPorch3b()
	{
		int Q_in = 2, Q_b = 2, Q_out = 2;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a", "b").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set(false, true);
		mod.getBackPorch(0).m_arrayFlags.setValues(false, true).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch3bOnlySolution()
	{
		// Checks that, for a given content of the buffers and porches, the size
		// of the back porch is uniquely determined
		int Q_in = 2, Q_b = 2, Q_out = 2;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a", "b").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		//c = Condition.simplify(c);
		//mod.getBackPorch().set(true, true, true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		Condition len = mod.getBackPorch(0).hasLength(false, 2);
		for (Assignment sol : solutions)
		{
			assertTrue(len.evaluate(sol));
		}
		assertEquals(1, solutions.size());
	}

	@Test
	public void testFrontsVsBackPorch4()
	{
		// There are 4 complete fronts in this test case
		int Q_in = 1, Q_b = 1, Q_out = 1;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set().assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch2()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		mod.getBackPorch(0).set("a", "b", "c", "a", "b").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues1()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set(true).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues1_false()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set(false).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues1OnlySolution()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		//mod.getBackPorch().set(true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.frontsVsBackPorch(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
	}

	@Test
	public void testBackPorchValues2()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("b").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch(0).set(false).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues2OnlySolution()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("b").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		//mod.getBackPorch().set(false).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.frontsVsBackPorch(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
	}

	@Test
	public void testBackPorchValues3()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBackPorch(0).set(true).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues3OnlySolution()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		//mod.getBackPorch().set(true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.frontsVsBackPorch(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
	}

	@Test
	public void testBackPorchValues4()
	{
		// There are 2 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		mod.getBackPorch(0).set(true, false).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues4_false()
	{
		// There are 2 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		mod.getBackPorch(0).set(true, true).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues4OnlySolution()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		//mod.getBackPorch().set(true, false).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.frontsVsBackPorch(false), mod.getBackPorch(0).isWellFormed());
		assertEquals(1, solutions.size());
	}

	@Test
	public void testBackPorchValues5()
	{
		// There are 2 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		mod.getBackPorch(0).set(true, false).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues6()
	{
		// There are 3 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("c").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c").assign(a);
		mod.getBackPorch(0).set(true, false, true).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues7()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues(false);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set("c", "d").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c", "b", "a").assign(a);
		mod.getBackPorch(0).set(true, false, true, false, false).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize1()
	{
		// There are 0 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set().assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize2()
	{
		// There are 3 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("c").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c").assign(a);
		mod.getBuffer(0).next().set().assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize3()
	{
		// There are 3 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("c", "a").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c").assign(a);
		mod.getBuffer(0).next().set("a").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize4()
	{
		// There are 3 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("c", "a", "b").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c").assign(a);
		mod.getBuffer(0).next().set("a", "b").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize5()
	{
		// There is 0 complete front in this test case; 1 event left in buffer 0 in next state
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set("a").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize6()
	{
		// There is 0 complete front in this test case; 1 event left in buffer 0 in next state
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set().assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize7()
	{
		// There are 2 complete fronts in this test case; 1 event left in buffer 0 in next state
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBuffer(0).next().set("a").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize8()
	{
		// There are 2 complete fronts in this test case; 1 event left in buffer 0 in next state
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBuffer(0).next().set("a", "b").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNextBufferSize9()
	{
		// The fronts that are consumed are a-a, b-b. What remains in the buffer are c +
		// what is in the front porch (c b).
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set("c", "b").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "b").assign(a);
		mod.getBuffer(0).next().set("c", "b").assign(a);
		Condition c = mod.nextBufferSize(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNextBufferValues1()
	{
		// No input event: buffer remains the same
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set("a").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));	
	}

	@Test
	public void testNextBufferValues1_false()
	{
		// No input event: buffer remains the same
		// nf=0, nq=1
		int Q_in = 1, Q_b = 1, Q_out = 1;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set("b").assign(a);
		Condition c = mod.nextBufferValues(0);
		Condition c2 = Condition.simplify(c);
		assertNotNull(c2);
		assertFalse(c.evaluate(a));	
	}

	@Test
	public void testNextBufferValues2()
	{
		// No front: buffer remains the same
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set("a", "b", "c").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));	
	}

	@Test
	public void testNextBufferValues2_false()
	{
		// No front: buffer remains the same
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBuffer(0).next().set("a", "c", "c").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));	
	}

	@Test
	public void testNextBufferValues3()
	{
		// One front: buffer contents shifted
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBuffer(0).next().set("b", "c").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testNextBufferValues3_false()
	{
		// One front: buffer contents shifted
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBuffer(0).next().set("b").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNextBufferValues3_false2()
	{
		// One front: buffer contents shifted
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBuffer(0).next().set("b", "a").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testNextBufferValues4()
	{
		// The fronts that are consumed are a-a, b-b. What remains in the buffer are c +
		// what is in the front porch (c b).
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set("c", "b").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "b").assign(a);
		mod.getBuffer(0).next().set("c", "c", "b").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertTrue(c.evaluate(a));	
	}

	@Test
	public void testNextBufferValues4_false()
	{
		// The fronts that are consumed are a-a, b-b. What remains in the buffer are c +
		// what is in the front porch (c b).
		int Q_in = 3, Q_b = 3, Q_out = 3;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set("c", "b").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "b").assign(a);
		mod.getBuffer(0).next().set("c", "b").assign(a);
		Condition c = mod.nextBufferValues(0);
		assertNotNull(c);
		assertFalse(c.evaluate(a));	
	}

}
