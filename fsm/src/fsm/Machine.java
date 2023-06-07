package fsm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;
import lang.Type;
import set.ComplementSet;
import set.FiniteSet;
import util.BooleanOperator;
import util.Sets;
import util.Tuple;

/**
 * Representation of
 * @param <T>
 * @param <R>
 */
public class Machine<T,R, TYPE extends Type<TYPE, T>> implements Language.Naive<Machine<T,R,TYPE>, T, TYPE>, Function<List<T>, List<R>> {

	/**
	 * indication if the empty word is contained
	 */
	private final boolean epsilon;

	/**
	 * {@link Factory} of underlying typesystem
	 */
	private final Type.Factory<TYPE, T> factory;

	private int id = 0;

	private final Set<State<T,R,TYPE>> states = new HashSet<>();
	private final State<T,R,TYPE> initial = state();
	private final Set<State<T,R,TYPE>> finals = new HashSet<>();
	private final Set<Transition<T,R,TYPE>> transitions = new HashSet<>();


	int ID() {
		return id++;
	}

	@Override
	public boolean hasEpsilon() {
		return epsilon;
	}

	protected Machine(final Type.Factory<TYPE, T> factory, final boolean epsilon) {
		this.factory = factory;
		this.epsilon = epsilon;
	}
	
	protected Machine(final Type.Factory<TYPE, T> factory, final TYPE type) {
		this(factory, false);
		transition(initial, type, state(true));
	}

	@SafeVarargs
	protected Machine(final Type.Factory<TYPE, T> factory, final T... values) {
		this(factory, false);
		State<T, R, TYPE> last = initial;
		for(int i = 0; i < values.length; i++) {
			State<T,R,TYPE> next = state(i == values.length - 1);
			transition(last, factory.summand(values[i]), next);
			last = next;
		}
	}
	
	/**
	 * @return new non-final {@link State}
	 */
	private State<T,R,TYPE> state() {
		return state(false);
	}

	/**
	 * @param isFinal
	 * @return new {@link State}
	 */
	private State<T,R,TYPE> state(final boolean isFinal) {
		final State<T,R,TYPE> result = new State<T,R,TYPE>(this);
		states.add(result);
		if (isFinal)
			finals.add(result);
		return result;
	}

	/**
	 * Add a {@link Transition} between two states
	 * @param source {@link State} of the {@link Transition}
	 * @param type that matches an input-element <T>
	 * @param target {@link State} of the {@link Transition}
	 */
	void transition(final State<T,R,TYPE> source, final TYPE type, final State<T,R,TYPE> target) {
		if (type.isEmpty())	return; // TODO this check should not be done
		final Optional<Transition<T,R,TYPE>> tx = transitions.stream().filter(t->t.source()==source && t.target()==target).findFirst();
		tx.ifPresentOrElse(t -> {
			transitions.remove(t);
			transitions.add(new Transition<T,R,TYPE>(source, type.unite(t.type()), target, t.result())); // TODO alter the type?
		}, ()-> transitions.add(new Transition<T,R,TYPE>(source, type, target)));
	}

	/**
	 * @param predicate
	 * @return {@link Stream} of {@link Transition} that match a given predicate
	 */
	private Stream<Transition<T,R,TYPE>> transitions(final Predicate<Transition<T,R,TYPE>> predicate) {
		return transitions.stream().filter(predicate).collect(Collectors.toSet()).stream();
	}

	/**
	 * create a new epsilon-{@link Transition} between two {@link State}
	 * @param source
	 * @param target
	 */
	private void transition(final State<T,R,TYPE> source, final State<T,R,TYPE> target) {
		transitions(target::isLoop).forEach(t->transition(source, t.type(), source));
		transitions(source::isLoop).forEach(t->transition(target, t.type(), target));
		transitions(target::isSourceNoLoop).forEach(t->transition(source, t.type(), t.target()));
		transitions(source::isTargetNoLoop).forEach(t->transition(t.source(), t.type(), target));
	}

	/**
	 * create new epsilon transitions between {@link State}s
	 * @param sources
	 * @param target
	 */


