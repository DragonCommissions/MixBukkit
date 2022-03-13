package com.dragoncommissions.mixbukkit.api.shellcode.impl.api;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@ShellCodeInfo(
        name = "Comment",
        description = "Leave a comment, and it won't do anything other than wasting performance",
        calledDirectly = true
)
@RequiredArgsConstructor
public class ShellCodeComment extends ShellCode {

    @NonNull
    private String comment;

    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        return ASMUtils.asInsnList(
                new LdcInsnNode(comment),
                new InsnNode(Opcode.POP)
        );
    }
}
