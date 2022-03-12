package com.dragoncommissions.mixbukkit.api.shellcode.impl.inner;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@ShellCodeInfo(
        name = "Method Invoke",
        description = "Call methods programmatically",
        requireVarManager = false,
        stacksContent = {"Return value of invoked method"},
        requiredStacksContent = {"Object that calls the method", "Arguments (in order)"},
        calledDirectly = false
)
//@AllArgsConstructor
@Getter
public class ShellCodeMethodInvoke extends ShellCode {

    private Method method;

    public ShellCodeMethodInvoke(Method method) {
        this.method = method;
    }

    @Override
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList list = new InsnList();
        list.add(new MethodInsnNode(
                Modifier.isStatic(method.getModifiers()) ? Opcode.INVOKESTATIC:(method.getDeclaringClass().isInterface()?Opcode.INVOKEINTERFACE:Opcode.INVOKEVIRTUAL),
                method.getDeclaringClass().getName().replace(".", "/"),
                method.getName(),
                ASMUtils.getDescriptor(method.getReturnType(), method.getParameterTypes()),
                method.getDeclaringClass().isInterface() && !Modifier.isStatic(method.getModifiers())
                ));
        return list;
    }
}