	private void transition(final Set<State<T,R,TYPE>> sources, final State<T,R,TYPE> target) {
		sources.stream().forEach(source->transition(source,target));
	}

	private void transition(final State<T,R,TYPE> source, final Set<State<T,R,TYPE>> targets) {
		targets.stream().forEach(target->transition(source,target));
	}


	/**
	 * include a {@link Machine} with its {@link State} and {@link Transition}
	 * @param that
	 * @return {@link Map} from {@link State} of that to {@link State} of this
	 * @see Machine#include(Machine, Function)
	 */
	private Map<State<T,R,TYPE>,State<T,R,TYPE>> include(final Machine<T,R,TYPE> that) {
		return clone(that.states.stream(), that.initial, State::isFinal);
	}

	/**
	 * include a {@link Machine} with its {@link State} and {@link Transition} given a {@link Type} conversion
	 * @param that
	 * @param f {@link Type} conversion
	 * @return {@link Map} from {@link State} of that to {@link State} of this
	 */
	private <X, XTYPE extends Type<XTYPE,X>>
	Map<State<X,R,XTYPE>,State<T,R,TYPE>> include(final Machine<X,R,XTYPE> that, final Function<XTYPE, TYPE> f) {
		final Map<State<X,R,XTYPE>, State<T,R,TYPE>> map = new HashMap<>();
		that.states.stream().forEach(s->map.put(s,state()));
		that.transitions.stream().forEach(t->transition(
				map.get(t.source()), f.apply(t.type()), map.get(t.target()))
				);
		return map;
	}



	private <X> Map<X, State<T,R,TYPE>> include(final Stream<X> stream, final X initial, final Predicate<X> isFinal) {
		final Map<X, State<T,R,TYPE>> map = new HashMap<>();
		stream.filter(s -> !Objects.equals(s, initial)).forEach(s -> map.put(s, state(isFinal.test(s))));
		map.put(initial, this.initial);
		return map;
	}

	private
	Map<State<T,R,TYPE>, State<T,R,TYPE>> clone(final Stream<State<T,R,TYPE>> stream, final State<T,R,TYPE> initial, final Predicate<State<T,R,TYPE>> isFinal) {
		final Map<State<T,R,TYPE>, State<T,R,TYPE>> map = this.include(stream, initial, isFinal);
		map.forEach((a,b) -> a.next().forEach(t -> transition(b, t.type(), map.get(t.target()))));
		return map;
	}


	@Override
	public Machine<T,R,TYPE> concat(final Machine<T,R,TYPE> that) {
		final Machine<T,R,TYPE> result = new Machine<>(factory, this.hasEpsilon() && that.hasEpsilon());

		final Map<Tuple<State<T,R,TYPE>,State<T,R,TYPE>>,State<T,R,TYPE>> map = result.include(
				Stream.concat(states.stream().map(Tuple::left), that.states.stream().map(Tuple::right)),
				Tuple.left(initial),
				t -> Objects.nonNull(t.right) && t.right.isFinal()
				);


		map.forEach((k,v) -> {
			if (k.left != null)
				k.left.next().forEach(t -> result.transition(v, t.type(), map.get(Tuple.left(t.target()))));
			else
				k.right.next().forEach(t -> result.transition(v, t.type(), map.get(Tuple.right(t.target()))));
		});
		//Map<State<T,TYPE,R>,State<T,TYPE,R>> s1 = result.include(that);

		// F1 -> I2
		result.transition(
				this.finals().stream().map(Tuple::left).map(map::get).collect(Collectors.toSet()),
				map.get(Tuple.right(that.initial()))
				);

		// special case when epsilon is involved
		if (this.epsilon) {
			// I -> I2
			result.transition(
					result.initial(),
					map.get(Tuple.right(that.initial()))
					);
		}
		if (that.epsilon) {
			// F1 -> F
			result.transition(
					this.finals().stream().map(Tuple::left).map(map::get).collect(Collectors.toSet()),
					result.state(true)
					);
		}

		return result.determinize();
	}

