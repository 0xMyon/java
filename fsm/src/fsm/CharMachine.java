package fsm;

import set.ComplementSet;
import set.FiniteSet;

public class CharMachine extends Machine<Character, Void> {

	public final static ComplementSet.Factory<Character> FACTORY = new ComplementSet.Factory<>();

	public CharMachine() {
		this(false);
	}

	public CharMachine(final boolean epsilon) {
		super(FACTORY, epsilon);
	}

	public CharMachine(final Character c) {
		super(FACTORY, new ComplementSet<>(new FiniteSet<>(c)));
	}

}
