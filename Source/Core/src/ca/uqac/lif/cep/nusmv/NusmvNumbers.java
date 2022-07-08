/*
    Modeling of BeepBeep processor pipelines in NuSMV
    Copyright (C) 2020-2022 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.cep.nusmv;

import ca.uqac.lif.nusmv4j.Addition.AdditionModulo;
import ca.uqac.lif.nusmv4j.BooleanDomain;
import ca.uqac.lif.nusmv4j.Condition;
import ca.uqac.lif.nusmv4j.Conjunction;
import ca.uqac.lif.nusmv4j.Constant;
import ca.uqac.lif.nusmv4j.ConstantTrue;
import ca.uqac.lif.nusmv4j.Disjunction;
import ca.uqac.lif.nusmv4j.Domain;
import ca.uqac.lif.nusmv4j.Equality;
import ca.uqac.lif.nusmv4j.Equivalence;
import ca.uqac.lif.nusmv4j.GreaterThan;
import ca.uqac.lif.nusmv4j.Modulo;
import ca.uqac.lif.nusmv4j.Multiplication.MultiplicationModulo;
import ca.uqac.lif.nusmv4j.Negation;
import ca.uqac.lif.nusmv4j.Term;

public class NusmvNumbers
{
	/**
	 * A binary function handling numbers taken from a finite set. 
	 */
	public static abstract class BinaryNumberFunction implements BinaryFunctionCall
	{
		/**
		 * The domain of both inputs and the output of the function.
		 */
		/*@ non_null @*/ protected final Domain m_domain;
		
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which is assumed to be the same for both
		 * inputs and the output of the function.
		 */
		public BinaryNumberFunction(Domain d)
		{
			super();
			m_domain = d;
		}
		
		@Override
		public Domain getInputDomain(int index)
		{
			return m_domain;
		}

		@Override
		public Domain getOutputDomain() 
		{
			return m_domain;
		}
	}
	
	/**
	 * A binary function handling numbers taken from a finite set. 
	 */
	public static abstract class UnaryNumberFunction implements UnaryFunctionCall
	{
		/**
		 * The domain of both inputs and the output of the function.
		 */
		/*@ non_null @*/ protected final Domain m_domain;
		
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which is assumed to be the same for both
		 * inputs and the output of the function.
		 */
		public UnaryNumberFunction(Domain d)
		{
			super();
			m_domain = d;
		}
		
		@Override
		public Domain getInputDomain()
		{
			return m_domain;
		}

		@Override
		public Domain getOutputDomain() 
		{
			return m_domain;
		}
	}
	
	/**
	 * Binary function for modular addition. The modulus of the addition is
	 * determined by the size of the domain passed to the function.
	 */
	public static class Addition extends BinaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public Addition(Domain d)
		{
			super(d);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
		{
			AdditionModulo plus = new AdditionModulo(m_domain.getSize());
			plus.add((Term<Number>) x);
			plus.add((Term<Number>) y);
			Equality eq = new Equality(plus, z);
			return eq;
		}
		
		@Override
		public String toString()
		{
			return "+";
		}
	}
	
	/**
	 * Binary function for modular multiplication. The modulus of the
	 * multiplication is determined by the size of the domain passed to the
	 * function.
	 */
	public static class Multiplication extends BinaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public Multiplication(Domain d)
		{
			super(d);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
		{
			MultiplicationModulo mul = new MultiplicationModulo(m_domain.getSize());
			mul.add((Term<Number>) x);
			mul.add((Term<Number>) y);
			Equality eq = new Equality(mul, z);
			return eq;
		}
		
		@Override
		public String toString()
		{
			return "\u00d7";
		}
	}
	
	/**
	 * Binary function for integer division. The modulus of the addition is
	 * determined by the size of the domain passed to the function.
	 */
	public static class IntegerDivision extends BinaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public IntegerDivision(Domain d)
		{
			super(d);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
		{
			AdditionModulo plus = new AdditionModulo(m_domain.getSize());
			plus.add((Term<Number>) x);
			plus.add((Term<Number>) y);
			Equality eq = new Equality(plus, z);
			return eq;
		}
		
		@Override
		public String toString()
		{
			return "+";
		}
	}
	
	/**
	 * Binary function for modular multiplication. The modulus of the
	 * multiplication is determined by the size of the domain passed to the
	 * function.
	 */
	public static class IsGreaterOrEqual extends BinaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public IsGreaterOrEqual(Domain d)
		{
			super(d);
		}
		
		@Override
		public Domain getOutputDomain()
		{
			return BooleanDomain.instance;
		}
		
		@Override
		public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
		{
			GreaterThan mul = new GreaterThan(x, y);
			Equality eq = new Equality(mul, z);
			return eq;
		}
		
		@Override
		public String toString()
		{
			return "\u2265";
		}
	}
	
	
	/**
	 * Binary function for modular addition. The modulus of the addition is
	 * determined by the size of the domain passed to the function.
	 */
	public static class Maximum extends BinaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public Maximum(Domain d)
		{
			super(d);
		}
		
		@Override
		public Condition getCondition(Term<?> x, Term<?> y, Term<?> z)
		{
			Disjunction or = new Disjunction();
			{
				Conjunction in_and = new Conjunction();
				in_and.add(new GreaterThan(x, y));
				in_and.add(new Equality(x, z));
				or.add(in_and);
			}
			{
				Conjunction in_and = new Conjunction();
				in_and.add(new Negation(new GreaterThan(x, y)));
				in_and.add(new Equality(y, z));
				or.add(in_and);
			}
			return or;
		}
		
		@Override
		public String toString()
		{
			return "max";
		}
	}
	
	/**
	 * Function that determines if a number is even.
	 */
	public static class IsEven extends UnaryNumberFunction
	{
		/**
		 * Creates a new instance of the function for a given domain.
		 * @param d The domain, which must be a discrete set for the form
		 * {0, 1, &hellip; n}.
		 */
		public IsEven(Domain d)
		{
			super(d);
		}

		@Override
		public Condition getCondition(Term<?> x, Term<?> y)
		{
			Equivalence eq = new Equivalence();
			eq.add(new Equality(new Modulo(x, new Constant(2)), new Constant(0)));
			eq.add(new Equality(ConstantTrue.TRUE, y));
			return eq;
		}
		
		@Override
		public Domain getOutputDomain() 
		{
			return BooleanDomain.instance;
		}
	}
}
