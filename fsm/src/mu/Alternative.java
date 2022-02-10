package mu;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lang.Language;

public class Alternative<T> implements Mu<T> {

	private final Set<Mu<T>> elements;
	
	private Alternative(Set<Mu<T>> elements) {
		this.elements = elements;
	}
	
	@SafeVarargs
	public static <T> Mu<T> of(Mu<T>... elements) {
		return new Alternative<>(Stream.of(elements).collect(Collectors.toSet()));
	}

	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		return factory.union(elements.stream().map(x -> x.convertLanguage(factory, function)));
	}

	@Override
	public Mu<T> complement() {
		// TODO Auto-generated method stub
		return null;
	}


}
