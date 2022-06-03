package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.Solver;

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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, false, false, false, false, false).assign(a);
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
		mod.getResetPorch(0).getVariable().setValues(false, true, false, false, false, false).assign(a);
		mod.getCounter().set(0).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 3).evaluate(a));
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
		mod.getResetPorch(0).getVariable().setValues(false, true, false, false, false, false).assign(a);
		mod.getCounter().set(0).assign(a);
		assertEquals(false, mod.shouldBeOutput(false, 0).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 1).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 2).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 3).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 4).evaluate(a));
		assertEquals(false, mod.shouldBeOutput(false, 5).evaluate(a));
	}
}
