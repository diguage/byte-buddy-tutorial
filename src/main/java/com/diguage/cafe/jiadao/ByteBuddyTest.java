package com.diguage.cafe.jiadao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.utility.JavaModule;
import org.junit.jupiter.api.Test;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ByteBuddyTest {
//
//    @Test
//    public void test1() {
//        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
//                .subclass(Object.class)
//                .make();
//    }
//
//    @Test
//    public void test2() {
//        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
//                .subclass(Object.class)
//                .name("com.diguage.cafe.jiadao.demo.Type")
//                .make();
//    }
//
//    @Test
//    public void test3() {
//        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
//                .with(new NamingStrategy.AbstractBase() {
//                    @Override
//                    protected String name(TypeDescription superClass) {
//                        return "com.diguage.cafe.buddy." + superClass.getTypeName();
//                    }
//
////                    @Override
////                    public String subclass(TypeDescription.Generic superClass) {
////                        return "com.diguage.cafe.buddy." + superClass.getTypeName();
////                    }
//                })
//                .subclass(Object.class)
//                .make();
//    }
//
//    @Test
//    public void test4() {
//        Class<?> type = new ByteBuddy()
//                .subclass(Object.class)
//                .make()
//                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
//                .getLoaded();
//    }
//
//    @Test
//    public void test5() {
//        ByteBuddyAgent.install();
//        Foo foo = new Foo();
//        new ByteBuddy()
//                .redefine(Bar.class)
//                .name(Foo.class.getName())
//                .make()
//                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
//        assertThat(foo.m()).isEqualTo("bar");
//    }
//
//    // TODO 报错，好奇怪
//    @Test
//    public void test6() {
//
//        TypePool typePool = TypePool.Default.ofSystemLoader();
//
//        String quxFieldName = "qux";
//        // TODO 示例里面显示是 Class，但是程序返回的不是 Class
//        // TODO 加一句 .getLoaded() 就会返回 Class，但是还是不行
//        DynamicType.Loaded<Object> barType = new ByteBuddy()
//                .redefine(typePool.describe("com.diguage.cafe.jiadao.Bar").resolve(),
//                        ClassFileLocator.ForClassLoader.ofSystemLoader())
//                .defineField(quxFieldName, String.class)
//                .make()
//                .load(ClassLoader.getSystemClassLoader());
//
//        FieldList<FieldDescription.InDefinedShape> declaredFields = barType.getTypeDescription().getDeclaredFields();
//        assertThat(declaredFields).isNotEmpty();
//    }
//
//    /**
//     * TODO 还没有实验
//     */
//    public static class ToStringAgent {
//        public static void premain(String argments, Instrumentation instrumentation) {
//            new AgentBuilder.Default()
//                    .type(isAnnotatedWith(ToString.class))
//                    .transform(new AgentBuilder.Transformer() {
//                        @Override
//                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
//                                                                TypeDescription typeDescription,
//                                                                ClassLoader classLoader,
//                                                                JavaModule module) {
//
//                            return builder.method(named("toString"))
//                                    .intercept(FixedValue.value("transformed"));
//                        }
//                    }).installOn(instrumentation);
//        }
//    }
//
//
//    public static @interface ToString {
//    }
//
//    @Test
//    public void test7() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        String className = "com.diguage.cafe.jiadao.demo.Type";
//        String toString = new ByteBuddy()
//                .subclass(Object.class)
//                .name(className)
//                .make()
//                .load(getClass().getClassLoader())
//                .getLoaded()
//                .getDeclaredConstructor()
//                .newInstance()
//                .toString();
//        assertThat(toString).contains(className);
//    }
//
//    @Test
//    public void test8() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        String toString = new ByteBuddy()
//                .subclass(Object.class)
//                .name("com.diguage.cafe.jiadao.demo.Type")
//                .method(named("toString"))
//                .intercept(FixedValue.value("Hello, https://www.diguage.com"))
//                .make()
//                .load(getClass().getClassLoader())
//                .getLoaded()
//                .getDeclaredConstructor()
//                .newInstance()
//                .toString();
//        assertThat(toString).contains("https://www.diguage.com");
//    }

    /**
     * TODO 理解错了 takesArguments(0) 应该是长度为 0，不是第一个参数。
     */
    @Test
    public void test9() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String className = "com.diguage.cafe.jiadao.demo.Type";
        String website = "https://www.diguage.com";
        Object dynamicObject = new ByteBuddy()
                .subclass(Object.class)
                .name(className)
                .method(named("toString").and(returns(String.class)).and(takesArguments(0)))
                .intercept(FixedValue.argument(0))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
        Method[] methods = dynamicObject.getClass().getDeclaredMethods();
        Method method = Arrays.stream(methods)
                .filter(m -> "toString".equals(m.getName()) && m.getParameterCount() > 0)
                .findAny().orElse(null);

        assertThat(method).isNotNull();
        Object toString = method.invoke(dynamicObject, website);
        assertThat(toString).isInstanceOf(String.class);
        assertThat(toString).isEqualTo(website);
    }


}
