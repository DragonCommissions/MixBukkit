package com.dragoncommissions.mixbukkit.api.shellcode;

import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import lombok.Getter;
import org.objectweb.asm.tree.MethodNode;

public class LocalVarManager {

    @Getter
    private MethodNode methodNode;

    private int latestVarNumber;

    public LocalVarManager(MethodNode methodNode) {
        this.methodNode = methodNode;
        latestVarNumber = ASMUtils.getLatestVarNumber(methodNode.instructions) + 1;
    }

    public int allocateVarNumber() {
        return latestVarNumber++;
    }

    public int getLatestUnusedVarNumber() {
        return latestVarNumber;
    }

}
