package lambda.reducible;

import java.util.Map;
import java.util.Objects;

import lambda.Reducible;

public class Constant implements Reducible {

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
	public Reducible replace(Variable variable, Reducible term) {
		return this;
	}

	@Override
	public Reducible reduce() {
		return this;
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
		return name;
	}

	@Override
	public boolean contains(Variable variable) {
		return false;
	}

}
