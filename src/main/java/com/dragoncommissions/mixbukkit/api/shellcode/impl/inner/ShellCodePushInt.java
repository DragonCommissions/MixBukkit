package com.dragoncommissions.mixbukkit.api.shellcode.impl.inner;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

@ShellCodeInfo(
        name = "Push Int",
        description = "Push an int into stack"
)
@AllArgsConstructor
@Getter
public class ShellCodePushInt extends ShellCode {

    private int value;

    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList out = new InsnList();
        if (value <= 5 && value >= -1) {
            out.add(new InsnNode(value + 3));
            return out;
        }
        if (value < 255 & value > 0) {
            out.add(new IntInsnNode(Opcode.BIPUSH, value));
            return out;
        }
        out.add(new IntInsnNode(Opcode.SIPUSH, value));
        return out;
    }
}
