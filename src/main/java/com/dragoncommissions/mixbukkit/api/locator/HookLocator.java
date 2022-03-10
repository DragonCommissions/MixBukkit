package com.dragoncommissions.mixbukkit.api.locator;

import org.objectweb.asm.tree.MethodNode;

public interface HookLocator {

    /**
     * Process a method, and return the line number to hook at. Return empty array if it fails to find target hook location
     * @param methodNode MethodNode input
     * @return Array of line numbers, it will be inserted, means 0 will make first instruction into hooking instruction
     */
    int[] getLineNumber(MethodNode methodNode);

}
