package mu;

import java.util.function.Function;

import lang.Language;

public class Consequence<T> implements Mu<T> {

	private final T x;
	private final Mu<T> term;
	
	public Consequence(T x, Mu<T> term) {
		this.x = x;
		this.term = term;
	}
	
	@Override
	public <THAT extends Language<THAT, U>, U> THAT convertLanguage(Language.Factory<THAT, U> factory, Function<T, U> function) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mu<T> complement() {
		return new Possibility<>(x, term.complement());
	}

}
