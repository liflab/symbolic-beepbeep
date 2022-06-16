package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.FilterModule;
import ca.uqac.lif.cep.nusmv.ForkModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PresetProcessorQueue;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.UnaryApplyFunctionModule;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class FilterEven extends BeepBeepPipeline
{
	public FilterEven(ProcessorQueue input_queue, int Q_in, int Q_out, IntegerRange d, int width)
	{
		super("Filter even",
				new ProcessorQueue[] {input_queue}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, d)});
		ForkModule fork = new ForkModule("Fork2", d, 2, Q_in);
		FilterModule filter = new FilterModule("Filter", d, Q_in, Q_in, Q_out);
		connect(fork, 0, filter, 0);
		UnaryApplyFunctionModule is_even = new UnaryApplyFunctionModule("IsEven", new NusmvNumbers.IsEven(d), Q_in, Q_out);
		connect(fork, 1, is_even, 0);
		connect(is_even, 0, filter, 1);
		add(fork, is_even, filter);
		setInput(fork, 0, 0);
		setOutput(filter, 0, 0);
	}
	
	public FilterEven(int Q_in, int Q_out, IntegerRange d, int width)
	{
		this(new ProcessorQueue("in", "in_c", "in_b", Q_in, d), Q_in, Q_out, d, width);
	}
	
	public static void main(String[] args) 
	{
		IntegerRange range = new IntegerRange(0, 3);
		PresetProcessorQueue inputs = new PresetProcessorQueue("in", "in_c", "in_b", "cnt", 2, range, 5, false);
		inputs.addStep(1);
		inputs.addStep(2);
		inputs.addStep(3);
		inputs.addStep(1);
		inputs.addStep(0);
		FilterEven mod = new FilterEven(inputs, 2, 2, range, 3);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}

}
