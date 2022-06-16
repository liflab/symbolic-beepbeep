package ca.uqac.lif.cep.nusmv;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.Solver;

public class ForkModuleTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 4);
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void test1()
	{
		ForkModule mod = new ForkModule("Fork", s_domNumbers, 2, 2);
		Assignment a = new Assignment();
		mod.getResetFlag().set(false).assign(a);
		mod.getFrontPorch(0).set(1).assign(a);
		Condition c = mod.getInit();
		List<Assignment> sols = s_solver.solveAll(c, a);
		assertEquals(1, sols.size());
	}
}
