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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, true, false, false, false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 2);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput7()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 2, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, true, false, false, false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 3);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput8()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, true, false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.shouldBeOutput(false, 2);
		assertEquals(true, c.evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(true, mod.shouldBeOutput(false, 4).evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput9()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(2, 3).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
		mod.getCounter().set(0).assign(a);
		Condition c = mod.shouldBeOutput(false, 1);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void testShouldBeOutput10()
	{
		int Q_in = 2, Q_out = 2;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(2, 3).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, true, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, true, false).assign(a);
		mod.getCounter().set(2).assign(a);
		mod.getBackPorch().set(2, 2).assign(a);
		assertEquals(true, mod.backPorchLength(false).evaluate(a));
		mod.getBackPorch().set(2).assign(a);
		assertEquals(false, mod.backPorchLength(false).evaluate(a));
		mod.getBackPorch().set(2, 2, 1).assign(a);
		assertEquals(false, mod.backPorchLength(false).evaluate(a));
	}
	
	@Test
	public void testIsOutputAt1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, true, false).assign(a);
		mod.getCounter().set(1).assign(a);
		Condition c = mod.isOutputAt(false, 2, 0);
		assertEquals(true, c.evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 1, 0).evaluate(a));
		assertEquals(true, mod.isOutputAt(false, 2, 0).evaluate(a));
		assertEquals(false, mod.isOutputAt(false, 3, 0).evaluate(a));
		assertEquals(true, mod.isOutputAt(false, 4, 1).evaluate(a));
	}
	
	@Test
	public void testIsOutputAt2()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, true, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch().isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(2).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues2()
	{
		int Q_in = 3, Q_out = 3;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch().isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(2, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues3()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch().isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(2, 2).assign(a);
		assertTrue(c.evaluate(a));
		mod.getBackPorch().set(2, 3).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testBackPorchValues4()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3, 3, 2, 1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, true, false, false, false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a, mod.backPorchLength(false), mod.getBackPorch().isWellFormed());
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(2, 3, 1).assign(a);
		assertTrue(c.evaluate(a));
		mod.getBackPorch().set(2, 1, 1).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter1()
	{
		int Q_in = 6, Q_out = 6;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set().assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false).assign(a);
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
	public void testNextCounter3()
	{
		int Q_in = 4, Q_out = 4;
		CountDecimateModule mod = new CountDecimateModule("dec", 3, s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2, 3).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(true, false, false, false).assign(a);
		mod.getCounter().set(2).assign(a);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 1);
		assertTrue(c.evaluate(a));
		a.set(mod.getCounter().next(), 0);
		assertFalse(c.evaluate(a));
	}
}
