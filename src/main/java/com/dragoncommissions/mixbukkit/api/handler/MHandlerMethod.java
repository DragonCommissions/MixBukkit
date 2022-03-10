package com.dragoncommissions.mixbukkit.api.handler;

import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MHandlerMethod implements MixinHandler {

    private Method handler;
    private boolean isTargetStatic;

    public MHandlerMethod(Method handler, boolean isTargetStatic) {
        this.handler = handler;
        this.isTargetStatic = isTargetStatic;
    }

    @Override
    public AbstractInsnNode[] getInstructions() {
        List<AbstractInsnNode> out = new ArrayList<>();
        int base = 0;
        if (!isTargetStatic) {
            base += 1;
        }
        for (int i = 0; i < handler.getParameterTypes().length; i++) {
            out.add(ASMUtils.loadVar(handler.getParameterTypes()[i], base + i));
        }
        out.add(new MethodInsnNode(Opcode.INVOKESTATIC, handler.getDeclaringClass().getName().replace(".", "/"), handler.getName(), ASMUtils.getDescriptor(handler.getReturnType(), handler.getParameterTypes())));
        return out.toArray(new AbstractInsnNode[0]);
    }
}
