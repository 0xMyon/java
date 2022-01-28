package fsm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Type;
import util.Tuple;


class State<T,TYPE extends Type<TYPE,T>,R> {

	private final Machine<T,TYPE,R> machine;
	
	private final int id;
	
	
	State(Machine<T,TYPE,R> machine) {
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
	
	
	public String toString() {
		return "<"+(isInitial()?"I":"")+id+(isFinal()?"F":"")+(isUnreachable()?"U":"")+">";
	}
	
	
	boolean isSource(Transition<T,TYPE,R> t) {
		return equals(t.source());
	}
	
	boolean isSourceNoLoop(Transition<T,TYPE,R> t) {
		return isSource(t) && !isTarget(t);
	}
	
	boolean isLoop(Transition<T,TYPE,R> t) {
		return isSource(t) && isTarget(t);
	}
	
	
	boolean isTarget(Transition<T,TYPE,R> t) {
		return equals(t.target());
	}
	
	boolean isTargetNoLoop(Transition<T,TYPE,R> t) {
		return isTarget(t) && !isSource(t);
	}
	
	
	public Stream<Transition<T,TYPE,R>> loop() {
		return machine.transitions().stream().filter(this::isLoop).collect(Collectors.toSet()).stream();
	}
	
	Stream<Transition<T,TYPE,R>> next() {
		return next(true);
	}
	
	Stream<Transition<T,TYPE,R>> next(boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isSource : this::isSourceNoLoop).collect(Collectors.toSet()).stream();
	}
	
	Stream<TYPE> nextSymbols() {
		return next().map(Transition::value).distinct();
	}
	
	Stream<State<T,TYPE,R>> next(T value) {
		return next().filter(t -> t.value().contains(value)).map(Transition::target);
	}
	
	Stream<State<T,TYPE,R>> next(TYPE value) {
		return next().filter(t -> t.value().containsAll(value)).map(Transition::target);
	}
	
	public Stream<Tuple<State<T,TYPE,R>,List<R>>> next(T value, List<R> output) {
		return next().filter(t -> t.value().contains(value)).map(t -> 
			Tuple.of(t.target(), Stream.concat(output.stream(), t.result().stream()).collect(Collectors.toList()))
		);
	}	
	
	Stream<Transition<T,TYPE,R>> prev() {
		return prev(true);
	}
	
	Stream<Transition<T,TYPE,R>> prev(boolean withLoop) {
		return machine.transitions().stream().filter(withLoop ? this::isTarget : this::isTargetNoLoop).collect(Collectors.toSet()).stream();
	}
	
	
	boolean remove() {
		if (isInitial()) 
			throw new NullPointerException();
		machine.transitions().removeIf( 
			t -> {
				try {
					return t.source().equals(this) || t.target().equals(this);
				} catch (Exception e) {
					return false;
				}
			}
		);
		machine.finals().remove(this);
		return machine.states().remove(this);
	}
		
	void combine(State<T,TYPE,R> that) {
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
