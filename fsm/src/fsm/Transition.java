package fsm;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import lang.Type;

/**
 * {@link Type} annotated Transition between two {@link State}s
 * @param <T> type of the input
 * @param <R> type of the output
 */
class Transition<T,R> {

	private final State<T,R> source, target;

	/**
	 * Annotated {@link Type}
	 */
	private final Type<?, T> type;

	private final List<R> result = new LinkedList<>();

	@SafeVarargs
	Transition(final State<T,R> source, final Type<?, T> value, final State<T,R> target, final R... rs) {
		this.source = source;
		this.type = value;
		this.target = target;
		for (final R r : rs) this.result.add(r);
	}

	Transition(final State<T,R> source, final Type<?, T> value, final State<T,R> target, final List<R> rs) {
		this.source = source;
		this.type = value;
		this.target = target;
		this.result.addAll(rs);
	}

	/**
	 * @return source {@link State}
	 */
	State<T,R> source() { return source; }

	/**
	 * @return target {@link State}
	 */
	State<T,R> target() { return target; }

	/**
	 * @return annotated {@link Type}
	 */
	Type<?,T> type() { return type; }

	/**
	 * @return annotated result
	 */
	public List<R> result() {
		return result;
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof Transition) {
			final Transition<?,?> that = (Transition<?,?>) object;
			return Objects.equals(source, that.source) &&
					Objects.equals(target, that.target) &&
					Objects.equals(type, that.type);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(source(), target(), type());
	}

	@Override
	public String toString() {
		return source().toString()+"["+type().toString()+"]"+target().toString();
	}



}