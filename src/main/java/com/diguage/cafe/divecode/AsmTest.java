package com.diguage.cafe.divecode;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.objectweb.asm.ClassReader.SKIP_CODE;
import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM9;

public class AsmTest {
    @Test
    public void test1() throws IOException {
        ClassReader cr = new ClassReader(getAsStream());
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                System.out.println("field:" + name);
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                System.out.println("method:" + name);
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        cr.accept(cv, SKIP_CODE | SKIP_DEBUG);
    }

    private static InputStream getAsStream() {
        return ClassLoader.getSystemResourceAsStream("com/diguage/cafe/divecode/MyMain.class");
    }

    @Test
    public void test2() throws IOException {
        ClassReader cr = new ClassReader(getAsStream());
        ClassNode cn = new ClassNode();
        cr.accept(cn, SKIP_DEBUG | SKIP_CODE);
        for (FieldNode field : cn.fields) {
            System.out.println("field:" + field.name);
        }
        for (MethodNode method : cn.methods) {
            System.out.println("method:" + method.name);
        }
        ClassWriter cw = new ClassWriter(0);
        cr.accept(cw, 0);
        byte[] modifiedBytes = cw.toByteArray();
    }

    @Test
    public void test3() throws IOException {
        URI uri = getUri();
        System.out.println(uri);
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                // TODO 查看生产的代码，感觉这里的 xyz 的类型声明不对。
                FieldVisitor fv = cv.visitField(ACC_PUBLIC, "xyz", "Ljava/lang/String", null, null);
                if (Objects.nonNull(fv)) {
                    fv.visitEnd();
                }
            }
        };
        cr.accept(cv, SKIP_CODE | SKIP_DEBUG);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }

    private static URI getUri() {
        try {
            return ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyNewMain.class").toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test4() throws IOException {
        URI uri = getUri();
        System.out.println(uri);
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                // TODO 查看生产的代码，感觉这里的 xyz 的类型声明不对。
                MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "xyz", "(ILjava/lang/String;)V", null, null);
                if (Objects.nonNull(mv)) {
                    mv.visitEnd();
                }
            }
        };
        cr.accept(cv, SKIP_CODE | SKIP_DEBUG);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }

    @Test
    public void test5() throws URISyntaxException, IOException {
        URI uri = ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyMain.class").toURI();
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ("abc".equals(name)) {
                    return null;
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ("xyz".equals(name)) {
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        cr.accept(cv, SKIP_CODE | SKIP_DEBUG);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }
}
