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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;

public class ProcessorQueueTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void testWellFormed1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.isWellFormed(false);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testWellFormed2()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.isWellFormed(false);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		pq.m_arrayFlags.setValues(true, false, true, true, false);
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a)); // Contains a "hole"
	}
	
	@Test
	public void testWellFormed3()
	{
		int Q = 1;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.isWellFormed(false);
		assertNotNull(c);
		pq.set("b");
		pq.m_arrayFlags.setValues(false);
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a)); // Not default value at index 0
	}
	
	@Test
	public void testLambdaAtLeast1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(false, 3);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaAtLeast2()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(false, 5);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testLambdaAtLeast3()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(false, 6);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testLambdaAtLeast4()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(false, 0);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaAtLeast5()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(false, 5);
		assertNotNull(c);
		pq.set("a", "b", "c", "a", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(false, 3);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals2()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(false, 4);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals3()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(false, 5);
		assertNotNull(c);
		pq.set("a", "b", "c", "a", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals4()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(false, 0);
		assertNotNull(c);
		pq.set();
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals5()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(false, 3);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		pq.m_arrayFlags.setValues(true, false, true, true, false);
		Assignment a = new Assignment();
		pq.assign(a);
		assertFalse(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEqualsNext1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q", "q_c", "q_b", Q, s_domLetters);
		ProcessorQueue pq_next = pq.next();
		Condition c = pq_next.hasLength(false, 3);
		assertNotNull(c);
		pq_next.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq_next.assign(a);
		assertFalse(c.evaluate(a));
	}
}
