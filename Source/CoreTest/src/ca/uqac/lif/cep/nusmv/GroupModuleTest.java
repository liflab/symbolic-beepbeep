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

/**
 * Unit tests for {@link GroupModule}.
 */
public class GroupModuleTest
{
	protected static Domain s_domLetters = new Domain(new Object[] {"a", "b", "c"});
	
	@Test
	public void testPrint1()
	{
		int Q_in = 2, Q_out = 2;
		GroupModule mod = new GroupModule("Group", 1, new Domain[] {s_domLetters}, 1, new Domain[] {s_domLetters}, Q_in, Q_out);
		PassthroughModule pt1 = new PassthroughModule("Passthrough", s_domLetters, Q_in);
		PassthroughModule pt2 = new PassthroughModule("Passthrough", s_domLetters, Q_in);
		mod.add(pt1, pt2);
		mod.connect(pt1, 0, pt2, 0);
		mod.associateInput(0, pt1, 0);
		mod.associateOutput(0, pt2, 0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrettyPrintStream ps = new PrettyPrintStream(baos);
		mod.print(ps);
		String out = baos.toString();
		assertNotNull(out);
		assertFalse(out.contains("ERROR")); // Indicating a problem with the transition relation
		System.out.println(out);
	}
}
