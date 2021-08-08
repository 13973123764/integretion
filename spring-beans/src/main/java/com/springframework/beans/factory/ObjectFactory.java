package com.springframework.beans.factory;

import com.springframework.beans.BeansException;

/**
 * 定义一个可以返回一个对象实例（共享或单例）的工厂
 * 该接口是典型的使用当每次调用为目标对象产生一个新实例的生产工厂
 *
 * <p>这个接口类似于{@link FactoryBean}，但是后者的实现通常被定义为{@link BeanFactory}中的SPI实例，
 * 而这个类的实现通常被作为API提供给其他bean（通过注入）。因此，{@code getObject（）}方法具有不同的异常处理行为。
 * @param <T>
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    /**
     * 返回一个通过该工厂管理的单例或独立的实例
     */
    T getObject() throws BeansException;

}
