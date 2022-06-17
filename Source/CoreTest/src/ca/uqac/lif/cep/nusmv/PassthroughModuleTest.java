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
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.Solver;

/**
 * Unit tests for {@link PassthroughModule}.
 */
public class PassthroughModuleTest
{
protected static Domain s_domNumbers = new IntegerRange(0, 3);
	
	protected static Solver s_solver = new BruteSolver();
	
	@Test
	public void testInOut1()
	{
		int Q_in = 2;
		PassthroughModule mod = new PassthroughModule("pt", s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		Condition c = mod.new MatchingPorches(false);
		List<Assignment> sols = s_solver.solveAll(c, a, mod.getFrontPorch(0).isWellFormed(false), mod.getBackPorch(0).isWellFormed(false));
		assertEquals(1, sols.size());
	}
	
	@Test
	public void testGetInit1()
	{
		int Q_in = 2;
		PassthroughModule mod = new PassthroughModule("pt", s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1, 2).assign(a);
		Condition c = mod.getInit();
		List<Assignment> sols = s_solver.solveAll(c, a);
		assertEquals(1, sols.size());
	}
	
	@Test
	public void testInOut3()
	{
		int Q_in = 2;
		PassthroughModule mod = new PassthroughModule("pt", s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).next().set(1, 2).assign(a);
		Condition c = mod.new MatchingPorches(true);
		List<Assignment> sols = s_solver.solveAll(c, a);
		assertEquals(1, sols.size());
	}
	
	@Test
	public void testGetTrans1()
	{
		int Q_in = 2;
		PassthroughModule mod = new PassthroughModule("pt", s_domNumbers, Q_in);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).next().set(1).assign(a);
		Condition c = mod.getTrans();
		List<Assignment> sols = s_solver.solveAll(c, a);
		assertEquals(1, sols.size());
	}
}
