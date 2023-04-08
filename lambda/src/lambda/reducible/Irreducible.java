package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lambda.Reducible;
import lambda.TypeMismatch;

public class Constant implements Reducible {

	private final String value;

	private final Constant type;

	public Constant(final String value, final Constant type) {
		this.value = value;
		this.type = type;
	}

	public Constant(final String value) {
		this(value, null);
	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Constant type() {
		assert Objects.nonNull(type) : "can not get type of '"+value+"'";
		return type;
	}

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable, String> names) {
		return value.toString();
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return false;
	}

	@Override
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		return this;
	}

	@Override
	public Reducible reduce() {
		return this;
	}

}
