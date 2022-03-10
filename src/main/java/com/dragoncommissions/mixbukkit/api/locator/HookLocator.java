package com.dragoncommissions.mixbukkit.api.locator;

import org.objectweb.asm.tree.InsnList;

import java.util.List;

public interface HookLocator {

    /**
     * Process a method, and return the line number to hook at. Return empty array if it fails to find target hook location
     * @param insnList MethodNode input
     * @return Array of line numbers, it will be inserted, means 0 will make first instruction into hooking instruction
     */
    List<Integer> getLineNumber(InsnList insnList);

}
