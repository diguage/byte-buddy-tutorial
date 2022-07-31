package com.diguage.cafe.jiadao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.pool.TypePool;

import static org.assertj.core.api.Assertions.assertThat;

public class Test6 {
//    public static void main(String[] args) {
//        TypePool typePool = TypePool.Default.ofSystemLoader();
//
//        String quxFieldName = "qux";
//        // TODO 示例里面显示是 Class，但是程序返回的不是 Class
//        // TODO 加一句 .getLoaded() 就会返回 Class，但是还是不行
//        TypePool.Resolution resolution = typePool.describe("com.diguage.cafe.jiadao.Bar");
//
//        DynamicType.Loaded<Object> barType = new ByteBuddy()
//                .redefine(resolution.resolve(),
//                        ClassFileLocator.ForClassLoader.ofSystemLoader())
//                .defineField(quxFieldName, String.class)
//                .make()
//                .load(ClassLoader.getSystemClassLoader());
//
//        System.out.println(barType);
//
////        FieldList<FieldDescription.InDefinedShape> declaredFields = barType.getTypeDescription().getDeclaredFields();
////        assertThat(declaredFields).isNotEmpty();
//    }

    public static void main(String[] args) {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        DynamicType.Loaded<Object> qux = new ByteBuddy()
                .redefine(typePool.describe("foo.Bar").resolve(), // do not use 'Bar.class'
                        ClassFileLocator.ForClassLoader.ofSystemLoader())
                .defineField("qux", String.class) // we learn more about defining fields later
                .make()
                .load(ClassLoader.getSystemClassLoader());
//        assertThat(bar.getDeclaredField("qux"), notNullValue());
    }
}
