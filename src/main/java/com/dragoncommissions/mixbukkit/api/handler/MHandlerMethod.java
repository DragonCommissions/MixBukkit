package com.dragoncommissions.mixbukkit.api.handler;

import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MHandlerMethod implements MixinHandler {

    private Method handler;
    private boolean isTargetStatic;

    public MHandlerMethod(Method handler, boolean isTargetStatic) {
        this.handler = handler;
        this.isTargetStatic = isTargetStatic;
    }

    @Override
    public InsnList getInstructions(MethodNode method, int latestVarCount) {
        return ASMUtils.generateMethodCall(method, latestVarCount, handler, isTargetStatic);
    }
}
