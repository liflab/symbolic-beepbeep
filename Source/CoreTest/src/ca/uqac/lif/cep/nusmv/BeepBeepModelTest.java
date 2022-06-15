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

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class BeepBeepModelTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void testPrint1()
	{
		int Q_in = 2, Q_out = 2;
		ProcessorQueue in_q = new ProcessorQueue("in", "in_c", "in_b", 2, s_domLetters);
		ProcessorQueue out_q = new ProcessorQueue("out", "ou_c", "ou_b", 2, s_domLetters);
		BeepBeepPipeline pipeline = new BeepBeepPipeline(new ProcessorQueue[] {in_q}, new ProcessorQueue[] {out_q});
		PassthroughModule pt1 = new PassthroughModule("Passthrough", s_domLetters, Q_in, Q_out);
		PassthroughModule pt2 = new PassthroughModule("Passthrough", s_domLetters, Q_in, Q_out);
		pipeline.add(pt1, pt2);
		pipeline.connect(pt1, 0, pt2, 0);
		pipeline.setInput(pt1, 0, 0);
		pipeline.setOutput(pt2, 0, 0);
		BeepBeepModel mod = new BeepBeepModel(pipeline);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrettyPrintStream ps = new PrettyPrintStream(baos);
		mod.print(ps);
		String out = baos.toString();
		assertNotNull(out);
		assertFalse(out.contains("ERROR")); // Indicating a problem with the transition relation
		System.out.println(out);
	}
	
	@Test
	public void testPrint2()
	{
		int Q_in = 2, Q_out = 2;
		ProcessorQueue in_q = new ProcessorQueue("in", "in_c", "in_b", 2, s_domLetters);
		ProcessorQueue out_q = new ProcessorQueue("out", "ou_c", "ou_b", 2, s_domLetters);
		BeepBeepPipeline pipeline = new BeepBeepPipeline(new ProcessorQueue[] {in_q}, new ProcessorQueue[] {out_q});
		TrimModule pt1 = new TrimModule("Trim1", 1, s_domLetters, Q_in, Q_out);
		PassthroughModule pt2 = new PassthroughModule("Passthrough", s_domLetters, Q_in, Q_out);
		pipeline.add(pt1, pt2);
		pipeline.connect(pt1, 0, pt2, 0);
		pipeline.setInput(pt1, 0, 0);
		pipeline.setOutput(pt2, 0, 0);
		BeepBeepModel mod = new BeepBeepModel(pipeline);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrettyPrintStream ps = new PrettyPrintStream(baos);
		mod.print(ps);
		String out = baos.toString();
		assertNotNull(out);
		assertFalse(out.contains("ERROR")); // Indicating a problem with the transition relation
		System.out.println(out);
	}
}