package fsm;

import set.ComplementSet;
import set.FiniteSet;

public class CharMachine extends Machine<Character, ComplementSet<FiniteSet<Character>, Character>, Void> {

	public final static ComplementSet.Factory<FiniteSet<Character>, Character> FACTORY = new ComplementSet.Factory<>(new FiniteSet.Factory<Character>());
	
	public CharMachine() {
		this(false);
	}

	public CharMachine(boolean epsilon) {
		super(FACTORY, epsilon);
	}
	
	public CharMachine(Character c) {
		super(FACTORY, new ComplementSet<>(new FiniteSet<>(c)));
	}
	
}