	private Machine<T,R,TYPE> operation(final Machine<T,R,TYPE> that, final BooleanOperator op) {
		final Machine<T,R,TYPE> result = new Machine<>(factory, op.apply(this.hasEpsilon(), that.hasEpsilon()));
		final Map<Tuple<State<T,R,TYPE>,State<T,R,TYPE>>, State<T,R,TYPE>> map = result.include(
				Sets.product(
						Stream.concat(this.states.stream(), Stream.of((State<T,R,TYPE>)null)),
						Stream.concat(that.states.stream(), Stream.of((State<T,R,TYPE>)null))
						),
				Tuple.of(this.initial(), that.initial()),
				t -> op.apply(Objects.nonNull(t.left) && t.left.isFinal(), Objects.nonNull(t.right) && t.right.isFinal())
				);

		// for each power-state
		map.entrySet().forEach(e -> {
			final Tuple<State<T,R,TYPE>,State<T,R,TYPE>> source = e.getKey();
			final Set<TYPE> inputs = Stream.of(source.left, source.right).filter(Objects::nonNull)
					.map(State::nextSymbols)
					.reduce(Stream.of(), Stream::concat)
					.distinct()
					.collect(Collectors.toSet());

			// for each partition of the states input
			Type.partition(inputs).forEach(x -> {
				if (Objects.isNull(source.left) || source.left.next(x).noneMatch(t1 -> {
					if (Objects.isNull(source.right) || source.right.next(x).noneMatch(t2 -> {
						result.transition(map.get(source), x, map.get(Tuple.of(t1, t2)));
						return true;
					})) {
						result.transition(map.get(source), x, map.get(Tuple.of(t1, null)));
					}
					return true;
				})) {
					if(Objects.isNull(source.right) || source.right.next(x).noneMatch(t2 -> {
						result.transition(map.get(source), x, map.get(Tuple.of(null, t2)));
						return true;
					})) {
						result.transition(map.get(source), x, map.get(Tuple.of(null, null)));
					}
				}
			});

			result.transition(
					map.get(source),
					factory.universe().minus(inputs.stream().reduce(factory.empty(), Type::unite)),
					map.get(Tuple.of(null, null))
					);
		});


		return result.determinize();
	}

	@Override
	public Machine<T,R,TYPE> unite(final Machine<T,R,TYPE> that) {
		return operation(that, BooleanOperator.disjunction);
	}

	@Override
	public Machine<T,R,TYPE> intersect(final Machine<T,R,TYPE> that) {
		return operation(that, BooleanOperator.conjunction);
	}

	@Override
	public Machine<T,R,TYPE> minus(final Machine<T,R,TYPE> that) {
		return operation(that, BooleanOperator.abjunction);
	}

	public Machine<T,R,TYPE> xor(final Machine<T,R,TYPE> that) {
		return operation(that, BooleanOperator.antivalence);
	}
	
	@Override
	public Machine<T,R,TYPE> implies(final Machine<T,R,TYPE> that) {
		return operation(that, BooleanOperator.implication);
	}

	@Override
	public Machine<T,R,TYPE> parallel(final Machine<T,R,TYPE> that) {
		final Machine<T,R,TYPE> result = new Machine<>(factory, this.hasEpsilon() && that.hasEpsilon());
		final Map<Tuple<State<T,R,TYPE>,State<T,R,TYPE>>, State<T,R,TYPE>> map = result.include(
				Sets.product(this.states.stream(), that.states.stream()),
				Tuple.of(this.initial(), that.initial()),
				t -> t.left.isFinal() && t.right.isFinal()
				);

		// for each power-state
		map.entrySet().forEach(e -> {
			final Tuple<State<T,R,TYPE>,State<T,R,TYPE>> source = e.getKey();
			source.left.next().forEach(t ->
			result.transition(map.get(source), t.type(), map.get(Tuple.of(t.target(), source.right)))
					);
			source.right.next().forEach(t ->
			result.transition(map.get(source), t.type(), map.get(Tuple.of(source.left, t.target())))
					);
		});

		return result.determinize();
	}

