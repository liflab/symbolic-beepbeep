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

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;

/**
 * Unit tests for basic methods of {@link BeepBeepModule}.
 */
public class BeepBeepModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void testResetAt1()
	{
		int Q_in = 5, Q_b = 0, Q_out = 5;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getResetFlag().set(true).assign(a);
		assertTrue(mod.new IsReset(false).evaluate(a));
		assertFalse(mod.new NoReset(false).evaluate(a));
	}
	
	@Test
	public void testResetAt2()
	{
		int Q_in = 5, Q_b = 0, Q_out = 5;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getResetFlag().set(false).assign(a);
		assertFalse(mod.new IsReset(false).evaluate(a));
		assertTrue(mod.new NoReset(false).evaluate(a));
	}
	
	@Test
	public void testAtBuffer1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atBuffer(false, 0, 3, 3);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atBuffer(false, 0, 3, 4);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 0, 4);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 1, 4);
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
		int Q_in = 5, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 1, 5);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 4, 8);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 4, 10);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.atPorch(false, 0, 1, 1);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe2a()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 2);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testMinTotalPipe2b()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 1);
		assertNotNull(c);
		Assignment a = new Assignment();
		mod.getBuffer(0).set("a").assign(a);
		mod.getFrontPorch(0).set("a").assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testMinTotalPipe3()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 9);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 10);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b", "c", "a");
		mod.getFrontPorch(0).set("a", "b", "c", "a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testMinTotalPipe5()
	{
		int Q_in = 1, Q_b = 1, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 0);
		assertNotNull(c);
		mod.getBuffer(0).set();
		mod.getFrontPorch(0).set();
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testMinTotalPipe6()
	{
		int Q_in = 1, Q_b = 1, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.minTotalPipe(false, 0, 0);
		assertNotNull(c);
		mod.getBuffer(0).set("a");
		mod.getFrontPorch(0).set();
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe1()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 4);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 7);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 2);
		assertNotNull(c);
		mod.getBuffer(0).set("a");
		mod.getFrontPorch(0).set("a");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);
		assertTrue(c.evaluate(a));
	}

	@Test
	public void testHasTotalPipe4()
	{
		int Q_in = 1, Q_b = 5, Q_out = 1;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 9);
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
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 4);
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
		int Q_in = 2, Q_b = 5, Q_out = 2;
		ProcessorModule mod = new DummyBeepBeepModule(1, s_domLetters, s_domLetters, Q_in, Q_b, Q_out);
		Condition c = mod.hasTotalPipe(false, 0, 4);
		assertNotNull(c);
		mod.getBuffer(0).set("a", "b");
		mod.getFrontPorch(0).set("a", "b");
		Assignment a = new Assignment();
		mod.getBuffer(0).assign(a);
		mod.getFrontPorch(0).assign(a);		
		assertTrue(c.evaluate(a));
	}

	protected static class DummyBeepBeepModule extends ProcessorModule
	{
		public DummyBeepBeepModule(int in_arity, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out) 
		{
			super("foo", in_arity, new Domain[] {in_domain}, 1, out_domain, Q_in, Q_b, Q_out);
		}
		
		public DummyBeepBeepModule duplicate()
		{
			// Not needed
			return null;
		}

		@Override
		protected void addToInit(Conjunction c)
		{
			// Nothing to do
		}

		@Override
		protected void addToTrans(Conjunction c)
		{
			// Nothing to do
		}
	}
}
