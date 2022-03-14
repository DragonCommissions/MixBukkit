package com.dragoncommissions.mixbukkit.api.action.impl;

import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.MixinAction;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.IShellCodeMethodInvoke;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MActionCallSuper implements MixinAction {

    private MixinPlugin plugin;

    public MActionCallSuper(MixinPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void action(Class<?> owner, MethodNode method) {
        Method m = null;
        Method superMethod = null;
        for (Method declaredMethod : owner.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(method.name)) {
                if (ASMUtils.getDescriptor(declaredMethod.getReturnType(), declaredMethod.getParameterTypes()).equals(method.desc)) {
                    m = declaredMethod;
                }
            }
        }
        if (m != null) {
            Class<?> superclass = owner.getSuperclass();
            while (superclass != null) {
                for (Method declaredMethod : superclass.getDeclaredMethods()) {
                    if (declaredMethod.getName().equals(method.name)) {
                        if (ASMUtils.getDescriptor(declaredMethod.getReturnType(), declaredMethod.getParameterTypes()).equals(method.desc)) {
                            superMethod = declaredMethod;
                        }
                    }
                }
                superclass = superclass.getSuperclass();
            }
        }
        if (superMethod == null) {
            throw new IllegalArgumentException("Could not find super method in " + owner.getSimpleName());
        }
        method.instructions.clear();
        int varNum = 0;
        if (Modifier.isStatic(superMethod.getModifiers())) {
            method.instructions.add(new VarInsnNode(Opcode.ALOAD, varNum++));
        }
        Class<?>[] parameterTypes = superMethod.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            method.instructions.add(ASMUtils.loadVar(parameterTypes[i], varNum++));
        }
        method.instructions.add(new IShellCodeMethodInvoke(superMethod).generate());
        method.instructions.add(ASMUtils.genReturnNode(superMethod.getReturnType()));

    }
}
