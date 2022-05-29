package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.uqac.lif.symbolif.Assignment;
import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Domain;

public class BeepBeepModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});

	@Test
	public void testAtBuffer1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atBuffer(0, 3, 3);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testAtBuffer2()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atBuffer(0, 3, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testAtPorch1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testAtPorch2()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 1, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testAtPorch3()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 1, 5);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testAtPorch4()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 4, 8);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testAtPorch5()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 4, 10);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testAtPorch6()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(0, 1, 1);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe2()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(0, 8);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe3()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(0, 9);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe4()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(0, 10);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe2()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 7);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe3()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 8);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe4()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 9);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe5()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set();
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);		
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe6()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		BeepBeepModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b");
		mod.getFrontPorch(0).set("a", "b");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);		
		assertTrue(c.evaluate(a));
	}

	protected static class DummyBeepBeepModule extends BeepBeepModule
	{
		public DummyBeepBeepModule(int in_arity, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out) 
		{
			super("foo", in_arity, in_domain, out_domain, Q_in, Q_b, Q_out);
		}
	}
}
