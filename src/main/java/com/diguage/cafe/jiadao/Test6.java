package com.diguage.cafe.jiadao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class Test6 {
    public static void main(String[] args) throws NoSuchFieldException {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        String className = "com.diguage.cafe.jiadao.Test6$UnloadedBar";
        DynamicType.Loaded<Object> qux = new ByteBuddy()
                .redefine(typePool.describe(className).resolve(), // do not use 'Bar.class'
                        ClassFileLocator.ForClassLoader.ofSystemLoader())
                .defineField("qux", String.class) // we learn more about defining fields later
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER);
        Class<?> quxClass = qux.getLoaded();
        Field qux1 = quxClass.getDeclaredField("qux");
        assertThat(qux1).isNotNull();
    }

    public static class UnloadedBar {

    }
}
