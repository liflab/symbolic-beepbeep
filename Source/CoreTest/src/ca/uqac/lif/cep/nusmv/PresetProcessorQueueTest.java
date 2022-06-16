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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.BruteSolver;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Solver;

/**
 * Unit tests for {@link PresetProcessorQueue}.
 */
public class PresetProcessorQueueTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void test1()
	{
		PresetProcessorQueue q = new PresetProcessorQueue("q", "q_c", "q_b", "cnt", 2, s_domLetters, 3, false);
		q.addStep("a").addStep("b").addStep("c");
		Assignment a = new Assignment();
		Conjunction c = new Conjunction();
		q.addToInit(c);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		q.set("a").assign(a);
		q.getStateCounter().set(0).assign(a);
		assertEquals(true, c.evaluate(a));
		q.set("b").assign(a);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test2()
	{
		PresetProcessorQueue q = new PresetProcessorQueue("q", "q_c", "q_b", "cnt", 2, s_domLetters, 3, false);
		q.addStep("a").addStep("b").addStep("c");
		Assignment a = new Assignment();
		Conjunction c = new Conjunction();
		q.addToTrans(c);
		q.getStateCounter().set(1).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		q.next().set("c").assign(a);
		q.getStateCounter().next().set(2).assign(a);
		assertEquals(true, c.evaluate(a));
		q.next().set("b").assign(a);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test3()
	{
		PresetProcessorQueue q = new PresetProcessorQueue("q", "q_c", "q_b", "cnt", 2, s_domLetters, 3, false);
		q.addStep("a").addStep("b").addStep("c", "b");
		Assignment a = new Assignment();
		Conjunction c = new Conjunction();
		q.addToTrans(c);
		q.getStateCounter().set(1).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		q.next().set("c", "b").assign(a);
		q.getStateCounter().next().set(2).assign(a);
		assertEquals(true, c.evaluate(a));
		q.next().set("c").assign(a);
		assertEquals(false, c.evaluate(a));
	}
	
	@Test
	public void test4()
	{
		PresetProcessorQueue q = new PresetProcessorQueue("q", "q_c", "q_b", "cnt", 2, s_domLetters, 3, true);
		q.addStep("a").addStep("b").addStep("c", "b");
		Assignment a = new Assignment();
		Conjunction c = new Conjunction();
		q.addToTrans(c);
		q.getStateCounter().set(2).assign(a);
		List<Assignment> solutions = s_solver.solveAll(c, a);
		assertEquals(1, solutions.size());
		q.next().set("a").assign(a);
		q.getStateCounter().next().set(0).assign(a);
		assertEquals(true, c.evaluate(a));
		q.next().set("c").assign(a);
		assertEquals(false, c.evaluate(a));
	}
}
