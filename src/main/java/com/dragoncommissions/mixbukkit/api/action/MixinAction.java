package com.dragoncommissions.mixbukkit.api.action;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public interface MixinAction {

    void action(Class<?> owner, MethodNode method);

}
