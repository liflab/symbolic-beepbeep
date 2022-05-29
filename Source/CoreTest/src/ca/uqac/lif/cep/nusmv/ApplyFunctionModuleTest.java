package ca.uqac.lif.cep.nusmv;

import org.junit.Test;

import static org.junit.Assert.*;

import ca.uqac.lif.symbolif.Assignment;
import ca.uqac.lif.symbolif.BooleanDomain;
import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Domain;

public class ApplyFunctionModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	protected static Domain s_domBooleans = BooleanDomain.instance;

	@Test
	public void testNumFronts1()
	{
		// There are 8 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(3);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(8);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(6);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(8);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(5);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.numFronts(4);
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
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		mod.getBackPorch().set("a", "b", "c", "a").assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch2()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		ApplyFunctionModule mod = new ApplyFunctionModule("f", 2, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set("a", "b", "c", "a").assign(a);
		mod.getBackPorch().set("a", "b", "c", "a", "b").assign(a);
		assertTrue(c.evaluate(a));
	}
}
