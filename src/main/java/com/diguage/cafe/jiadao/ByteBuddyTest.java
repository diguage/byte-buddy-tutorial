package com.diguage.cafe.jiadao;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;

public class ByteBuddyTest {

    @Test
    public void test1() {
        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .make();
    }

    @Test
    public void test2() {
        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("com.diguage.cafe.jiadao.demo.Type")
                .make();
    }

    @Test
    public void test3() {
        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
                .with(new NamingStrategy.AbstractBase() {
                    @Override
                    protected String name(TypeDescription superClass) {
                        return "com.diguage.cafe.buddy." + superClass.getTypeName();
                    }

//                    @Override
//                    public String subclass(TypeDescription.Generic superClass) {
//                        return "com.diguage.cafe.buddy." + superClass.getTypeName();
//                    }
                })
                .subclass(Object.class)
                .make();
    }
}
