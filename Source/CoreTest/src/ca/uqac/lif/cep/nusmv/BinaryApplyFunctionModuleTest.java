package ca.uqac.lif.cep.nusmv;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;

import static org.junit.Assert.*;

public class BinaryApplyFunctionModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	protected static Domain s_domBooleans = BooleanDomain.instance;

	@Test
	public void testNumFronts1()
	{
		// There are 8 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
	public void testFrontsVsBackPorch1OnlySolution()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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
	public void testFrontsVsBackPorch3()
	{
		// There are 4 complete fronts in this test case
		int Q_in = 4, Q_b = 4, Q_out = 4;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a", "b", "c", "a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch().set(true, true, true, true).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch4()
	{
		// There are 4 complete fronts in this test case
		int Q_in = 1, Q_b = 1, Q_out = 1;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.frontsVsBackPorch();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set().assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch().set().assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testFrontsVsBackPorch2()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
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

	@Test
	public void testBackPorchValues1()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("a").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch().set(true).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues2()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set("b").assign(a);
		mod.getFrontPorch(1).set().assign(a);
		mod.getBackPorch().set(false).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues3()
	{
		// There is 1 complete front in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a").assign(a);
		mod.getBackPorch().set(true).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues4()
	{
		// There are 2 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		mod.getBackPorch().set(true, false).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues5()
	{
		// There are 2 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set().assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c").assign(a);
		mod.getBackPorch().set(true, false).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues6()
	{
		// There are 3 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b").assign(a);
		mod.getFrontPorch(0).set("c").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c").assign(a);
		mod.getBackPorch().set(true, false, true).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testBackPorchValues7()
	{
		// There are 5 complete fronts in this test case
		int Q_in = 5, Q_b = 5, Q_out = 5;
		BinaryApplyFunctionModule mod = new BinaryApplyFunctionModule("f", new FunctionEquals(s_domLetters), Q_in, Q_b, Q_out);
		Condition c = mod.backPorchValues();
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a", "b", "c").assign(a);
		mod.getFrontPorch(0).set("c", "d").assign(a);
		mod.getBuffer(1).set().assign(a);
		mod.getFrontPorch(1).set("a", "c", "c", "b", "a").assign(a);
		mod.getBackPorch().set(true, false, true, false, false).assign(a);
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
