package lambda.type;

import java.util.Objects;

import lambda.Type;

public class ArrowType implements Type {

	private final Type domain, codomain;
	
	public ArrowType(Type domain, Type codomain) {
		this.domain = domain;
		this.codomain = codomain;
	}
	
	public Type domain() {
		return domain;
	}
	public Type codomain() {
		return codomain;
	}
	
	public boolean equals(Object other) {
		if (other instanceof ArrowType) {
			final ArrowType that = (ArrowType) other;
			return this.domain.equals(that.domain) && this.codomain.equals(that.codomain);
		}
		return false;
	}
	
	public int hashCode() {
		return Objects.hash(domain, codomain);
	}
	
}
