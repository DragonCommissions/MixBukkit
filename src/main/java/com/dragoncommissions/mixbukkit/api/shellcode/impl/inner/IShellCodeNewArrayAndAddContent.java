package com.dragoncommissions.mixbukkit.api.shellcode.impl.inner;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

@ShellCodeInfo(
        name = "New Array and Add Content",
        description = "Create a new array, set the content of it (by for looping), and pushes the array to stack",
        requireVarManager = false,
        stacksContent = {"The created array"},
        requiredStacksContent = {},
        calledDirectly = false
)
@AllArgsConstructor
@Getter
public class IShellCodeNewArrayAndAddContent extends ShellCode {

    private int arraySize;
    private Class<?> type;
    private ForLoopInstructionGenerator gen;

    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList out = new InsnList();
        out.add(ASMUtils.pushInt(arraySize));
        out.add(new TypeInsnNode(Opcode.ANEWARRAY, type.getName().replace(".", "/")));
        for (int i = 0; i < arraySize; i++) {
            out.add(new InsnNode(Opcode.DUP));
            out.add(ASMUtils.pushInt(i));
            out.add(gen.generate(i));
            out.add(new InsnNode(Opcode.AASTORE));
        }
        return out;
    }

    public interface ForLoopInstructionGenerator {
        InsnList generate(int index);
    }

}
