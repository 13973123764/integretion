package com.springframework.beans.factory;

import com.springframework.beans.BeansException;
import com.springframework.lang.Nullable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * {@link ObjectFactory}的一个变体，专门为注入点设计，允许编程可选性和宽松而非唯一的处理。
 * <p>从5.1开始，这个接口扩展了{@link Iterable}并提供了{@link Stream}支持。
 * 因此，它可以在{@code for}循环中使用，提供{@link#forEach}迭代，并允许集合样式{@link#stream}访问。
 */
public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {

    /**
     * 通过工厂返回一个对象实例
     */
    T getObject(Object... args) throws BeansException;

    /**
     * 通过工厂返回一个对象实例
     */
    @Nullable
    T getIfAvailable() throws BeansException;

    /**
     * 通过工厂返回一个对象实例
     */
    default T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfAvailable();
        return (dependency != null ? dependency : defaultSupplier.get());
    }

    /**
     * 通过工厂返回一个对象实例
     */
    default void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfAvailable();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    /**
     * 通过工厂返回一个对象实例
     */
    @Nullable
    T getIfUnique() throws BeansException;

    /**
     * 返回对象的实例（可能是共享的或独立的）
     */
    default T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfUnique();
        return (dependency != null ? dependency : defaultSupplier.get());
    }

    /**
     * 返回对象的实例（可能是共享的或独立的）
     */
    default void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfUnique();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    default Stream<T> stream() {
        throw new UnsupportedOperationException("Multi element access not supported");
    }

    @Override
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    default Stream<T> orderedStream() {
        throw new UnsupportedOperationException("Ordered element access not supported");
    }
}
