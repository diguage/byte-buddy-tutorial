package com.diguage.cafe.divecode;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
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
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

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

    /**
     * 本实例有错误！看下面一个示例的代码。
     */
    @Test
    public void test6() throws URISyntaxException, IOException {
        URI uri = ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyMain.class").toURI();
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            private String methodName = "foo";

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (methodName.equals(name)) {
                    // 删除 foo 方法
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            @Override
            public void visitEnd() {
                MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, methodName, "(I)I", null, null);
                mv.visitCode();
                mv.visitVarInsn(ILOAD, 1);
                mv.visitIntInsn(BIPUSH, 100);
                mv.visitInsn(IADD);
                mv.visitInsn(IRETURN);
                mv.visitEnd();
            }
        };
        cr.accept(cv, 0);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }

    /**
     * 使用 ASM 自动计算 stack 和 locals
     */
    @Test
    public void test7() throws URISyntaxException, IOException {
        URI uri = ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyMain.class").toURI();
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            private String methodName = "foo";

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (methodName.equals(name)) {
                    // 删除 foo 方法
                    return null;
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            @Override
            public void visitEnd() {
                MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, methodName, "(I)I", null, null);
                mv.visitCode();
                mv.visitVarInsn(ILOAD, 1);
                mv.visitIntInsn(BIPUSH, 100);
                mv.visitInsn(IADD);
                mv.visitInsn(IRETURN);
                // 触发计算
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        };
        cr.accept(cv, 0);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }

    @Test
    public void test8() throws IOException, URISyntaxException {
        URI uri = ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyMain.class").toURI();
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (!"foo".equals(name)) {
                    return mv;
                }
                return new AdviceAdapter(ASM9, mv, access, name, descriptor) {
                    @Override
                    protected void onMethodEnter() {
                        super.onMethodEnter();
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mv.visitLdcInsn("enter:" + getName());
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    }

                    @Override
                    protected void onMethodExit(int opcode) {
                        super.onMethodExit(opcode);
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        if (opcode == ATHROW) {
                            mv.visitLdcInsn("err exit:" + getName());
                        } else {
                            mv.visitLdcInsn("normal exit:" + getName());
                        }
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    }
                };
            }
        };
        cr.accept(cv, 0);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }

    @Test
    public void test9() throws IOException, URISyntaxException {
        URI uri = ClassLoader.getSystemResource("com/diguage/cafe/divecode/MyMain.class").toURI();
        byte[] bytes = FileUtils.readFileToByteArray(new File(uri));
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (!"divzero".equals(name)) {
                    return mv;
                }
                return new AdviceAdapter(ASM9, mv, access, name, descriptor) {
                    Label startLabel = new Label();

                    @Override
                    protected void onMethodEnter() {
                        super.onMethodEnter();
                        mv.visitLabel(startLabel);

                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        mv.visitLdcInsn("enter:" + getName());
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        // 生成异常表
                        Label endLabel = new Label();
                        mv.visitTryCatchBlock(startLabel, endLabel, endLabel, null);
                        mv.visitLabel(endLabel);

                        //生成异常代码
                        finallyBlock(ATHROW);
                        mv.visitInsn(ATHROW);
                        super.visitMaxs(maxStack, maxLocals);
                    }

                    private void finallyBlock(int opcode) {
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                        if (opcode == ATHROW) {
                            mv.visitLdcInsn("err exit:" + getName());
                        } else {
                            mv.visitLdcInsn("normal exit:" + getName());
                        }
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    }

                    @Override
                    protected void onMethodExit(int opcode) {
                        super.onMethodExit(opcode);
                        if (opcode != ATHROW) {
                            finallyBlock(opcode);
                        }
                    }
                };
            }
        };
        cr.accept(cv, 0);
        byte[] modifiedBytes = cw.toByteArray();
        FileUtils.writeByteArrayToFile(new File(uri), modifiedBytes);
    }
}
