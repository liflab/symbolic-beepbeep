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

public class Sum extends BeepBeepPipeline
{
	public Sum(ProcessorQueue input1, ProcessorQueue input2, IntegerRange range, int Q_in, int Q_b)
	{
		super("Sum", new ProcessorQueue[] {input1, input2}, new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_in + Q_b, range)});
		BinaryApplyFunctionModule add = new BinaryApplyFunctionModule("Add", new Addition(range), Q_in, Q_b, Q_in + Q_b);
		setInput(add, 0, 0);
		setInput(add, 1, 1);
		setOutput(add, 0, 0);
		add(add);
	}
	
	public static void main(String[] args) 
	{
		int Q_in = 1, Q_b = 1;
		IntegerRange range = new IntegerRange(0, 3);
		PresetProcessorQueue inputs1 = new PresetProcessorQueue("in1", "in1_c", "in1_b", "cnt1", Q_in, range, 5, false);
		inputs1.addStep(1);
		inputs1.addStep(2);
		inputs1.addStep(3);
		inputs1.addStep(1);
		inputs1.addStep(0);
		PresetProcessorQueue inputs2 = new PresetProcessorQueue("in2", "in2_c", "in2_b", "cnt2", Q_in, range, 5, false);
		inputs2.addStep();
		inputs2.addStep(2);
		inputs2.addStep(3);
		inputs2.addStep(1);
		inputs2.addStep(0);
		Sum mod = new Sum(inputs1, inputs2, range, Q_in, Q_b);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}

}
