package mu;

import java.util.function.Function;

import lang.Language;

public class Possibility<T> implements Mu<T> {

	private final T x;
	private final Mu<T> term;
	
	public Possibility(T x, Mu<T> term) {
		this.x = x;
		this.term = term;
	}
	
	@Override
	public <U, THAT extends Language<THAT, U>> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		// TODO Auto-generated method stub
		return factory.factor(function.apply(x)).concat(term.convertLanguage(factory, function));
	}

	@Override
	public Mu<T> complement() {
		return new Consequence<>(x, term.complement());
	}

}
