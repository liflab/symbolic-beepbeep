package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.ScalarVariable;

public class FunctionEqualsTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void test1()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "a").set(z, true);
		assertEquals(true, c.evaluate(a));
	}
	
	@Test
	public void test2()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "a").set(z, false);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test3()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "b").set(z, true);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test4()
	{
		FunctionEquals eq = new FunctionEquals(s_domLetters);
		ScalarVariable x = new ScalarVariable("x", eq.getInputDomain(0));
		ScalarVariable y = new ScalarVariable("y", eq.getInputDomain(1));
		ScalarVariable z = new ScalarVariable("z", eq.getOutputDomain());
		Condition c = eq.getCondition(x, y, z);
		Assignment a = new Assignment().set(x, "a").set(y, "b").set(z, false);
		assertEquals(true, c.evaluate(a));
	}
}
