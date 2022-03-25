package fsm;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import lang.Type;

class Transition<T,R> {

	private final State<T,R> source, target;

	private final Type<?, T> value;

	private final List<R> result = new LinkedList<>();

	@SafeVarargs
	Transition(final State<T,R> source, final Type<?, T> value, final State<T,R> target, final R... rs) {
		this.source = source;
		this.value = value;
		this.target = target;
		for (final R r : rs) this.result.add(r);
	}

	Transition(final State<T,R> source, final Type<?, T> value, final State<T,R> target, final List<R> rs) {
		this.source = source;
		this.value = value;
		this.target = target;
		this.result.addAll(rs);
	}

	State<T,R> source() { return source; }

	State<T,R> target() { return target; }

	Type<?,T> value() { return value; }

	@Override
	public boolean equals(final Object object) {
		if (object instanceof Transition) {
			final Transition<?,?> that = (Transition<?,?>) object;
			return Objects.equals(source, that.source) &&
					Objects.equals(target, that.target) &&
					Objects.equals(value, that.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(source(), target(), value());
	}

	@Override
	public String toString() {
		return source().toString()+"["+value().toString()+"]"+target().toString();
	}

	public List<R> result() {
		return result;
	}

}