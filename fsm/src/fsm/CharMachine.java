package fsm;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import set.ComplementSet;
import set.FiniteSet;

public class CharMachine extends Machine<Character, Void, ComplementSet<Character, FiniteSet<Character>>> {

	public final static ComplementSet.Factory<Character, FiniteSet<Character>> FACTORY = 
			new ComplementSet.Factory<>(new FiniteSet.Factory<>());

	public CharMachine() {
		this(false);
	}

	public CharMachine(final boolean epsilon) {
		super(FACTORY, epsilon);
	}

	
	public CharMachine(final Stream<Character> cs) {
		super(FACTORY, cs.toArray(Character[]::new));
	}
	
	public CharMachine(final Character... cs) {
		super(FACTORY, cs);
	}
		
	public CharMachine(String str) {
		this(toStream(str));
	}
	
	boolean contains(String str) {
		return contains(Arrays.asList(toStream(str).toArray(Character[]::new)));
	}
	
	private static Stream<Character> toStream(String str) {
		return IntStream.range(0, str.length()).mapToObj(str::charAt);
	}

}
