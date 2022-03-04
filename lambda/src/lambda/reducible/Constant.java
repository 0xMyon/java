package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lambda.Irreducible;
import lambda.Reducible;

public class Constant implements Irreducible {

	private final Constant type;
	
	private final String name;
	
	public Constant(String name, Constant type) {
		this.type = type;
		this.name = name;
	}
	
	public Constant(String name) {
		this(name, null);
	}

	@Override
	public boolean isMapping(Reducible term, Map<Variable, Reducible> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Constant type() {
		assert Objects.nonNull(type) : "can not get type of '"+name+"'";
		return type;
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}
	
	@Override
	public String toString(Map<Variable, String> names) {
		return name;
	}

	@Override
	public boolean contains(Variable variable) {
		return false;
	}

}
