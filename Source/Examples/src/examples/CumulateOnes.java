package examples;

import ca.uqac.lif.cep.nusmv.BeepBeepModel;
import ca.uqac.lif.cep.nusmv.BeepBeepPipeline;
import ca.uqac.lif.cep.nusmv.CumulateModule;
import ca.uqac.lif.cep.nusmv.NusmvNumbers;
import ca.uqac.lif.cep.nusmv.PassthroughModule;
import ca.uqac.lif.cep.nusmv.ProcessorQueue;
import ca.uqac.lif.cep.nusmv.TurnIntoModule;
import ca.uqac.lif.nusmv4j.IntegerRange;
import ca.uqac.lif.nusmv4j.PrettyPrintStream;

public class CumulateOnes extends BeepBeepPipeline
{
	public CumulateOnes(ProcessorQueue input_queue1, int Q_in, int Q_out, IntegerRange d)
	{
		super("CumulateOnes",
				new ProcessorQueue[] {input_queue1}, 
				new ProcessorQueue[] {new ProcessorQueue("ou", "ou_c", "ou_b", Q_out, d)});
		//TurnIntoModule t = new TurnIntoModule("TurnOne", d, d, 1, Q_in, Q_out);
		//PassthroughModule t = new PassthroughModule("TurnOne", d, Q_in);
		CumulateModule c = new CumulateModule("Sum", new NusmvNumbers.Addition(d), Q_in, Q_out);
		//connect(t, 0, c, 0);
		add(c);
		setInput(c, 0, 0);
		setOutput(c, 0 ,0);
	}
	
	public static void main(String[] args)
	{
		int Q_in = 1, Q_out = 1;
		IntegerRange range = new IntegerRange(0, 3);
		ProcessorQueue inputs1 = new ProcessorQueue("in1", "in1_c", "in1_b", Q_in, range);
		CumulateOnes mod = new CumulateOnes(inputs1, Q_in, Q_out, range);
		BeepBeepModel model = new BeepBeepModel(mod);
		model.print(new PrettyPrintStream(System.out));
	}
}
