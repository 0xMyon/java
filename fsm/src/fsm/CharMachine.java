package fsm;

import lang.FiniteSet;
import lang.InfiniteSet;

public class CharMachine extends Machine<Character, InfiniteSet<Character>, Void> {

	public final static InfiniteSet.Factory<Character> FACTORY = new InfiniteSet.Factory<Character>();
	
	public CharMachine() {
		this(false);
	}

	public CharMachine(boolean epsilon) {
		super(FACTORY, epsilon);
	}
	
	public CharMachine(Character c) {
		super(FACTORY, new FiniteSet<>(c));
	}
	
	

}
