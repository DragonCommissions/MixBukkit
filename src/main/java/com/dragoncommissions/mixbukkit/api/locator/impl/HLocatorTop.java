package com.dragoncommissions.mixbukkit.api.locator.impl;

import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import org.objectweb.asm.tree.InsnList;

import java.util.Arrays;
import java.util.List;

public class HLocatorTop implements HookLocator {
    @Override
    public List<Integer> getLineNumber(InsnList insnNodes) {
        return Arrays.asList(0);
    }
}
