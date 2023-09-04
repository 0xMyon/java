package util;

@FunctionalInterface
public interface BooleanOperator {

	boolean apply(boolean a, boolean b);


	public final static BooleanOperator abjunction = (a,b) -> a && !b;
	public final static BooleanOperator implication = (a,b) -> !a || b;

	public final static BooleanOperator equalence = (a,b) -> a == b;

}
