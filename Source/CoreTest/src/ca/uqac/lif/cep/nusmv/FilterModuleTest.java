package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Solver;

import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.BUFFER;
import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.PORCH;

public class FilterModuleTest 
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void testHasNTrueBuffer1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a);
		mod.getFrontPorch(1).set().assign(a);
		assertEquals(false, mod.hasNTrue(mod.getBuffer(1), 0, 0).evaluate(a));
		assertEquals(true, mod.hasNTrue(mod.getBuffer(1), 0, 1).evaluate(a));
		assertEquals(true, mod.hasNTrue(mod.getBuffer(1), 1, 1).evaluate(a));
		assertEquals(true, mod.hasNTrue(mod.getBuffer(1), 2, 2).evaluate(a));
		assertEquals(true, mod.hasNTrue(mod.getBuffer(1), 3, 3).evaluate(a));
		assertEquals(true, mod.hasNTrue(mod.getBuffer(1), 4, 3).evaluate(a));
	}
		
	@Test
	public void testHasNTruePorch1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a);
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(false, mod.hasNTruePorch(1, 0, 3).evaluate(a));
		assertEquals(true, mod.hasNTruePorch(1, 0, 4).evaluate(a));
		assertEquals(true, mod.hasNTruePorch(1, 1, 4).evaluate(a));
		assertEquals(true, mod.hasNTruePorch(1, 2, 5).evaluate(a));
		assertEquals(true, mod.hasNTruePorch(1, 3, 6).evaluate(a));
		assertEquals(false, mod.hasNTruePorch(1, 4, 5).evaluate(a));
		assertEquals(true, mod.hasNTruePorch(1, 4, 6).evaluate(a));
	}
	
	@Test
	public void testIsNthTrue1()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(true, false, true, true, false).assign(a); // 3 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(true, mod.isNthTrue(BUFFER, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 1, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 1, 2).evaluate(a));
		assertEquals(true, mod.isNthTrue(BUFFER, 1, 2, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 2, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 2, 3).evaluate(a));
		assertEquals(true, mod.isNthTrue(BUFFER, 1, 3, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 3, 4).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 4, 3).evaluate(a));
		assertEquals(true, mod.isNthTrue(PORCH, 1, 0, 4).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 0, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 0, 5).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 1, 4).evaluate(a));
		assertEquals(true, mod.isNthTrue(PORCH, 1, 2, 5).evaluate(a));
	}
	
	@Test
	public void testIsNthTrue2()
	{
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getBuffer(1).set(false, false, false, false, false).assign(a); // 0 true
		mod.getFrontPorch(1).set(true, false, true, true, false).assign(a);
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 1, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 1, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 2, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 2, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 2, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 3, 3).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 3, 4).evaluate(a));
		assertEquals(false, mod.isNthTrue(BUFFER, 1, 4, 3).evaluate(a));
		assertEquals(true, mod.isNthTrue(PORCH, 1, 0, 1).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 0, 2).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 0, 0).evaluate(a));
		assertEquals(false, mod.isNthTrue(PORCH, 1, 1, 2).evaluate(a));
		assertEquals(true, mod.isNthTrue(PORCH, 1, 2, 2).evaluate(a));
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
		assertEquals(true, mod.isFrontToOutput(BUFFER, 0, BUFFER, 0, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(BUFFER, 0, BUFFER, 1, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(BUFFER, 1, BUFFER, 0, 0).evaluate(a));
		assertEquals(false, mod.isFrontToOutput(BUFFER, 0, BUFFER, 0, 1).evaluate(a));
		assertEquals(true, mod.isFrontToOutput(BUFFER, 3, PORCH, 0, 1).evaluate(a));
		assertEquals(true, mod.isFrontToOutput(PORCH, 0, PORCH, 1, 2).evaluate(a));
	}
	
	@Test
	public void testFrontsVsBackPorch1()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, false).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set("a", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testFrontsVsBackPorch2()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, true).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set("a", "c", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues1()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		FilterModule mod = new FilterModule("f", s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set(true, false, true).assign(a);
		mod.getFrontPorch(1).set(true, true).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.getBackPorch().isWellFormed(), mod.frontsVsBackPorch());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set("a", "c", "a", "a").assign(a);
		assertTrue(c.evaluate(a));
	}
}
