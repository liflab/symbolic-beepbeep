package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.symbolif.ArrayVariable;
import ca.uqac.lif.symbolif.Assignment;
import ca.uqac.lif.symbolif.Condition;
import ca.uqac.lif.symbolif.Conjunction;
import ca.uqac.lif.symbolif.Disjunction;
import ca.uqac.lif.symbolif.Domain;
import ca.uqac.lif.symbolif.Equivalence;
import ca.uqac.lif.symbolif.LogicModule;
import ca.uqac.lif.symbolif.PrettyPrintStream;
import ca.uqac.lif.symbolif.Term;
import ca.uqac.lif.symbolif.Variable;

public class ApplyFunctionModule extends BeepBeepModule
{

	public ApplyFunctionModule(String name, int in_arity, Domain in_domain, Domain out_domain, int Q_in, int Q_b, int Q_out)
	{
		super(name, in_arity, in_domain, out_domain, Q_in, Q_b, Q_out);
	}

	/*@ non_null @*/ public static Condition buildInitialState(int Q_up, int Q_b)
	{
		Conjunction big_and = new Conjunction();
		return big_and;
	}
	
	/**
	 * Generates the condition asserting that the input pipes contain exactly
	 * n complete event fronts.
	 * @param n The number of event fronts
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition numFronts(int n)
	{
		Disjunction or = new Disjunction();
		{
			Conjunction and = new Conjunction();
			and.add(hasTotalPipe(0, n));
			and.add(minTotalPipe(1, n));
			or.add(and);
		}
		{
			Conjunction and = new Conjunction();
			and.add(minTotalPipe(0, n));
			and.add(hasTotalPipe(1, n));
			or.add(and);
		}
		return or;
	}
	
	/**
	 * Generates the condition stipulating that the size of the back porch is
	 * equal to the number of complete event fronts in the input pipes.
	 * @return The condition
	 */
	/*@ non_null @*/ public Condition frontsVsBackPorch()
	{
		ProcessorQueue back_porch = getBackPorch();
		Conjunction and = new Conjunction();
		for (int i = 0; i <= back_porch.getSize() - 1; i++)
		{
			Equivalence eq = new Equivalence();
			eq.add(numFronts(i));
			eq.add(back_porch.hasLength(i));
			and.add(eq);
		}
		return and;
	}
	
	public Condition backPorchValues(BinaryFunctionCall f)
	{
		return null;
	}
	
	protected static interface BinaryFunctionCall
	{
		public void printFor(Term<?> x, Term<?> y);		
	}
	
}
