package util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Algorithm {

	public static <T> Set<T> itterative(final T initial, final Function<T, Stream<T>> f) {

		final Set<T> all = new HashSet<>();
		Set<T> next = new HashSet<>();

		all.add(initial);
		next.add(initial);

		while(!next.isEmpty()) {
			next = next.stream().map(f)
					.reduce(Stream.of(), Stream::concat)
					.filter(s -> !all.contains(s))
					.collect(Collectors.toSet());
			all.addAll(next);
		}

		return all;
	}

}
