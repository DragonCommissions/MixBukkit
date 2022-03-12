package com.dragoncommissions.mixbukkit.api.shellcode;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public interface IShellCode {

    InsnList generate(MethodNode methodNode, LocalVarManager varManager);

}