	@Override
	public Machine<T,R,TYPE> iterate() {
		final Machine<T,R,TYPE> result = new Machine<>(factory, this.hasEpsilon());
		final Map<State<T,R,TYPE>,State<T,R,TYPE>> s0 = result.include(this);

		// F1 -> I1
		result.transition(
				this.finals.stream().map(s0::get).collect(Collectors.toSet()),
				s0.get(this.initial)
				);

		return result.determinize();
	}


	@Override
	public Machine<T,R,TYPE> optional() {
		final Machine<T,R,TYPE> result = new Machine<>(factory, true);

		result.include(this);


		return result.determinize();
	}


	@Override
	public Machine<T,R,TYPE> complement() {
		final Machine<T,R,TYPE> result = new Machine<>(factory, !hasEpsilon());
		result.clone(states.stream(), initial, State::nonFinal);


		// new F
		final State<T,R,TYPE> fin = result.state(true);

		// S -> F with input completion
		result.states().forEach(s -> result.transition(
				s,
				s.nextSymbols().reduce(factory.empty(), Type::unite).complement(),
				//factory.union(s.nextSymbols()).complement(),
				fin
				));

		return result.determinize();
	}






	/**
	 * @return a new deterministic machine that is equivalent to this
	 */
	private Machine<T,R,TYPE> determinize() {

		// initial cleanup to reduce states
		removeUnreachable();

		//System.out.println("rem -> "+this);

		final Machine<T,R,TYPE> result = new Machine<>(factory, epsilon);
		final Map<Set<State<T,R,TYPE>>, State<T,R,TYPE>> map = result.include(
				Sets.power(this.states()).stream(),
				Sets.of(this.initial()),
				x -> x.stream().anyMatch(State::isFinal)
				);

		// for each power-state
		map.entrySet().forEach(e -> {
			final Set<State<T,R,TYPE>> source = e.getKey();
			final Set<TYPE> inputs = source.stream()
					.map(State::nextSymbols)
					.reduce(Stream.of(), Stream::concat)
					.distinct()
					.collect(Collectors.toSet());

			// for each partition of the states input
			Type.partition(inputs).stream().forEach(x -> {
				final Set<State<T,R,TYPE>> target = source.stream()
						.map(s -> s.next(x))
						.reduce(Stream.of(), Stream::concat)
						.collect(Collectors.toSet());

				// set the power-transition
				result.transition(map.get(source), x, map.get(target));
			});
		});

		return result.removeUnreachable().identify();
	}


	/**
	 * remove unreachable {@link State}
	 */
	private Machine<T, R, TYPE> removeUnreachable() {
		while(states.removeIf(State::isUnreachable)) {
			finals.removeIf(f -> !states.contains(f));
			transitions.removeIf(t -> !states.contains(t.source()) || !states.contains(t.target()));
		}
		// remove loop on initial when it's not an final (e.a. epsilon is not contained)
		if (!epsilon && states.size() == 1) transitions.clear();
		return this;
	}

	/**
	 * identify equal {@link State}
	 * it is required to be deterministic before calling this function
	 */
	private Machine<T, R, TYPE> identify() {

		// combine states
		final Set<Tuple<State<T,R,TYPE>, State<T,R,TYPE>>> equalent = new HashSet<>();
		states.stream().forEach(P -> {
			states.stream().filter(Q -> Q.hashCode() < P.hashCode() && !Q.equals(P)).forEach(Q -> {
				if ((P.isFinal() && Q.isFinal()) || (!P.isFinal() && !Q.isFinal())) {
					equalent.add(Tuple.of(P, Q));
				}
			});
		});

		// remove unequal pairs
		while(equalent.removeIf(current -> {
			final State<T,R,TYPE> P = current.left;
			final State<T,R,TYPE> Q = current.right;

			// for all inputs in sigma: check if transitions end up on the same states
			return Type.partition(Stream.concat(P.nextSymbols(), Q.nextSymbols()).collect(Collectors.toSet())).stream()
					.anyMatch(c -> {
						final State<T,R,TYPE> PT = P.next(c).findAny().orElse(null);
						final State<T,R,TYPE> QT = Q.next(c).findAny().orElse(null);
						return !Objects.equals(PT, QT) && !equalent.contains(Tuple.of(PT, QT));
					});

		})) {};

		// combine remaining states
		equalent.stream().forEach(current -> current.apply(State::combine));

		return removeUnreachable();
	}

