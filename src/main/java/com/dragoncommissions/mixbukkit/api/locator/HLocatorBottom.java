package com.dragoncommissions.mixbukkit.api.locator;

import org.objectweb.asm.tree.MethodNode;

public class HLocatorBottom implements HookLocator {
    @Override
    public int[] getLineNumber(MethodNode methodNode) {
        return new int[] {methodNode.instructions.size() - 1};
    }
}
