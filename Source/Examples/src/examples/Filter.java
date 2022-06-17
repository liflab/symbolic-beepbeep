package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.FilterModule;
import ca.uqac.lif.cep.nusmv.ForkModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PresetProcessorQueue;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.UnaryApplyFunctionModule;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class Filter extends BeepBeepPipeline
{
	public Filter(ProcessorQueue input_queue1, ProcessorQueue input_queue2, int Q_in, int Q_out, IntegerRange d, int width)
	{
		super("Filter",
				new ProcessorQueue[] {input_queue1, input_queue2}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, d)});
		FilterModule filter = new FilterModule("Filter", d, Q_in, Q_in, Q_out);
		add(filter);
		setInput(filter, 0, 0);
		setInput(filter, 1, 1);
		setOutput(filter, 0, 0);
	}
	
	public static void main(String[] args) 
	{
		IntegerRange range = new IntegerRange(0, 3);
		PresetProcessorQueue inputs1 = new PresetProcessorQueue("in1", "in1_c", "in1_b", "cnt1", 2, range, 5, false);
		inputs1.addStep(1);
		inputs1.addStep(2);
		inputs1.addStep(3);
		inputs1.addStep(1);
		inputs1.addStep(0);
		PresetProcessorQueue inputs2 = new PresetProcessorQueue("in2", "in2_c", "in2_b", "cnt2", 2, BooleanDomain.instance, 5, false);
		inputs2.addStep(false);
		inputs2.addStep(true);
		inputs2.addStep(false);
		inputs2.addStep(false);
		inputs2.addStep(true);
		Filter mod = new Filter(inputs1, inputs2, 2, 2, range, 3);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}

}
