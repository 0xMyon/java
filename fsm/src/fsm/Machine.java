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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import expr.Expression;
import lang.Language;
import lang.Type;
import util.BooleanOperator;
import util.Sets;
import util.Tuple;

public class Machine<T,TYPE extends Type<TYPE,T>,R> implements Language<Machine<T,TYPE,R>, T> {

	private final boolean epsilon;
	private final Type.Factory<TYPE, T> factory;
	
	private int id = 0;
	
	private final Set<State<T,TYPE,R>> states = new HashSet<>();
	private final State<T,TYPE,R> initial = state();
	private final Set<State<T,TYPE,R>> finals = new HashSet<>();
	private final Set<Transition<T,TYPE,R>> transitions = new HashSet<>();
	
	
	int ID() {
		return id++;
	}
	
	public boolean hasEpsilon() {
		return epsilon;
	}
	
	protected Machine(Type.Factory<TYPE, T> factory, boolean epsilon) {
		this.factory = factory;
		this.epsilon = epsilon;
	}
	
	protected Machine(Type.Factory<TYPE, T> factory, TYPE value) {
		this(factory, false);
		transition(initial, value, state(true));
	}

	/**
	 * @return new non-final {@link State} 
	 */
	private State<T,TYPE,R> state() {
		return state(false);
	}
	
	/**
	 * @param isFinal
	 * @return new {@link State}
	 */
	private State<T,TYPE,R> state(boolean isFinal) {
		State<T,TYPE,R> result = new State<T,TYPE,R>(this);
		states.add(result);
		if (isFinal)
			finals.add(result);
		return result;
	}
	
	void transition(State<T,TYPE,R> source, TYPE value, State<T,TYPE,R> target) {
		if (value.isEmpty())
			return;
		Optional<Transition<T,TYPE,R>> tx = transitions.stream().filter(t->t.source()==source && t.target()==target).findFirst();
		if (tx.isPresent()) {
			Transition<T,TYPE,R> t = tx.get();
			transitions.remove(t);
			transitions.add(new Transition<T, TYPE, R>(source, value.unite(t.value()), target, t.result()));
		} else
			transitions.add(new Transition<T,TYPE,R>(source, value, target));
	}
	
	/**
	 * @param predicate
	 * @return {@link Stream} of {@link Transition} that match a given predicate
	 */
	private Stream<Transition<T,TYPE,R>> transitions(Predicate<Transition<T,TYPE,R>> predicate) {
		return transitions.stream().filter(predicate).collect(Collectors.toSet()).stream();
	}
	
	/**
	 * create a new epsilon-{@link Transition} between two {@link State}
	 * @param source
	 * @param target
	 */
	private void transition(State<T,TYPE,R> source, State<T,TYPE,R> target) {
		transitions(target::isLoop).forEach(t->transition(source, t.value(), source));
		transitions(source::isLoop).forEach(t->transition(target, t.value(), target));
		transitions(target::isSourceNoLoop).forEach(t->transition(source, t.value(), t.target()));
		transitions(source::isTargetNoLoop).forEach(t->transition(t.source(), t.value(), target));
	}
	
	/**
	 * create new epsilon transitions between {@link State}s
	 * @param sources
	 * @param target
	 */
	
	
	private void transition(Set<State<T,TYPE,R>> sources, State<T,TYPE,R> target) {
		sources.stream().forEach(source->transition(source,target));
	}
	
	private void transition(State<T,TYPE,R> source, Set<State<T,TYPE,R>> targets) {
		targets.stream().forEach(target->transition(source,target));
	}
	
	
	/**
	 * include a {@link Machine} with its {@link State} and {@link Transition}
	 * @param that
	 * @return {@link Map} from {@link State} of that to {@link State} of this
	 * @see Machine#include(Machine, Function)
	 */
	private Map<State<T,TYPE,R>,State<T,TYPE,R>> include(Machine<T,TYPE,R> that) {
		return clone(that.states.stream(), that.initial, State::isFinal);
	}
	