	@Override
	public String toString() {
		return transitions.toString()+(hasEpsilon()?"+e":"");
	}

	@Override
	public boolean contains(final Stream<T> word) {
		return contains(word.collect(Collectors.toList()));
	}

	@Override
	public boolean contains(final List<T> word) {
		if (epsilon && word.isEmpty())
			return true;
		Set<State<T,R,TYPE>> states = new HashSet<>();
		states.add(initial);
		for(final T t : word) {
			states = states.stream().map(s -> s.next(t)).reduce(Stream.of(), Stream::concat).collect(Collectors.toSet());
		}
		return states.stream().anyMatch(State::isFinal);
	}

	@Override
	public List<R> apply(final List<T> word) {

		Set<Tuple<State<T,R,TYPE>,List<R>>> states = new HashSet<>();
		states.add(Tuple.of(initial, new LinkedList<R>()));
		for(final T t : word) {
			states = states.stream().map(s -> s.left.next(t, s.right)).reduce(Stream.of(), Stream::concat).collect(Collectors.toSet());
		}

		return states.stream().filter(s->s.left.isFinal()).findAny().get().right;
	}


	@Override
	public boolean containsAll(final Machine<T,R,TYPE> that) {

		if (that.hasEpsilon() && !this.hasEpsilon())
			return false;

		final Map<State<T,R,TYPE>, State<T,R,TYPE>> map = new HashMap<>();

		map.put(that.initial(), this.initial());

		final Map<State<T,R,TYPE>, State<T,R,TYPE>> current = new HashMap<>();
		final Map<State<T,R,TYPE>, State<T,R,TYPE>> next = new HashMap<>();

		next.putAll(map);

		do {

			current.clear();
			current.putAll(next);
			next.clear();

			for(final Entry<State<T,R,TYPE>, State<T,R,TYPE>> pair : current.entrySet()) {

				//Type.partition(Stream.concat(pair.getKey().nextSymbols(), pair.getValue().nextSymbols()))

				if (!pair.getKey().next().allMatch(t1 -> {
					if (pair.getValue().next(t1.type()).count() == 0) {
						//System.out.println("not found "+t1);
						return false;
					}
					return pair.getValue().next(t1.type()).anyMatch(t2 -> {

						if (map.containsKey(t1.target()) && !map.get(t1.target()).equals(t2)) {
							//System.out.println(t1.target()+"!="+t2);
							return false;
						}
						if (t1.target().isFinal() && !t2.isFinal()) {
							//System.out.println("!final");
							return false;
						}
						if (!map.containsKey(t1.target())) {
							next.put(t1.target(), t2);
							map.put(t1.target(), t2);
						}
						return true;
					});
				})) {
					return false;
				}

			}
			//System.out.println("next: "+next+" "+next.size());
		} while (!next.isEmpty());

		return true;
	}

	/**
	 * @return the initial {@link State}
	 */
	public State<T,R,TYPE> initial() {
		return initial;
	}

	/**
	 * @return all final {@link State}s
	 */
	public Set<State<T,R,TYPE>> finals() {
		return finals;
	}

	/**
	 *
	 * @return all {@link Transition}s
	 */
	Collection<Transition<T,R,TYPE>> transitions() {
		return transitions;
	}

	/**
	 * @return all {@link State}s
	 */
	Set<State<T,R,TYPE>> states() {
		return states;
	}



	@Override
	public Machine<T,R,TYPE> THIS() {
		return this;
	}

	@Override
	public boolean isEmpty() {
		return transitions.isEmpty() && !hasEpsilon();
	}

	@Override
	public boolean isEpsilon() {
		return transitions.isEmpty() && hasEpsilon();
	}

