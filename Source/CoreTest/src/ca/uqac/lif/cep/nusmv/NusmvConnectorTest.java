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

import java.util.List;

import org.junit.Test;

import ca.uqac.lif.cep.nusmv.NusmvConnector.NusmvConnectorException;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.ModuleDomain;
import ca.uqac.lif.nusmv4j.ScalarVariable;

/**
 * Unit tests for {@link NusmvConnector}.
 */
public class NusmvConnectorTest
{
	protected static Domain s_domNumbers = new IntegerRange(0, 3);
	
	protected static Domain s_domNumbersLarge = new IntegerRange(0, 5);
	
	@Test
	public void testConnect1()
	{
		int Q = 2;
		ScalarVariable reset = new ScalarVariable("reset", BooleanDomain.instance);
		NusmvConnector connector = new NusmvConnector(reset);
		PassthroughModule pt1 = new PassthroughModule("pt1", s_domNumbers, Q);
		PassthroughModule pt2 = new PassthroughModule("pt2", s_domNumbers, Q);
		connector.connect(pt1, 0, pt2, 0);
		ProcessorQueue in = new ProcessorQueue("in", "in_c", "in_b", Q, s_domNumbers);
		ProcessorQueue out = new ProcessorQueue("ou", "ou_c", "ou_b", Q, s_domNumbers);
		connector.setInput(pt1, 0, in);
		connector.setOutput(pt2, 0, out);
		ModuleDomain dom;
		dom = connector.getDomain(pt1);
		assertNotNull(dom);
		dom = connector.getDomain(pt2);
		assertNotNull(dom);
		List<ProcessorQueue> inner_queues = connector.getInnerQueues();
		assertEquals(1, inner_queues.size());
		assertNotNull(inner_queues.get(0));
	}
	
	@Test(expected = NusmvConnectorException.class)
	public void testConnect2()
	{
		int Q = 2;
		ScalarVariable reset = new ScalarVariable("reset", BooleanDomain.instance);
		NusmvConnector connector = new NusmvConnector(reset);
		PassthroughModule pt1 = new PassthroughModule("pt1", s_domNumbers, Q);
		PassthroughModule pt2 = new PassthroughModule("pt2", s_domNumbers, Q + 1);
		connector.connect(pt1, 0, pt2, 0);
	}
	
	@Test(expected = NusmvConnectorException.class)
	public void testConnect3()
	{
		int Q = 2;
		ScalarVariable reset = new ScalarVariable("reset", BooleanDomain.instance);
		NusmvConnector connector = new NusmvConnector(reset);
		PassthroughModule pt1 = new PassthroughModule("pt1", s_domNumbersLarge, Q);
		PassthroughModule pt2 = new PassthroughModule("pt2", s_domNumbers, Q);
		connector.connect(pt1, 0, pt2, 0);
	}
}
