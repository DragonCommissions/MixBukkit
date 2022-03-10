package com.dragoncommissions.mixbukkit.agent;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.ClassPool;
import javassist.CtClass;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class ClassesManager {


    public static Map<String, byte[]> classes = new HashMap<>();
    public static Map<String, ClassNode> classNodes = new HashMap<>();
    private static ClassPool cp = ClassPool.getDefault();

    public static void init() {
        MixBukkit.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                classes.put(className.replace("/", "."), classfileBuffer);
                return classfileBuffer;
            }
        }, true);
    }

    public static ClassNode getClassNode(String name) {
        ClassNode classNode1 = classNodes.get(name);
        if (classNode1 == null) {
            byte[] classBytecode = getClassBytecode(name);
            if (classBytecode == null) return null;
            classNode1 = ASMUtils.toClassNode(classBytecode);
            return classNode1;
        }
        return classNode1;
    }

    public synchronized static byte[] getClassBytecode(String name) {
        name = name.replace("/", ".");
        byte[] bytes = classes.get(name);
        if (bytes == null) {
            Class[] allLoadedClasses = MixBukkit.INSTRUMENTATION.getAllLoadedClasses();
            for (Class allLoadedClass : allLoadedClasses) {
                if (allLoadedClass.getName().equals(name)) {
                    try {
                        MixBukkit.INSTRUMENTATION.retransformClasses(allLoadedClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes.get(name);
    }


}
