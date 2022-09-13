package com.diguage.cafe.divecode;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.ProtectionDomain;

import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
import static org.objectweb.asm.ClassReader.SKIP_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ASM9;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("execute premain");
        instrumentation.addTransformer(new AgentFileTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException {
        System.out.println("execute agentmain");
        instrumentation.addTransformer(new AttachClassFileTransformer(), true);
        Class[] classes = instrumentation.getAllLoadedClasses();
        for (Class clazz : classes) {
            if (clazz.getName().contains("AttachTest")) {
                System.out.println("\nReloading class: " + clazz.getName());
                instrumentation.retransformClasses(clazz);
                break;
            }
        }
    }

    public static class AgentMethodVisitor extends AdviceAdapter {
//        // TODO 如何给方法调用加前缀？
//        private ThreadLocal<String> PREFIX = new ThreadLocal<>();

        protected AgentMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
//            String prefix = PREFIX.get();
//            prefix = Objects.isNull(prefix) ? "" : prefix;
//            PREFIX.set("  " + prefix);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn(prefix + ">>>> enter:" + getName());
            mv.visitLdcInsn(">>>> enter:" + getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.onMethodEnter();
        }

        @Override
        protected void onMethodExit(int opcode) {
//            super.onMethodExit(opcode);
//            String prefix = PREFIX.get();
//            if (Objects.isNull(prefix) || prefix.length() < 2) {
//                prefix = "";
//            } else {
//                PREFIX.set(prefix.substring(0, prefix.length() - 2));
//            }
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn(prefix + "<<<< exit:" + getName());
            mv.visitLdcInsn("<<<< exit:" + getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }

    public static class AgentClassVisitor extends ClassVisitor {

        protected AgentClassVisitor(int api) {
            super(api);
        }

        protected AgentClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("<init>".equals(name)) {
                return mv;
            }
            return new AgentMethodVisitor(ASM9, mv, access, name, descriptor);
        }
    }

    public static class AgentFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.contains("diguage")) {
                System.out.println("start to load class: " + className);
            }
            if (!className.contains("AgentTest")) {
                return classfileBuffer;
            }
            System.out.println("start to transform class:" + className);
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
            AgentClassVisitor cv = new AgentClassVisitor(ASM9, cw);
            cr.accept(cv, SKIP_FRAMES | SKIP_DEBUG);
            return cw.toByteArray();
        }
    }

    public static class AttachMethodVisitor extends AdviceAdapter {
        protected AttachMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            mv.visitIntInsn(BIPUSH, 50);
            mv.visitInsn(IRETURN);
        }
    }

    public static class AttachClassVisitor extends ClassVisitor {

        protected AttachClassVisitor(int api) {
            super(api);
        }

        protected AttachClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("foo".equals(name)) {
                return new AttachMethodVisitor(ASM9, mv, access, name, descriptor);
            }
            return mv;
        }
    }

    public static class AttachClassFileTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (!className.contains("AttachTest")) {
                return classfileBuffer;
            }
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
            AttachClassVisitor cv = new AttachClassVisitor(ASM9, cw);
            cr.accept(cv, SKIP_FRAMES | SKIP_DEBUG);
            return cw.toByteArray();
        }
    }

    public static class AttachMain {
        public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, URISyntaxException {
            URL url = Thread.currentThread().getContextClassLoader().getResource("com/diguage/cafe/divecode/AgentMain.class");
            String basePath = url.toURI().getPath().replace("classes/" + "com/diguage/cafe/divecode/AgentMain.class", "");
            VirtualMachine vm = VirtualMachine.attach(args[0]);
            try {
                vm.loadAgent(basePath + "jiadao.jar");
            } finally {
                vm.detach();
            }
        }
    }

}
