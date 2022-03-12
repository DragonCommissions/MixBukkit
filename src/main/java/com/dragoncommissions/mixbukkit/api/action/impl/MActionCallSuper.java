package com.dragoncommissions.mixbukkit.api.action.impl;

import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.ObfMap;
import com.dragoncommissions.mixbukkit.api.action.MixinAction;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;
import net.minecraft.world.damagesource.DamageSource;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

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
        method.instructions.add(ASMUtils.loadVar(Object.class, 0));
        method.instructions.add(ASMUtils.loadVar(DamageSource.class, 1));
        method.instructions.add(ASMUtils.loadVar(float.class, 2));
        String descriptor = ASMUtils.getDescriptor(boolean.class, DamageSource.class, float.class);
        String mappingName = plugin.getObfMap().resolveMapping(new ObfMap.MethodMapping(superMethod.getDeclaringClass().getName().replace(".", "/"), descriptor, method.name));
        method.instructions.add(new MethodInsnNode(Opcode.INVOKESPECIAL, superMethod.getDeclaringClass().getName().replace(".", "/"), mappingName, descriptor));
        method.instructions.add(ASMUtils.genReturnNode(superMethod.getReturnType()));

    }
}
