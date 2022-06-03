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

public class CumulateModuleTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 4);
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void testOutputValues1()
	{
		int Q_in = 1, Q_out = 1;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false).assign(a);
		mod.getBackPorch().set(1).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues2()
	{
		int Q_in = 2, Q_out = 2;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(1, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues3()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(1, 2, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues5()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(1, 1, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues6()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		a.set(mod.getCounter(), 2);
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(3, 1, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues7()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		a.set(mod.getCounter(), 2);
		Condition c = mod.backPorchValues(false);
		mod.getBackPorch().set(3, 4, 2).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues4()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(1, 1, 3).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues8()
	{
		// Test with another function
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Maximum(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(3, 1, 4).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(3, 3, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testOutputValues9()
	{
		// Test with another function
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Maximum(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(3, 1, 4).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.backPorchValues(false);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		mod.getBackPorch().set(3, 1, 4).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter1()
	{
		int Q_in = 1, Q_out = 1;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false).assign(a);
		mod.getBackPorch().set(1).assign(a);
		a.set(mod.getCounter(), s_domNumbers.getDefaultValue());
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 1);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter2()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false).assign(a);
		mod.getBackPorch().set(1, 2, 4).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 4);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter3()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, false, false).assign(a);
		mod.getBackPorch().set(1, 2, 4).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		a.set(mod.getCounter().next(), 3);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter4()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, false).assign(a);
		mod.getBackPorch().set(1, 1, 3).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 3);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testNextCounter5()
	{
		int Q_in = 3, Q_out = 3;
		CumulateModule mod = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), s_domNumbers, Q_in, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 1, 2).assign(a);
		mod.getResetPorch(0).getVariable().setValues(false, true, true).assign(a);
		mod.getBackPorch().set(1, 2, 2).assign(a);
		a.set(mod.getCounter(), 1);
		Condition c = mod.nextCounter();
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		a.set(mod.getCounter().next(), 2);
		assertTrue(c.evaluate(a));
	}
}