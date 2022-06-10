package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Domain;

public class GroupModule extends ProcessorModule
{
	public GroupModule(String name, int in_arity, Domain[] in_domains, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, in_arity, in_domains, out_domain, Q_in, 0, Q_out);
	}

	@Override
	public ProcessorModule duplicate()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
