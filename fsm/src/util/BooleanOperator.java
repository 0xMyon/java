package util;

@FunctionalInterface
public interface BooleanOperator {

	boolean apply(boolean a, boolean b);

	public final static BooleanOperator conjunction = (a,b) -> a && b;
	public final static BooleanOperator disjunction = (a,b) -> a || b;

	public final static BooleanOperator abjunction = (a,b) -> a && !b;
	public final static BooleanOperator implication = (a,b) -> !a || b;

	public final static BooleanOperator antivalence = (a,b) -> a != b;
	public final static BooleanOperator equalence = (a,b) -> a == b;

}
