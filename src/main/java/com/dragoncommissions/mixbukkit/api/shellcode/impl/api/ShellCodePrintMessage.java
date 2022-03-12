package com.dragoncommissions.mixbukkit.api.shellcode.impl.api;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.*;

import static javassist.bytecode.Opcode.GETSTATIC;
import static javassist.bytecode.Opcode.INVOKEVIRTUAL;

@AllArgsConstructor
@Getter
@ShellCodeInfo(
        name = "stdout Print Message",
        description = "Call a System.out.println",
        requireVarManager = false,
        stacksContent = {},
        requiredStacksContent = {},
        calledDirectly = true
)
public class ShellCodePrintMessage extends ShellCode {

    private String message;


    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add(new LdcInsnNode(message));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
        return list;
    }
}
