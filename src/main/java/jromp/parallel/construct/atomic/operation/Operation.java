package jromp.parallel.construct.atomic.operation;

import java.io.Serializable;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Operation<T extends Serializable> extends Supplier<UnaryOperator<T>> {
    String identifier();

    @Override
    UnaryOperator<T> get();
}
