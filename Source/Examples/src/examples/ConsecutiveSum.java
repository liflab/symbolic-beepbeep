package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.BinaryApplyFunctionModule;
import ca.uqac.lif.cep.nusmv.ForkModule;
import ca.uqac.lif.cep.nusmv.PresetProcessorQueue;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.TrimModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers.Addition;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class ConsecutiveSum extends BeepBeepPipeline
{
	public ConsecutiveSum(ProcessorQueue input, IntegerRange range, int Q_in, int Q_b)
	{
		super("Consecutive sum", new ProcessorQueue[] {input}, new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_in + Q_b, range)});
		ForkModule fork = new ForkModule("Fork2", range, 2, Q_in);
		TrimModule trim = new TrimModule("Trim1", 1, range, Q_in);
		connect(fork, 0, trim, 0);
		BinaryApplyFunctionModule add = new BinaryApplyFunctionModule("Add", new Addition(range), Q_in, Q_b, Q_in + Q_b);
		connect(trim, 0, add, 0);
		connect(fork, 1, add, 1);
		setInput(fork, 0, 0);
		setOutput(add, 0, 0);
		add(fork, trim, add);
	}
	
	public static void main(String[] args) 
	{
		int Q_in = 1, Q_b = 1;
		IntegerRange range = new IntegerRange(0, 3);
		PresetProcessorQueue inputs = new PresetProcessorQueue("in", "in_c", "in_b", "cnt", Q_in, range, 5, false);
		inputs.addStep(1);
		inputs.addStep(2);
		inputs.addStep(3);
		inputs.addStep(1);
		inputs.addStep(0);
		ConsecutiveSum mod = new ConsecutiveSum(inputs, range, Q_in, Q_b);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}

}
