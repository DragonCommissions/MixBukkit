package com.dragoncommissions.mixbukkit.api.action;

import lombok.Getter;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.Arrays;

@Getter
public class MActionInsertMethodCall implements MixinAction {

    private Method invokeMethod;
    private boolean targetStatic;

    public MActionInsertMethodCall(Method method, boolean targetStatic) {
        this.invokeMethod = method;
        this.targetStatic = targetStatic;
    }

    @Override
    public void action(MethodNode method) {
        int highestVarNum = 5;

        for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof VarInsnNode) {
                highestVarNum = Math.max(highestVarNum, ((VarInsnNode) instruction).var);
            }
        }


    }
}
