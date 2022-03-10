package com.dragoncommissions.mixbukkit.agent;

import com.dragoncommissions.mixbukkit.MixBukkit;
import javassist.ClassPool;
import javassist.CtClass;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class ClassesManager {


    public static Map<String, CtClass> classes = new HashMap<>();

    public static void init() {
        ClassPool cp = ClassPool.getDefault();
        MixBukkit.INSTRUMENTATION.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    classes.put(className.replace("/", "."), cp.makeClass(className));
                } catch (Exception e) {
                    System.out.println("Failed to make class: " + className.replace("/", "."));
                }
                return classfileBuffer;
            }
        });
    }

    public static CtClass getClass(String name) {
        CtClass ctClass = classes.get(name);
        if (ctClass == null) {
            Class[] allLoadedClasses = MixBukkit.INSTRUMENTATION.getAllLoadedClasses();
            for (Class allLoadedClass : allLoadedClasses) {
                if (allLoadedClass.getName().equals(name)) {
                    try {
                        MixBukkit.INSTRUMENTATION.retransformClasses(allLoadedClass);
                    } catch (Exception ignored) {}
                }
            }
        }
        return classes.get(name);
    }

}