	/**
	 * include a {@link Machine} with its {@link State} and {@link Transition} given a {@link Type} conversion
	 * @param that
	 * @param f {@link Type} conversion
	 * @return {@link Map} from {@link State} of that to {@link State} of this
	 */
	private <X, TYPE2 extends Type<TYPE2,X>> 
	Map<State<X,TYPE2,R>,State<T,TYPE,R>> include(Machine<X,TYPE2,R> that, Function<TYPE2,TYPE> f) {
		final Map<State<X,TYPE2,R>, State<T,TYPE,R>> map = new HashMap<>();
		that.states.stream().forEach(s->map.put(s,state()));
		that.transitions.stream().forEach(t->transition(
			map.get(t.source()), f.apply(t.value()), map.get(t.target()))
		);
		return map;
	}
	
	
	
	private <X> Map<X, State<T,TYPE,R>> include(final Stream<X> stream, final X initial, final Predicate<X> isFinal) {
		final Map<X, State<T,TYPE,R>> map = new HashMap<>();
		stream.filter(s -> !Objects.equals(s, initial)).forEach(s -> map.put(s, state(isFinal.test(s))));
		map.put(initial, this.initial);
		return map;
	}
	
	private
	Map<State<T,TYPE,R>, State<T,TYPE,R>> clone(final Stream<State<T,TYPE,R>> stream, final State<T,TYPE,R> initial, final Predicate<State<T,TYPE,R>> isFinal) {
		final Map<State<T,TYPE,R>, State<T,TYPE,R>> map = this.include(stream, initial, isFinal);
		map.forEach((a,b) -> a.next().forEach(t -> transition(b, t.value(), map.get(t.target()))));
		return map;
	}
	

	@Override
	public Machine<T,TYPE,R> concat(Machine<T,TYPE,R> that) {
		Machine<T,TYPE,R> result = new Machine<>(factory, this.hasEpsilon() && that.hasEpsilon());
		
		Map<Tuple<State<T,TYPE,R>,State<T,TYPE,R>>,State<T,TYPE,R>> map = result.include(
			Stream.concat(states.stream().map(Tuple::left), that.states.stream().map(Tuple::right)), 
			Tuple.left(initial), 
			t -> Objects.nonNull(t.b) && t.b.isFinal()
		);
		
		
		map.forEach((k,v) -> {
			if (k.a != null)
				k.a.next().forEach(t -> result.transition(v, t.value(), map.get(Tuple.left(t.target()))));
			else
				k.b.next().forEach(t -> result.transition(v, t.value(), map.get(Tuple.right(t.target()))));
		});
		//Map<State<T,TYPE,R>,State<T,TYPE,R>> s1 = result.include(that);
	
		// F1 -> I2
		result.transition(
			this.finals().stream().map(Tuple::left).map(map::get).collect(Collectors.toSet()), 
			map.get(Tuple.right(that.initial()))
		);

		// special case when one epsilon is involved
		if (this.epsilon && !that.epsilon) {
			// I -> I2
			result.transition(
				result.initial(), 
				map.get(Tuple.right(that.initial()))
			);
		} else if (that.epsilon && !this.epsilon) {
			// F1 -> F
			result.transition(
				this.finals().stream().map(Tuple::left).map(map::get).collect(Collectors.toSet()), 
				result.state(true)
			);
			
		}
		
		return result.determinize();
	}
	
