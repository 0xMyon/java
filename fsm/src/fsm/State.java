package fsm;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Type;
import util.Tuple;

/**
 * State of {@link Machine}
 * @param <T> input {@link Type}
 * @param <R> output type
 */
public class State<T,R,TYPE extends Type<TYPE, T>> {

	/**
	 * Owning {@link Machine}
	 */
	private final Machine<T,R,TYPE> machine;

	private final int id;

	protected final Set<Transition<T,R,TYPE>> next = new HashSet<>();
	protected final Set<Transition<T,R,TYPE>> prev = new HashSet<>();


	State(final Machine<T,R,TYPE> machine) {
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

	boolean isNormal() {
		return !isInitial() && !isFinal();
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the source {@link State} of t
	 * @see Transition#source()
	 */
	boolean isSource(final Transition<T,R,TYPE> t) {
		return equals(t.source());
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the source {@link State} of t and not a loop on t
	 * @see Transition#source()
	 */
	boolean isSourceNoLoop(final Transition<T,R,TYPE> t) {
		return isSource(t) && !isTarget(t);
	}

	/**
	 * @param t {@link Transition}
	 * @return true, this is a loop on t
	 * @see Transition#source()
	 */
	boolean isLoop(final Transition<T,R,TYPE> t) {
		return isSource(t) && isTarget(t);
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the target {@link State} of t
	 * @see Transition#target()
	 */
	boolean isTarget(final Transition<T,R,TYPE> t) {
		return equals(t.target());
	}

	boolean nonTarget(final Transition<T,R,TYPE> t) {
		return !isTarget(t);
	}

	boolean nonSource(final Transition<T,R,TYPE> t) {
		return !isSource(t);
	}

	/**
	 * @param t {@link Transition}
	 * @return true, if this is the target {@link State} of t and not a loop on t
	 * @see Transition#target()
	 */
	boolean isTargetNoLoop(final Transition<T,R,TYPE> t) {
		return isTarget(t) && !isSource(t);
	}

	/**
	 * @return the {@link Type} annotated on a loop of this {@link State}
	 * @see #isLoop(Transition)
	 */
	public Optional<TYPE> loop() {
		return machine.transitions().stream().filter(this::isLoop).findFirst().map(Transition::type);
	}

	/**
	 * @return {@link Stream} of succeeding {@link Transition} with loop
	 */
	Stream<Transition<T,R,TYPE>> next() {
		return next.stream();
	}

	/**
	 * @param withLoop enable loops
	 * @return {@link Stream} of succeeding {@link Transition}
	 */
	Stream<Transition<T,R,TYPE>> nextNoLoop() {
		return next().filter(this::nonTarget).collect(Collectors.toSet()).stream();
	}

	/**
	 * @return {@link Stream} of {@link Type} annotated in succeeding {@link Transition}
	 */
	Stream<TYPE> nextSymbols() {
		return next().map(Transition::type).distinct();
	}

	/**
	 * @param value specific input
	 * @return {@link Stream} succeeding {@link State} that are reachable with input value
	 */
	Stream<State<T,R,TYPE>> next(final T value) {
		return next().filter(t -> t.type().contains(value)).map(Transition::target);
	}

	/**
	 * @param type input {@link Type}
	 * @return {@link Stream} succeeding {@link State} that are reachable with input type
	 */
	Stream<State<T,R,TYPE>> next(final TYPE type) {
		return next().filter(t -> t.type().containsAll(type)).map(Transition::target);
	}


	public Stream<Tuple<State<T,R,TYPE>,List<R>>> next(final T value, final List<R> output) {
		return next().filter(t -> t.type().contains(value)).map(t ->
		Tuple.of(t.target(), Stream.concat(output.stream(), t.result().stream()).collect(Collectors.toList()))
				);
	}

	Stream<Transition<T,R,TYPE>> prev() {
		return prev.stream();
	}

	Stream<Transition<T,R,TYPE>> prevNoLoop() {
		return prev().filter(this::nonSource).collect(Collectors.toSet()).stream();
	}

	/**
	 * removes this {@link State} from the {@link Machine}
	 * @return
	 * @throws IllegalStateException if this is the initial {@link State}
	 */
	boolean remove(final boolean inRemoveIf) {
		if (isInitial())
			throw new IllegalStateException();

		next.forEach(t -> {
			if (nonTarget(t)) t.target().prev.remove(t);
			machine.transitions().remove(t);
		});

		prev.forEach(t -> {
			if (nonSource(t)) t.source().next.remove(t);
			machine.transitions().remove(t);
		});

		machine.finals().remove(this);

		if (!inRemoveIf)
			machine.states().remove(this);

		return true;
	}

	boolean isValid() {
		return machine.states().contains(this);
	}

	void combine(final State<T,R,TYPE> that) {
		if (isValid() && that.isValid()) {

			if (that.isInitial()) {
				that.combine(this);
				return;
			}
			that.next().forEach(t -> machine.transition(this, t.type(), t.target()));
			that.prev().forEach(t -> machine.transition(t.source(), t.type(), this));
			that.remove(false);
		}
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
