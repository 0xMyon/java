package fsm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Type;
import util.Tuple;


class State<T,R> {

	private final Machine<T,R> machine;

	private final int id;


	State(final Machine<T,R> machine) {
		this.machine = machine;
		this.id = machine.ID();
	}


	boolean isFinal() {
		return machine.finals().contains(this) || (isInitial() && machine.hasEpsilon());
	}

	boolean nonFinal() {
		return !isFinal();
	}

	boolean isInitial() {
		return this == machine.initial();
	}


	@Override
	public String toString() {
		return "<"+(isInitial()?"I":"")+id+(isFinal()?"F":"")+(isUnreachable()?"U":"")+">";
	}


	boolean isSource(final Transition<T,R> t) {
		return equals(t.source());
	}

	boolean isSourceNoLoop(final Transition<T,R> t) {
		return isSource(t) && !isTarget(t);
	}

	boolean isLoop(final Transition<T,R> t) {
		return isSource(t) && isTarget(t);
	}


	boolean isTarget(final Transition<T,R> t) {
		return equals(t.target());
	}

	boolean isTargetNoLoop(final Transition<T,R> t) {
		return isTarget(t) && !isSource(t);
	}


	public Stream<Transition<T,R>> loop() {
		return machine.transitions().stream().filter(this::isLoop).collect(Collectors.toSet()).stream();
	}

	Stream<Transition<T,R>> next() {
		return next(true);
	}

	Stream<Transition<T,R>> next(final boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isSource : this::isSourceNoLoop).collect(Collectors.toSet()).stream();
	}

	Stream<Type<?, T>> nextSymbols() {
		return next().map(Transition::value).distinct().map(x->x);
	}

	Stream<State<T,R>> next(final T value) {
		return next().filter(t -> t.value().contains(value)).map(Transition::target);
	}

	Stream<State<T,R>> next(final Type<?, T> value) {
		return next().filter(t -> t.value().containsAll_Type(value)).map(Transition::target);
	}

	public Stream<Tuple<State<T,R>,List<R>>> next(final T value, final List<R> output) {
		return next().filter(t -> t.value().contains(value)).map(t ->
		Tuple.of(t.target(), Stream.concat(output.stream(), t.result().stream()).collect(Collectors.toList()))
				);
	}

	Stream<Transition<T,R>> prev() {
		return prev(true);
	}

	Stream<Transition<T,R>> prev(final boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isTarget : this::isTargetNoLoop).collect(Collectors.toSet()).stream();
	}


	boolean remove() {
		if (isInitial())
			throw new NullPointerException();
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
		that.next().forEach(t -> machine.transition(this, t.value(), t.target()));
		that.prev().forEach(t -> machine.transition(t.source(), t.value(), this));
		that.remove();
	}


	boolean isUnreachable() {
		return !isInitial() && (isLastButNotFinal() || isFirst());
	}

	private boolean isLastButNotFinal() {
		return !isFinal() && next().allMatch(t -> t.target() == this);
	}
	private boolean isFirst() {
		return prev().allMatch(t -> t.source() == this);
	}





}
