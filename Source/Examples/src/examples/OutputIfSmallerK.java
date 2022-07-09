package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.BinaryApplyFunctionModule;
import ca.uqac.lif.cep.nusmv.CumulateModule;
import ca.uqac.lif.cep.nusmv.FilterModule;
import ca.uqac.lif.cep.nusmv.ForkModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PassthroughModule;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.TurnIntoModule;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class OutputIfSmallerK extends BeepBeepPipeline
{
	public OutputIfSmallerK(ProcessorQueue input_queue1, int k, int Q_in, int q_size, int Q_out, IntegerRange domain)
	{
		super("OutputIfSmallerK",
				new ProcessorQueue[] {input_queue1}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, domain)});
		ForkModule f = new ForkModule("Fork3", domain, 3, 1);
		FilterModule filter = new FilterModule("Filter", domain, Q_in, q_size, Q_out);
		connect(f, 0, filter, 0);
		TurnIntoModule turn_k = new TurnIntoModule("TurnK", domain, domain, k, Q_in, Q_out);
		connect(f, 1, turn_k, 0);
		TurnIntoModule turn_1 = new TurnIntoModule("TurnOne", domain, domain, 1, Q_in, Q_out);
		connect(f, 2, turn_1, 0);
		CumulateModule sum = new CumulateModule("Sum", new NusmvNumbers.Addition(domain), Q_in, Q_out);
		connect(turn_1, 0, sum, 0);
		BinaryApplyFunctionModule gt = new BinaryApplyFunctionModule("Greater", new NusmvNumbers.IsGreaterOrEqual(domain), Q_in, q_size, Q_out);
		connect(turn_k, 0, gt, 0);
		connect(sum, 0, gt, 1);
		connect(gt, 0, filter, 1);
		add(f, filter, turn_k, turn_1, sum, gt);
		setInput(f, 0, 0);
		setOutput(filter, 0, 0);
	}
	
	public static void main(String[] args)
	{
		int Q_in = 1, Q_b = 1, Q_out = 1, k = 0;
		IntegerRange range = new IntegerRange(0, 3);
		ProcessorQueue inputs1 = new ProcessorQueue("in1", "in1_c", "in1_b", Q_in, range);
		OutputIfSmallerK mod = new OutputIfSmallerK(inputs1, k, Q_in, Q_b, Q_out, range);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}
}
