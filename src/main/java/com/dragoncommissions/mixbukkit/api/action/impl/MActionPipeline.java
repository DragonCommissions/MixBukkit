package com.dragoncommissions.mixbukkit.api.action.impl;

import com.dragoncommissions.mixbukkit.api.action.MixinAction;
import org.objectweb.asm.tree.MethodNode;

public class MActionPipeline implements MixinAction {

    private final MixinAction[] actions;

    public MActionPipeline(MixinAction... actions) {
        this.actions = actions;
    }

    @Override
    public void action(Class<?> owner, MethodNode method) {
        for (MixinAction action : actions) {
            action.action(owner, method);
        }
    }
}
