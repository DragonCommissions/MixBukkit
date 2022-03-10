package com.dragoncommissions.mixbukkit.api.locator;

import org.objectweb.asm.tree.MethodNode;

public class HLocatorTop implements HookLocator {
    @Override
    public int[] getLineNumber(MethodNode methodNode) {
        return new int[1];
    }
}
