package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Sets {

	/**
	 * @param set
	 * @return {@link Set} of all subsets
	 */
	public static <T> Set<Set<T>> power(final Set<T> set) {
		Set<Set<T>> result = new HashSet<>(Arrays.asList(new HashSet<T>()));
		
		for(final T current : set) {
			result.addAll( result.stream().map(s -> {
				final Set<T> r = new HashSet<>();
				r.addAll(s);
				r.add(current);
				return r;
			}).collect(Collectors.toSet()));
		}
		
		return result;
	}
	
	/**
	 * @param set
	 * @return {@link Set} of {@link Tuple}
	 */
	public static <T,U> Stream<Tuple<T,U>> product(final Stream<T> t, final Stream<U> u) {
		final Set<U> set = u.collect(Collectors.toSet());
		return t.map(a -> set.stream()
				.map(b -> Tuple.of(a, b)))
				.reduce(Stream.of(), Stream::concat);
	}
	
	@SafeVarargs
	public static <T> Set<T> of(T... ts) {
		return new HashSet<>(Arrays.asList(ts));
	}
	
}
