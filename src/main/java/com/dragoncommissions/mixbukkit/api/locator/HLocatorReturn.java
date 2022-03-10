package com.dragoncommissions.mixbukkit.api.locator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

public class HLocatorReturn implements HookLocator {
    @Override
    public List<Integer> getLineNumber(InsnList insnList) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < insnList.size(); i++) {
            AbstractInsnNode insnNode = insnList.get(i);
            if (insnNode.getOpcode() >= 172 && insnNode.getOpcode() <= 177) {
                list.add(i);
            }
        }
        return list;
    }
}
