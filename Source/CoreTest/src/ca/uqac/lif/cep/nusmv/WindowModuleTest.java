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

import org.junit.Test;

import ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType;
import ca.uqac.lif.nusmv4j.Assignment;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;

import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.BUFFER;
import static ca.uqac.lif.cep.nusmv.ProcessorModule.QueueType.PORCH;

/**
 * Unit tests for {@link WindowModule}.
 */
public class WindowModuleTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 2);
	
	@Test
	public void testIsActive1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(2).assign(a);
		assertEquals(true, mod.new IsActive(false, 0).evaluate(a));
		assertEquals(false, mod.new IsActive(false, 1).evaluate(a));
		assertEquals(false, mod.new IsActive(false, 2).evaluate(a));
	}
	
	@Test
	public void testIsActive2()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1, 1).assign(a);
		mod.getBuffer(0).set(2).assign(a);
		assertEquals(true, mod.new IsActive(false, 0).evaluate(a));
		assertEquals(true, mod.new IsActive(false, 1).evaluate(a));
		assertEquals(false, mod.new IsActive(false, 2).evaluate(a));
	}
	
	@Test
	public void testIsActive3()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getBuffer(0).set(2).assign(a);
		assertEquals(false, mod.new IsActive(false, 0).evaluate(a));
		assertEquals(false, mod.new IsActive(false, 1).evaluate(a));
		assertEquals(false, mod.new IsActive(false, 2).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchSizes1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(0, 2).assign(a);
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(0);
			porch.set(1, 0, 2).assign(a);
		}
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(1);
			porch.set(0, 1, 0).assign(a);
		}
		mod.m_innerFrontPorches.get(2).set().assign(a);
		mod.m_innerFrontPorches.get(3).set().assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a);
		assertEquals(true, mod.innerFrontPorchSizes(false).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchSizes2()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(0, 2).assign(a);
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(0);
			porch.set(0, 2, 0).assign(a);
		}
		mod.m_innerFrontPorches.get(1).set().assign(a); // should contain 2, 0, 1 
		mod.m_innerFrontPorches.get(2).set().assign(a);
		mod.m_innerFrontPorches.get(3).set().assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a);
		assertEquals(false, mod.innerFrontPorchSizes(false).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchSizes3()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(0, 2).assign(a);
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(0);
			porch.set(1, 0, 2).assign(a);
		}
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(1);
			porch.set(0, 1, 0).assign(a);
		}
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(2);
			porch.set(0, 0, 1).assign(a);
		}
		mod.m_innerFrontPorches.get(3).set().assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a);
		assertEquals(false, mod.innerFrontPorchSizes(false).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchContents1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(0, 2).assign(a);
		// First instance
		assertEquals(true, mod.innerFrontPorchCellContents(false, 0, QueueType.BUFFER, 0, 0).evaluate(a));
		assertEquals(false, mod.innerFrontPorchCellContents(false, 0, QueueType.BUFFER, 0, 1).evaluate(a));
		assertEquals(false, mod.innerFrontPorchCellContents(false, 0, QueueType.BUFFER, 1, 0).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 0, QueueType.BUFFER, 1, 1).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 0, QueueType.PORCH, 0, 2).evaluate(a));
		// Second instance
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.BUFFER, 1, 0).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.PORCH, 0, 1).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.PORCH, 1, 2).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchContentsAll1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(0, 1).assign(a);
		mod.getBuffer(0).set(0, 2).assign(a);
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.BUFFER, 1, 0).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.PORCH, 0, 1).evaluate(a));
		assertEquals(true, mod.innerFrontPorchCellContents(false, 1, QueueType.PORCH, 1, 2).evaluate(a));
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(0);
			porch.set(0, 2, 0).assign(a);
			assertEquals(true, mod.new InnerFrontPorchContents(false, 0).evaluate(a));
			porch.set(1, 2, 0).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 0).evaluate(a));
			porch.set(0, 1, 0).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 0).evaluate(a));
			porch.set(0, 2, 1).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 0).evaluate(a));
		}
		{
			ProcessorQueue porch = mod.m_innerFrontPorches.get(1);
			porch.set(2, 0, 1).assign(a);
			assertEquals(true, mod.innerFrontPorchCellContents(false, 1, BUFFER, 1, 0).evaluate(a));           
			assertEquals(true, mod.new InnerFrontPorchContents(false, 1).evaluate(a));
			porch.set(1, 0, 1).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 1).evaluate(a));
			porch.set(2, 1, 1).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 1).evaluate(a));
			porch.set(2, 0, 2).assign(a);
			assertEquals(false, mod.new InnerFrontPorchContents(false, 1).evaluate(a));
		}
	}
	
	@Test
	public void testInnerFrontPorchContentsAll2()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(1).assign(a);
		mod.getBuffer(0).set().assign(a);
		mod.m_innerFrontPorches.get(0).set().assign(a);
		mod.m_innerFrontPorches.get(1).set().assign(a);
		mod.m_innerFrontPorches.get(2).set().assign(a);
		mod.m_innerFrontPorches.get(3).set().assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a);
		assertEquals(true, mod.innerFrontPorchContents(false).evaluate(a));
	}
	
	@Test
	public void testInnerFrontPorchContentsAll3()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.getFrontPorch(0).set(2, 1, 1).assign(a);
		mod.getBuffer(0).set(2, 1, 2).assign(a);
		mod.m_innerFrontPorches.get(0).set(2, 1, 2).assign(a);
		mod.m_innerFrontPorches.get(1).set(1, 2, 2).assign(a);
		mod.m_innerFrontPorches.get(2).set(2, 2, 1).assign(a);
		mod.m_innerFrontPorches.get(3).set(2, 1, 1).assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a);
		assertEquals(true, mod.innerFrontPorchContents(false).evaluate(a));
	}
	
	@Test
	public void testBackPorchContentsAll1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 3;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.m_innerBackPorches.get(0).set(0, 1).assign(a);
		mod.m_innerBackPorches.get(1).set(1, 2).assign(a);
		mod.m_innerBackPorches.get(2).set(0).assign(a);
		mod.m_innerBackPorches.get(3).set(1, 0).assign(a);
		mod.m_innerBackPorches.get(4).set().assign(a);
		mod.m_innerBackPorches.get(5).set().assign(a);
		ProcessorQueue back_porch = mod.getBackPorch();
		{
			back_porch.set(1, 2, 0, 0).assign(a);
			assertEquals(true, mod.new BackPorchContents(false, 0).evaluate(a));
			back_porch.set(0, 2, 0, 0).assign(a);
			assertEquals(false, mod.new BackPorchContents(false, 0).evaluate(a));
			back_porch.set(2, 2, 0, 0).assign(a);
			assertEquals(false, mod.new BackPorchContents(false, 0).evaluate(a));
		}
		{
			back_porch.set(1, 2, 0, 0).assign(a);
			assertEquals(true, mod.new BackPorchContents(false, 1).evaluate(a));
			back_porch.set(1, 1, 0, 0).assign(a);
			assertEquals(false, mod.new BackPorchContents(false, 1).evaluate(a));
			back_porch.set(1, 0, 0, 0).assign(a);
			assertEquals(false, mod.new BackPorchContents(false, 1).evaluate(a));
		}
	}
	
	@Test
	public void testBackPorchLength1()
	{
		int Q_in = 3, Q_b = 3, Q_out = 6;
		CumulateModule sum = new CumulateModule("sum", new NusmvNumbers.Addition(s_domNumbers), Q_in, Q_out);
		WindowModule mod = new WindowModule("win", sum, 3, s_domNumbers, s_domNumbers, Q_in, Q_b, Q_out);
		Assignment a = new Assignment();
		mod.m_innerFrontPorches.get(0).set(0, 0, 0).assign(a); // Must set front porches
		mod.m_innerBackPorches.get(0).set(0, 1).assign(a);
		mod.m_innerFrontPorches.get(1).set(0, 0, 0).assign(a); // Must set front porches
		mod.m_innerBackPorches.get(1).set(1, 2).assign(a);
		mod.m_innerFrontPorches.get(2).set(0, 0, 0).assign(a); // Must set front porches
		mod.m_innerBackPorches.get(2).set(0).assign(a);
		mod.m_innerFrontPorches.get(3).set(0, 0, 0).assign(a); // Must set front porches
		mod.m_innerBackPorches.get(3).set(1, 0).assign(a);
		mod.m_innerFrontPorches.get(4).set().assign(a); // Must set front porches
		mod.m_innerBackPorches.get(4).set().assign(a);
		mod.m_innerFrontPorches.get(5).set().assign(a); // Must set front porches
		mod.m_innerBackPorches.get(5).set().assign(a);
		ProcessorQueue back_porch = mod.getBackPorch();
		back_porch.set(1, 2, 0, 0).assign(a);
		assertEquals(true, mod.new BackPorchLength(false).evaluate(a));
		back_porch.set(1, 2, 0, 0, 0).assign(a);
		assertEquals(false, mod.new BackPorchLength(false).evaluate(a));
		back_porch.set(1, 2, 0).assign(a);
		assertEquals(false, mod.new BackPorchLength(false).evaluate(a));
		back_porch.set(1).assign(a);
		assertEquals(false, mod.new BackPorchLength(false).evaluate(a));
		back_porch.set().assign(a);
		assertEquals(false, mod.new BackPorchLength(false).evaluate(a));
	}
}