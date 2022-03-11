package com.dragoncommissions.mixbukkit.api.handler;

import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class MHandlerAppender implements MixinHandler {

    private MixinHandler first;
    private MixinHandler second;


    @Override
    public InsnList getInstructions(MethodNode method, int latestVarCount) {
        InsnList list = new InsnList();
        list.add(first.getInstructions(method, latestVarCount));
        list.add(second.getInstructions(method, latestVarCount));
        return list;
    }
}
