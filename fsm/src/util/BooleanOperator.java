package util;

@FunctionalInterface
public interface BooleanOperator {

	boolean apply(boolean a, boolean b);
	
}
