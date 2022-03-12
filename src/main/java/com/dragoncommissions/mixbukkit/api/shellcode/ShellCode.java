package com.dragoncommissions.mixbukkit.api.shellcode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public abstract class ShellCode implements IShellCode {

    public InsnList generate() {
        return generate(null, null);
    }

    public InsnList popExtraStack() {
        InsnList list = new InsnList();
        for (int i = 0; i < getShellCodeInfo().stacksContent().length; i++) {
            list.add(new InsnNode(Opcodes.POP));
        }
        return list;
    }

    public ShellCodeInfo getShellCodeInfo() {
        try {
            return getClass().getAnnotationsByType(ShellCodeInfo.class)[0];
        } catch (Exception ignored) {
            return null;
        }
    }

}
