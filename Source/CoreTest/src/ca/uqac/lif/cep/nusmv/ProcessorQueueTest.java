package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.uqac.lif.symbolif.Assignment;
import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Domain;

public class ProcessorQueueTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void testLambdaAtLeast1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(3);
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
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(5);
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
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(6);
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
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.minLength(0);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
	
	@Test
	public void testLambdaEquals1()
	{
		int Q = 5;
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(3);
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
		ProcessorQueue pq = new ProcessorQueue("q_c", "q_b", Q, s_domLetters);
		Condition c = pq.hasLength(4);
		assertNotNull(c);
		pq.set("a", "b", "c", "a");
		Assignment a = new Assignment();
		pq.assign(a);
		assertTrue(c.evaluate(a));
	}
}