	@Override
	public <THAT extends Language<THAT, U, ULIST, TYPE2>, U, ULIST, TYPE2 extends Type<TYPE2, U>, FACTORY extends Language.Factory<THAT, U, ULIST, TYPE2>> 
	THAT convert(final FACTORY factory, Function<TYPE, TYPE2> FUNCTION) {
		final Machine<ULIST, R, THAT> result = new Machine<>(factory);

		final Map<State<T,R,TYPE>, State<ULIST,R,THAT>> s1 = 
				result.include(this, FUNCTION.andThen(factory::letter));
		
		result.transition(result.initial, factory.epsilon(), s1.get(this.initial));

		var newFinal = result.state(true);
		
		if (hasEpsilon())
			result.transition(s1.get(initial()), factory.epsilon(), newFinal);
		
		this.finals().stream().map(s1::get).forEach(f -> 
			result.transition(f, factory.epsilon(), newFinal)
		);

		result.states.stream().filter(State::isNormal).forEach(s -> {
			s.prev(false).forEach(left -> {
				s.next(false).forEach(right -> {
					result.transition(
						left.source(), 
						left.type().concat(s.loop().orElse(factory.epsilon()).star()).concat(right.type()), 
						right.target()
					);
				});
			});
			result.transitions.removeIf(t -> s.isSource(t) || s.isTarget(t));
		});
		result.removeUnreachable();
		
		
		return result.transitions().stream().findFirst().map(Transition::type).orElse(factory.empty());
	}

	public static class Factory<T, R, TYPE extends Type<TYPE,T>> implements Language.Naive.Factory<Machine<T,R,TYPE>, T, TYPE> {

		public Factory(final Type.Factory<TYPE, T> factory) {
			this.factory = factory;
		}

		private final Type.Factory<TYPE, T> factory;

		@Override
		public Machine<T,R,TYPE> empty() {
			return new Machine<>(factory, false);
		}
		@Override
		public Machine<T,R,TYPE> epsilon() {
			return new Machine<>(factory, true);
		}
		@Override
		public Machine<T, R, TYPE> factor(final T that) {
			return new Machine<>(factory, that);
		}
		@Override
		public Machine<T, R, TYPE> letter(TYPE type) {
			return new Machine<>(factory, type);
		}
		
		@Override
		public Type.Factory<TYPE, T> alphabet() {
			return factory;
		}
		
	}



	@Override
	public Machine<T,R,TYPE> reverse() {

		final Machine<T,R,TYPE> result = new Machine<>(factory, hasEpsilon());

		final Map<State<T,R,TYPE>, State<T,R,TYPE>> s0 = result.include(states.stream(), null, State::isInitial);

		s0.forEach((a,b) -> {
			if (a != null) a.next().forEach(t -> result.transition(s0.get(t.target()), t.type(), b));
		});

		// null -> F1
		result.transition(
				s0.get(null),
				this.finals.stream().map(s0::get).collect(Collectors.toSet())
				);

		return result.determinize();

	}
	
	public static <T> Factory<T, Void, ComplementSet<T, FiniteSet<T>>> FACTORY() {
		return new Factory<>(new ComplementSet.Factory<>(new FiniteSet.Factory<>()));
	}

	@Override
	public Factory<T,R,TYPE> factory() {
		return new Factory<>(factory);
	}

	@Override
	public boolean isFinite() {
		return transitions.stream().map(Transition::type).allMatch(Type::isFinite) && !hasLoops();
	}

	private boolean hasLoops() {
		final Set<State<T,R,TYPE>> visited = new HashSet<>();
		Set<State<T,R,TYPE>> current = new HashSet<>();
		current.add(initial);
		visited.add(initial);
		while(!current.isEmpty()) {
			current = current.stream().map(s -> s.next().map(Transition::target)).reduce(Stream.of(), Stream::concat).collect(Collectors.toSet());
			if (current.stream().anyMatch(visited::contains)) {
				return true;
			}
			visited.addAll(current);
		}
		return false;
	}

	public List<T> random(Random random) {
		if (isEmpty()) throw new RuntimeException("empty");
		List<T> word = new Vector<>();
		State<T, R, TYPE> state = initial();
		while((!state.isFinal() || random.nextBoolean()) && state.next().count() != 0) {
			var ts = state.next().collect(Collectors.toSet());
			var t = ts.stream().skip(random.nextInt(ts.size())).findFirst().orElseThrow();
			word.add(t.type().random(random));
			state = t.target();
		}
		return word;
	}




}
