package lambda.term;

import java.util.Map;

import lambda.Term;
import lambda.Type;

public class Variable implements Term {

	private final Type type;
	
	public Variable(Type type) {
		this.type = type;
	}
	
	@Override
	public Term replace(Variable variable, Term term) {
		return equals(variable) ? term : this;
	}

	@Override
	public Term reduce() {
		return this;
	}

	@Override
	public boolean isEqual(Term term, Map<Variable, Variable> map) {
		if (term instanceof Variable) {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else {
				map.put(this, (Variable)term);
				return true;
			}
		}
		return false;
	}

	@Override
	public Type type() {
		return type;
	}

	
	
}
