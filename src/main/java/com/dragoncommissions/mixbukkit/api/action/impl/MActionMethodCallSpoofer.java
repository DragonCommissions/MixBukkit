package com.dragoncommissions.mixbukkit.api.action.impl;

import com.dragoncommissions.mixbukkit.api.action.MixinAction;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.IShellCodeReflectionMethodInvoke;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@SuppressWarnings("Untested")
public class MActionMethodCallSpoofer implements MixinAction {

    private Method method;
    private String key;
    private Predicate<Integer> filter;

    private static Map<String, ReturnValueGetter> getters = new HashMap<>();

    public static Object get(String value) {
        return getters.get(value).getReturnValue();
    }

    private MActionMethodCallSpoofer(Method method, ReturnValueGetter returnValueGetter, Predicate<Integer> filter) {
        this.method = method;
        if (method.getReturnType() == void.class) throw new IllegalArgumentException("Method: " + method.getName() + " is not returning anything. Nothing to spoof");
        UUID uuid = UUID.randomUUID();
        while (getters.containsKey(uuid.toString())) {
            uuid = UUID.randomUUID();
            System.out.println("Come on! You are so f**king lucky! You literally got same UUID, how?");
            System.out.println("If you see this message without cheating, today is literally your most luckiest day");
            System.out.println("I would say the chance of it is lower than every thing you can think of");
            System.out.println("UUID: " + uuid);
        }
        getters.put(uuid.toString(), returnValueGetter);
        this.key = uuid.toString();
    }

    @Override
    @SneakyThrows
    public void action(Class<?> owner, MethodNode methodNode) {
        InsnList out = new InsnList();
        int amount = 0;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            out.add(instruction);
            if (instruction instanceof MethodInsnNode) {
                MethodInsnNode insn = (MethodInsnNode) instruction;
                if (insn.name.equals(method.getName()) && insn.owner.equals(method.getDeclaringClass().getName().replace(".", "/"))
                    && insn.desc.equals(ASMUtils.getDescriptor(method.getReturnType(), method.getParameterTypes())) && filter.test(amount++)) {
                    out.add(new InsnNode(Opcode.POP)); // Pop the return value first
                    out.add(new LdcInsnNode(key));
                    out.add(new IShellCodeReflectionMethodInvoke(MActionMethodCallSpoofer.class.getDeclaredMethod("get", String.class)).generate());
                    out.add(ASMUtils.cast(method.getReturnType()));
                }
            }
        }
        methodNode.instructions = out;
    }

    public interface ReturnValueGetter {
        Object getReturnValue();
    }

}
