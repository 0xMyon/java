package fsm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Type;
import util.Tuple;

/**
 * State of {@link Machine}
 * @param <T> input {@link Type}
 * @param <R> output type
 */
class State<T,R> {

	/**
	 * Owning {@link Machine}
	 */
	private final Machine<T,R> machine;

	private final int id;


	State(final Machine<T,R> machine) {
		this.machine = machine;
		this.id = machine.ID();
	}


	/**
	 * @return true, if this is a final state
	 * @see Machine#finals()
	 */
	boolean isFinal() {
		return machine.finals().contains(this) || (isInitial() && machine.hasEpsilon());
	}

	/**
	 * @return true, if this is non-final {@link State}
	 * @see Machine#finals()
	 */
	boolean nonFinal() {
		return !isFinal();
	}

	/**
	 * @return true, if this is the initial {@link State}
	 * @see Machine#initial()
	 */
	boolean isInitial() {
		return this == machine.initial();
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the source {@link State} of t
	 * @see Transition#source()
	 */
	boolean isSource(final Transition<T,R> t) {
		return equals(t.source());
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the source {@link State} of t and not a loop on t
	 * @see Transition#source()
	 */
	boolean isSourceNoLoop(final Transition<T,R> t) {
		return isSource(t) && !isTarget(t);
	}

	/**
	 * @param t {@link Transition}
	 * @return true, this is a loop on t
	 * @see Transition#source()
	 */
	boolean isLoop(final Transition<T,R> t) {
		return isSource(t) && isTarget(t);
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the target {@link State} of t
	 * @see Transition#target()
	 */
	boolean isTarget(final Transition<T,R> t) {
		return equals(t.target());
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the target {@link State} of t and not a loop on t
	 * @see Transition#target()
	 */
	boolean isTargetNoLoop(final Transition<T,R> t) {
		return isTarget(t) && !isSource(t);
	}

	/**
	 * @return the {@link Type} annotated on a loop of this {@link State}
	 * @see #isLoop(Transition)
	 */
	public Optional<Type<?,T>> loop() {
		return machine.transitions().stream().filter(this::isLoop).findFirst().map(Transition::type);
	}

	/**
	 * @return {@link Stream} of succeeding {@link Transition} with loop
	 */
	Stream<Transition<T,R>> next() {
		return next(true);
	}

	/**
	 * @param withLoop enable loops
	 * @return {@link Stream} of succeeding {@link Transition}
	 */
	Stream<Transition<T,R>> next(final boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isSource : this::isSourceNoLoop).collect(Collectors.toSet()).stream();
	}

	/**
	 * @return {@link Stream} of {@link Type} annotated in succeeding {@link Transition}
	 */
	Stream<Type<?, T>> nextSymbols() {
		return next().map(Transition::type).distinct().map(x->x);
	}

	/**
	 * @param value specific input
	 * @return {@link Stream} succeeding {@link State} that are reachable with input value
	 */
	Stream<State<T,R>> next(final T value) {
		return next().filter(t -> t.type().contains(value)).map(Transition::target);
	}

	/**
	 * @param type input {@link Type}
	 * @return {@link Stream} succeeding {@link State} that are reachable with input type
	 */
	Stream<State<T,R>> next(final Type<?, T> type) {
		return next().filter(t -> t.type().containsAll_Type(type)).map(Transition::target);
	}


	public Stream<Tuple<State<T,R>,List<R>>> next(final T value, final List<R> output) {
		return next().filter(t -> t.type().contains(value)).map(t ->
		Tuple.of(t.target(), Stream.concat(output.stream(), t.result().stream()).collect(Collectors.toList()))
				);
	}

	Stream<Transition<T,R>> prev() {
		return prev(true);
	}

	Stream<Transition<T,R>> prev(final boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isTarget : this::isTargetNoLoop).collect(Collectors.toSet()).stream();
	}

	/**
	 * removes this {@link State} from the {@link Machine}
	 * @return
	 * @throws IllegalStateException if this is the initial {@link State}
	 */
	boolean remove() {
		if (isInitial())
			throw new IllegalStateException();
		machine.transitions().removeIf(
				t -> {
					try {
						return t.source().equals(this) || t.target().equals(this);
					} catch (final Exception e) {
						return false;
					}
				}
				);
		machine.finals().remove(this);
		return machine.states().remove(this);
	}


	void combine(final State<T,R> that) {
		if (that.isInitial()) {
			that.combine(this);
			return;
		}
		//System.out.println(this+"="+that);
		that.next().forEach(t -> machine.transition(this, t.type(), t.target()));
		that.prev().forEach(t -> machine.transition(t.source(), t.type(), this));
		that.remove();
	}

	/**
	 * @return true, if this {@link State} is unreachable from the initial state
	 */
	boolean isUnreachable() {
		return !isInitial() && (isLastButNotFinal() || isFirst());
	}

	/**
	 * @return true, if this {@link State} has no outgoing {@link Transition} and is not a final {@link State}
	 */
	private boolean isLastButNotFinal() {
		return !isFinal() && next().allMatch(t -> t.target() == this);
	}

	/**
	 * @return true, if this {@link State} has no incoming {@link Transition}
	 */
	private boolean isFirst() {
		return prev().allMatch(t -> t.source() == this);
	}




	@Override
	public String toString() {
		return "<"+(isInitial()?"I":"")+id+(isFinal()?"F":"")+(isUnreachable()?"U":"")+">";
	}



}
