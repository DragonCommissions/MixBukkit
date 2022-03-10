package com.dragoncommissions.mixbukkit.api.handler;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

public interface MixinHandler {

    AbstractInsnNode[] getInstructions(MethodNode method, int latestVarCount);

}
