package lambda.term;

import lambda.Term;
import lambda.Type;
import lambda.reducible.ReducibleVariable;


public class Variable extends ReducibleVariable<Term> implements Term {

	private final Type type;
	
	public Variable(Type type) {
		this.type = type;
	}
	
	@Override
	public Type type() {
		return type;
	}

}
