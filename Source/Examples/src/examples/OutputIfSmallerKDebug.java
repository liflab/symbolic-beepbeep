package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.BinaryApplyFunctionModule;
import ca.uqac.lif.cep.nusmv.BinaryTurnIntoFunction;
import ca.uqac.lif.cep.nusmv.CumulateModule;
import ca.uqac.lif.cep.nusmv.FilterModule;
import ca.uqac.lif.cep.nusmv.ForkModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PassthroughModule;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.TurnIntoModule;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class OutputIfSmallerKDebug extends BeepBeepPipeline
{
	public OutputIfSmallerKDebug(ProcessorQueue input_queue1, int k, int Q_in, int q_size, int Q_out, Domain domain)
	{
		super("OutputIfSmallerK",
				new ProcessorQueue[] {input_queue1}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, domain)});
		ForkModule f = new ForkModule("Fork2", domain, 2, 1);
		FilterModule filter = new FilterModule("Filter", domain, Q_in, q_size, Q_out);
		connect(f, 0, filter, 0);
		connect(f, 1, filter, 1);
		//BinaryApplyFunctionModule gt = new BinaryApplyFunctionModule("Greater", new NusmvNumbers.IsGreaterOrEqual(domain), Q_in, q_size, Q_out);
		//BinaryApplyFunctionModule gt = new BinaryApplyFunctionModule("Greater", new BinaryTurnIntoFunction(domain, domain, BooleanDomain.instance, true), Q_in, q_size, Q_out);
		//connect(f, 1, gt, 0);
		//connect(f, 2, gt, 1);
		//connect(gt, 0, filter, 1);
		add(f, filter);
		setInput(f, 0, 0);
		setOutput(filter, 0, 0);
	}
	
	public static void main(String[] args)
	{
		int Q_in = 1, Q_b = 1, Q_out = 1, k = 0;
		BooleanDomain range = BooleanDomain.instance;
		ProcessorQueue inputs1 = new ProcessorQueue("in1", "in1_c", "in1_b", Q_in, range);
		OutputIfSmallerKDebug mod = new OutputIfSmallerKDebug(inputs1, k, Q_in, Q_b, Q_out, range);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}
}
