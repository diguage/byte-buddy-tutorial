package com.diguage.cafe.divecode;

import javassist.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static javassist.CtClass.intType;
import static javassist.Modifier.PUBLIC;

public class JavassistTest {
    @Test
    public void test1() throws CannotCompileException, IOException {
        ClassPool cp = ClassPool.getDefault();
        CtClass ct = cp.makeClass("com.diguage.cafe.divecode.JavassistMain");
        ct.writeFile(getPath());
    }

    @Test
    public void test2() throws NotFoundException, CannotCompileException, IOException {
        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(getMainPath());
        CtClass ct = cp.get(JavassistMain.class.getName());
        CtMethod method = new CtMethod(intType, "foo", new CtClass[]{intType, intType}, ct);
        method.setModifiers(PUBLIC);
        ct.addMethod(method);


        ct.writeFile(getPath());
    }

    @Test
    public void test3() throws NotFoundException, CannotCompileException, IOException {
        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(getMainPath());
        CtClass ct = cp.get(JavassistMain.class.getName());
        CtMethod method = ct.getMethod("bar", "(II)I");
        method.setBody("return $1 * $2;");
        ct.writeFile(getPath());
    }

    private static String getMainPath() {
        String testClassPath = "com/diguage/cafe/divecode/JavassistMain.class";
        URL url = Thread.currentThread().getContextClassLoader().getResource(testClassPath);
        String path = url.getPath();
        return path;
    }


    private static String getPath() {
        String testClassPath = "com/diguage/cafe/divecode/JavassistTest.class";
        URL url = Thread.currentThread().getContextClassLoader().getResource(testClassPath);
        String path = url.getPath();
        String classPath = path.replace(testClassPath, "");
        return classPath;
    }
}
