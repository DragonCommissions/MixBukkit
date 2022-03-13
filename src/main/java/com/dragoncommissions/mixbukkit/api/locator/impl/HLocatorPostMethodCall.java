package com.dragoncommissions.mixbukkit.api.locator.impl;

import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class HLocatorPostMethodCall implements HookLocator {

    private Method method;

    @Override
    public List<Integer> getLineNumber(InsnList insnList) {
        String owner = method.getDeclaringClass().getName().replace(".", "/");
        String desc = ASMUtils.getDescriptor(method.getReturnType(), method.getParameterTypes());
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < insnList.size(); i++) {
            if (insnList.get(i) instanceof MethodInsnNode) {
                MethodInsnNode insnNode = (MethodInsnNode) insnList.get(i);
                if (insnNode.owner.equals(owner) && insnNode.name.equals(method.getName()) && insnNode.desc.equals(desc)) {
                    out.add(i + 1);
                }
            }
        }
        return out;
    }
}
