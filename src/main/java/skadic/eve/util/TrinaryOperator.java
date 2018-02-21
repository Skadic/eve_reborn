package skadic.eve.util;

@FunctionalInterface
public interface TrinaryOperator<T> {

    T apply(T a, T b, T c);
}
