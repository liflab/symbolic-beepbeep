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

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.nusmv4j.Module;
import ca.uqac.lif.nusmv4j.NusmvFile;
import ca.uqac.lif.nusmv4j.NusmvPrintable;

/**
 * A NuSMV file made of a BeepBeep pipeline. A BeepBeep model takes care of
 * fetching all distinct module definitions (all modules with different names)
 * and printing them to a file, with the main module at the end.
 */
public class BeepBeepModel extends NusmvFile
{
	/**
	 * Creates a new BeepBeep model.
	 * @param pipeline The pipeline used as the main module
	 */
	public BeepBeepModel(BeepBeepPipeline pipeline)
	{
		super();
		Set<String> module_names = new HashSet<String>();
		Set<ProcessorModule> modules = new HashSet<ProcessorModule>();
		pipeline.addModules(modules);
		for (ProcessorModule mod : modules)
		{
			String mod_name = mod.getName();
			if (!module_names.contains(mod_name))
			{
				add(mod);
				module_names.add(mod_name);
			}
		}
		add(pipeline);
	}
	
	public Set<Module> getModules()
	{
		Set<Module> modules = new HashSet<Module>();
		for (NusmvPrintable p : m_parts)
		{
			if (p instanceof Module)
			{
				modules.add((Module) p);
			}
		}
		return modules;
	}
}
