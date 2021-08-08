package com.springframework.beans;


import com.springframework.beans.factory.NoSuchBeanDefinitionException;
import com.springframework.beans.factory.ObjectProvider;
import com.springframework.core.ResolvableType;
import com.springframework.lang.Nullable;

public interface BeanFactory {

    /**
     * 用于取消对{@link FactoryBean}实例的引用，并将其与FactoryBean创建的Bean<i>区分开来。
     * 例如，如果名为{@code myJndiObject}的bean是FactoryBean，
     * 那么获取{@code &myJndiObject}将返回工厂，而不是工厂返回的实例。
     */
    String FACTORY_BEAN_PREFIX = "&";

    /**
     * 返回指定bean的实例，该实例可以是共享的，也可以是独立的。
     * <p>此方法允许使用Spring BeanFactory替换
     * 单例或原型设计模式。调用者可以保留对在单例bean的情况下返回对象。
     * <p>将别名转换回相应的规范bean名称。将询问父工厂是否在此工厂实例中找不到该bean。
     * @param name  bean的名称
     * @throws BeansException
     */
    Object getBean(String name) throws BeansException;


    /**
     * 返回指定bean的实例，该实例可以是共享的，也可以是独立的。
     * <p>行为与{@link #getBean（String）}相同，但提供类型的度量
     * 如果bean不是
     * 必需的类型。这意味着ClassCastException不能在强制转换时抛出
     * 结果是正确的，就像{@link #getBean（String）}一样。
     * <p>将别名转换回相应的规范bean名称。
     * 将询问父工厂是否在此工厂实例中找不到该bean。
     * @param name
     * @param requiredType
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     * 返回指定bean的实例，该实例可以是共享的，也可以是独立的。
     */
    Object getBean(String name, Object... args) throws BeansException;

    /**
     * 返回指定bean的实例，该实例可以是共享的，也可以是独立的。
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;

    /**
     * 返回指定bean的实例，该实例可以是共享的，也可以是独立的。
     */
    <T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

    /**
     * 返回指定bean的提供程序，允许延迟按需检索
     * 实例的数量，包括可用性和唯一性选项。
     */
    <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

    /**
     *
     * @param requiredType
     * @param <T>
     * @return
     */
    <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);


    boolean containsBean(String name);

    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

    boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

    @Nullable
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    String[] getAliases(String name);

}