	private Machine<T,TYPE,R> operation(final Machine<T,TYPE,R> that, final BooleanOperator op) {
		final Machine<T,TYPE,R> result = new Machine<>(factory, op.apply(this.hasEpsilon(), that.hasEpsilon()));
		final Map<Tuple<State<T,TYPE,R>,State<T,TYPE,R>>, State<T,TYPE,R>> map = result.include(
			Sets.product(
				Stream.concat(this.states.stream(), Stream.of((State<T,TYPE,R>)null)), 
				Stream.concat(that.states.stream(), Stream.of((State<T,TYPE,R>)null))
			), 
			Tuple.of(this.initial(), that.initial()),
			t -> op.apply(Objects.nonNull(t.a) && t.a.isFinal(), Objects.nonNull(t.b) && t.b.isFinal())
		);

		// for each power-state
		map.entrySet().forEach(e -> {
			final Tuple<State<T,TYPE,R>,State<T,TYPE,R>> source = e.getKey();
			final Set<TYPE> inputs = Stream.of(source.a, source.b).filter(Objects::nonNull)
					.map(State::nextSymbols)
					.reduce(Stream.of(), Stream::concat)
					.distinct()
					.collect(Collectors.toSet());
			
			// for each partition of the states input			
			Type.partition(inputs).forEach(x -> {
				if (Objects.isNull(source.a) || source.a.next(x).noneMatch(t1 -> {
					if (Objects.isNull(source.b) || source.b.next(x).noneMatch(t2 -> {
						result.transition(map.get(source), x, map.get(Tuple.of(t1, t2)));
						return true;
					})) {
						result.transition(map.get(source), x, map.get(Tuple.of(t1, null)));
					}
					return true;
				})) {
					if(Objects.isNull(source.b) || source.b.next(x).noneMatch(t2 -> {
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
	public Machine<T,TYPE,R> unite(final Machine<T,TYPE,R> that) {
		return operation(that, (a,b) -> a || b);
	}
	
	@Override
	public Machine<T, TYPE, R> intersect(final Machine<T, TYPE, R> that) {
		return operation(that, (a,b) -> a && b);
	}
	
	@Override
	public Machine<T, TYPE, R> minus(final Machine<T, TYPE, R> that) {
		return operation(that, (a,b) -> a && !b);
	}
	
	public Machine<T, TYPE, R> xor(final Machine<T, TYPE, R> that) {
		return operation(that, (a,b) -> a ^ b);
	}
	
	public Machine<T, TYPE, R> parallel(final Machine<T, TYPE, R> that) {
		final Machine<T,TYPE,R> result = new Machine<>(factory, this.hasEpsilon() && that.hasEpsilon());
		final Map<Tuple<State<T,TYPE,R>,State<T,TYPE,R>>, State<T,TYPE,R>> map = result.include(
			Sets.product(this.states.stream(), that.states.stream()), 
			Tuple.of(this.initial(), that.initial()),
			t -> t.a.isFinal() && t.b.isFinal()
		);
		
		// for each power-state
		map.entrySet().forEach(e -> {
			final Tuple<State<T,TYPE,R>,State<T,TYPE,R>> source = e.getKey();
			source.a.next().forEach(t -> 
				result.transition(map.get(source), t.value(), map.get(Tuple.of(t.target(), source.b)))
			);
			source.b.next().forEach(t -> 
				result.transition(map.get(source), t.value(), map.get(Tuple.of(source.a, t.target())))
			);
		});
		
		return result.determinize();
	}
	
	@Override
	public Machine<T,TYPE,R> iterate() {
		Machine<T,TYPE,R> result = new Machine<>(factory, this.hasEpsilon());
		Map<State<T,TYPE,R>,State<T,TYPE,R>> s0 = result.include(this);
		
		// F1 -> I1
		result.transition(
			this.finals.stream().map(s0::get).collect(Collectors.toSet()),
			s0.get(this.initial)
		);
		
		return result.determinize();
	}
	

	@Override
	public Machine<T,TYPE,R> optional() {
		Machine<T,TYPE,R> result = new Machine<>(factory, true);
		
		result.include(this);
		

		return result.determinize();
	}
	
	
	@Override
	public Machine<T,TYPE,R> complement() {
		// TODO something is fishy
		
		final Machine<T,TYPE,R> result = new Machine<>(factory, !hasEpsilon());
		result.clone(states.stream(), initial, State::nonFinal);
		
		
		// new F
		final State<T,TYPE,R> fin = result.state(true);
		
		// S -> F with input completion
		result.states().forEach(s -> result.transition(
				s,
				factory.union(s.nextSymbols()).complement(),
				fin
		));
				
		return result.determinize();
	}


	
	
	
	
	/**
	 * @return a new deterministic machine that is equivalent to this
	 */
	private Machine<T,TYPE,R> determinize() {
		
		// initial cleanup to reduce states
		removeUnreachable();
		
		//System.out.println("rem -> "+this);
		
		final Machine<T,TYPE,R> result = new Machine<>(factory, epsilon);
		final Map<Set<State<T,TYPE,R>>, State<T,TYPE,R>> map = result.include(
			Sets.power(this.states()).stream(), 
			Sets.of(this.initial()), 
			x -> x.stream().anyMatch(State::isFinal)
		);
		
		// for each power-state
		map.entrySet().forEach(e -> {
			final Set<State<T,TYPE,R>> source = e.getKey();
			final Set<TYPE> inputs = source.stream()
					.map(State::nextSymbols)
					.reduce(Stream.of(), Stream::concat)
					.distinct()
					.collect(Collectors.toSet());
			
			// for each partition of the states input			
			Type.partition(inputs).stream().forEach(x -> {
				final Set<State<T,TYPE,R>> target = source.stream()
						.map(s -> s.next(x))
						.reduce(Stream.of(), Stream::concat)
						.collect(Collectors.toSet());
				
				// set the power-transition
				result.transition(map.get(source), x, map.get(target));
			});
		});
		
		result.removeUnreachable();
		result.identify();
		return result;
	}

	
	/**
	 * remove unreachable {@link State}
	 */
	private void removeUnreachable() {
		while(states.removeIf(State::isUnreachable)) {
			finals.removeIf(f -> !states.contains(f));
			transitions.removeIf(t -> !states.contains(t.source()) || !states.contains(t.target()));
		}
		// remove loop on initial when it's not an final (e.a. epsilon is not contained)
		if (!epsilon && states.size() == 1) transitions.clear();
	}
	
	/**
	 * identify equal {@link State}
	 * it is required to be deterministic before calling this function
	 */
	private void identify() {
		
		// combine states
		Set<Tuple<State<T,TYPE,R>, State<T,TYPE,R>>> equalent = new HashSet<>();
		states.stream().forEach(P -> {
			states.stream().filter(Q -> Q.hashCode() < P.hashCode() && !Q.equals(P)).forEach(Q -> {
				if ((P.isFinal() && Q.isFinal()) || (!P.isFinal() && !Q.isFinal())) {
					equalent.add(Tuple.of(P, Q));
				}
			});
		});	
		
		// remove unequal pairs
		while(equalent.removeIf(current -> {
			final State<T,TYPE,R> P = current.a;
			final State<T,TYPE,R> Q = current.b;
			
			// for all inputs in sigma: check if transitions end up on the same states
			return Type.partition(Stream.concat(P.nextSymbols(), Q.nextSymbols()).collect(Collectors.toSet())).stream()
					.anyMatch(c -> {
				final State<T,TYPE,R> PT = P.next(c).findAny().orElse(null);
				final State<T,TYPE,R> QT = Q.next(c).findAny().orElse(null);
				return !Objects.equals(PT, QT) && !equalent.contains(Tuple.of(PT, QT));
			});
			
		})) {};
		
		// combine remaining states
		equalent.stream().forEach(current -> current.apply(State::combine));
	}

	@Override
	public String toString() {
		try {
			return convert(new Expression.Factory<T>()).toString();
		} catch (Exception e) {
			return transitions.toString()+(hasEpsilon()?"+e":"");
		}
	}
	
	public boolean contains(Stream<T> word) {
		return contains(word.collect(Collectors.toList()));
	}
	
	@Override
	public boolean contains(List<T> word) {
		if (epsilon && word.isEmpty())
			return true;
		Set<State<T,TYPE,R>> states = new HashSet<>();
		states.add(initial);
		for(T t : word) {
			states = states.stream().map(s -> s.next(t)).reduce(Stream.of(), Stream::concat).collect(Collectors.toSet());
		}
		return states.stream().anyMatch(State::isFinal);
	}
	
	
	public List<R> apply(List<T> word) {
		
		Set<Tuple<State<T,TYPE,R>,List<R>>> states = new HashSet<>();
		states.add(Tuple.of(initial, new LinkedList<R>()));
		for(T t : word) {
			states = states.stream().map(s -> s.a.next(t, s.b)).reduce(Stream.of(), Stream::concat).collect(Collectors.toSet());
		}
		
		return states.stream().filter(s->s.a.isFinal()).findAny().get().b;
	}

	@Override
	public boolean containsAll(Machine<T,TYPE,R> that) {
		
		if (that.hasEpsilon() && !this.hasEpsilon())
			return false;
		
		Map<State<T,TYPE,R>, State<T,TYPE,R>> map = new HashMap<>();
		
		map.put(that.initial(), this.initial());
		
		Map<State<T,TYPE,R>, State<T,TYPE,R>> current = new HashMap<>();
		Map<State<T,TYPE,R>, State<T,TYPE,R>> next = new HashMap<>();
		
		next.putAll(map);
		
		do {
			
			current.clear();
			current.putAll(next);
			next.clear();
		
			for(Entry<State<T,TYPE,R>, State<T,TYPE,R>> pair : current.entrySet()) {
				
				//Type.partition(Stream.concat(pair.getKey().nextSymbols(), pair.getValue().nextSymbols()))
				
				if (!pair.getKey().next().allMatch(t1 -> {
					if (pair.getValue().next(t1.value()).count() == 0) {
						//System.out.println("not found "+t1);
						return false;
					}
					return pair.getValue().next(t1.value()).anyMatch(t2 -> {
				
						if (map.containsKey(t1.target()) && !map.get(t1.target()).equals(t2)) {
							//System.out.println(t1.target()+"!="+t2);
							return false;
						}	
						if (t1.target().isFinal() != t2.isFinal()) {
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
	State<T,TYPE,R> initial() {
		return initial;
	}

	/**
	 * @return all final {@link State}s
	 */
	Set<State<T,TYPE,R>> finals() {
		return finals;
	}

	/**
	 * 
	 * @return all {@link Transition}s
	 */
	Collection<Transition<T,TYPE,R>> transitions() {
		return transitions;
	}

	/**
	 * @return all {@link State}s
	 */
	Set<State<T,TYPE,R>> states() {
		return states;
	}

	
	
	@Override
	public Machine<T,TYPE,R> THIS() {
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
	public <THAT extends Language<THAT, T>> THAT convert(Language.Factory<THAT, T> factory) {
		// TODO 
		final Machine<List<T>, THAT, R> result = new Machine<>(factory, hasEpsilon());
		
		
		final Map<State<T,TYPE,R>, State<List<T>,THAT,R>> s1 = result.include(this, n -> n.toLanguage(factory));
		
		
		result.transition(result.initial, s1.get(this.initial));
		
		result.transition(
			this.finals().stream().map(s1::get).collect(Collectors.toSet()), 
			result.state(true)
		);
		
		
		result.removeUnreachable();
		
		result.states.stream().filter(s->!s.isInitial()&&!s.isFinal()).forEach(s -> {
			Optional<THAT> opt = s.loop().map(Transition::value).findFirst();
			s.prev(false).forEach(left -> {
				s.next(false).forEach(right -> {
					if (opt.isPresent()) {
						result.transition(left.source(), left.value().concat(opt.get().star()).concat(right.value()) ,right.target());
					} else {
						result.transition(left.source(), left.value().concat(right.value()) ,right.target());
					}
				});
			});
			result.transitions.removeIf(t -> s.isSource(t) || s.isTarget(t));
		});
		
		System.out.println("k "+result.transitions);
		
		return null;
	}
	
	
	
	public static class Factory<T, TYPE extends Type<TYPE, T>,R> implements Language.Factory<Machine<T,TYPE,R>, T> {
		
		public Factory(Type.Factory<TYPE, T> factory) {
			this.factory = factory;
		}
		
		private final Type.Factory<TYPE, T> factory;
		
		@Override
		public Machine<T, TYPE,R> empty() {
			return new Machine<>(factory, false);
		}
		@Override
		public Machine<T, TYPE,R> epsilon() {
			return new Machine<>(factory, true);
		}
		@Override
		public Machine<T, TYPE, R> factor(T that) {
			return new Machine<>(factory, factory.summand(that));
		}
		
	}



	@Override
	public Machine<T, TYPE, R> reverse() {
		
		final Machine<T,TYPE,R> result = new Machine<>(factory, hasEpsilon());
		
		final Map<State<T,TYPE,R>, State<T,TYPE,R>> s0 = result.include(states.stream(), null, State::isInitial);
		
		s0.forEach((a,b) -> {
			if (a != null) a.next().forEach(t -> result.transition(s0.get(t.target()), t.value(), b));
		});
		
		// null -> F1
		result.transition(
			s0.get(null),
			this.finals.stream().map(s0::get).collect(Collectors.toSet())
		);
		
		return result.determinize();

	}

	@Override
	public Factory<T, TYPE, R> factory() {
		return new Factory<>(factory);
	}

	@Override
	public boolean isFinite() {
		return transitions.stream().map(Transition::value).allMatch(Type::isFinite) && !hasLoops();
	}
	
	private boolean hasLoops() {
		Set<State<T,TYPE,R>> visited = new HashSet<>();
		Set<State<T,TYPE,R>> current = new HashSet<>();
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


	
	
	
}
