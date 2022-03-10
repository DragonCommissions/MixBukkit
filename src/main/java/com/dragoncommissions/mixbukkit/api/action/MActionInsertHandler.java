package com.dragoncommissions.mixbukkit.api.action;

import com.dragoncommissions.mixbukkit.api.handler.MixinHandler;
import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MActionInsertHandler implements MixinAction {

    private MixinHandler mixinHandler;
    private HookLocator hookLocator;


    @Override
    public void action(Class<?> owner, MethodNode method) {
        // Copy hookLocator.getLineNumber(method) to listHooks
        List<Integer> hooks = hookLocator.getLineNumber(method.instructions);


        int highestVarNum = 5;

        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof VarInsnNode) {
                highestVarNum = Math.max(highestVarNum, ((VarInsnNode) instruction).var);
            }
        }

        // Hook!
        InsnList newInstructions = new InsnList();
        for (int i = 0; i < method.instructions.size(); i++) {
            if (hooks.contains(i)) {
                AbstractInsnNode[] instructions = mixinHandler.getInstructions(method, highestVarNum);
                for (AbstractInsnNode instruction : instructions) {
                    newInstructions.add(instruction);
                    if (instruction instanceof VarInsnNode) {
                        highestVarNum = Math.max(highestVarNum, ((VarInsnNode) instruction).var);
                    }
                }
            }
            newInstructions.add(method.instructions.get(i));
        }
        method.instructions = newInstructions;
    }
}
