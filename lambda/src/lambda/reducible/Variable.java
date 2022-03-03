package lambda.reducible;

import java.util.Map;

import lambda.Reducible;

public class Variable implements Reducible {

	private static int ID = 0;
	
	private final int id = ID++;
	private final Reducible type;
	
	
	public Variable(Reducible type) {
		this.type = type;
	}
	
	@Override
	public Reducible type() {
		return type;
	}
	
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	public <X> Reducible replace(Variable variable, Reducible term) {
		return equals(variable) ? (Reducible)term : this;
	}

	@Override
	public Reducible reduce() {
		return this;
	}

	@Override
	public boolean isMapping(Reducible term, Map<Variable, Reducible> map) {
		try {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().isMapping(term.type(), map)) {
				map.put(this, term);
				return true;
			}
		} catch (AssertionError e) {}
		return false;
		
		/*
		if (term instanceof Variable) {
			Variable<?> that = (Variable<?>) term;
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().isMapping(term.type(), map)) {
				map.put(this, that);
				return true;
			}
		}
		return false;
		*/
	}

	@Override
	public boolean contains(Variable variable) {
		return equals(variable) || type.contains(variable);
	}
	

}