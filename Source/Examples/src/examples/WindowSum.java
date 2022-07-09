package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.CumulateModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PassthroughModule;
import ca.uqac.lif.cep.nusmv.PresetProcessorQueue;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.WindowModule;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class WindowSum extends BeepBeepPipeline
{
	public WindowSum(ProcessorQueue input_queue, int Q_in, int Q_out, IntegerRange d, int width)
	{
		super("WindowSum",
				new ProcessorQueue[] {input_queue}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, d)});
		WindowModule win = new WindowModule("Window" + width,
				//new PassthroughModule("pt", d, width),
				new CumulateModule("Sum", new NusmvNumbers.Addition(d), width, width),
				width, d, d, Q_in, Q_out);
		add(win);
		setInput(win, 0, 0);
		setOutput(win, 0, 0);
	}
	
	public WindowSum(int Q_in, int Q_out, IntegerRange d, int width)
	{
		this(new ProcessorQueue("in", "in_c", "in_b", Q_in, d), Q_in, Q_out, d, width);
	}
	
	public static void main(String[] args) 
	{
		int width = 2;
		IntegerRange range = new IntegerRange(0, 2);
		PresetProcessorQueue inputs = new PresetProcessorQueue("in", "in_c", "in_b", "cnt", 1, range, 5, false);
		inputs.addStep(0);
		inputs.addStep(1);
		inputs.addStep(1);
		inputs.addStep(1);
		inputs.addStep(0);
		WindowSum mod = new WindowSum(inputs, 1, 1, range, width);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}

}
