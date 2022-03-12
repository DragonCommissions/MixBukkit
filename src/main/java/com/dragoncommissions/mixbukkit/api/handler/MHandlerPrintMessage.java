package com.dragoncommissions.mixbukkit.api.handler;

import static javassist.bytecode.Opcode.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.*;

@AllArgsConstructor
@Getter
public class MHandlerPrintMessage implements MixinHandler {

    private String message;

    @Override
    public InsnList getInstructions(MethodNode method, int latestVarCount) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "java/io/PrintWriter"));
        list.add(new LdcInsnNode(message));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintWriter", "println", "(Ljava/lang/String;)V"));
        return list;
    }
}
