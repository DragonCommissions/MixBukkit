package com.dragoncommissions.mixbukkit.api.shellcode.impl.api;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.IShellCodeMethodInvoke;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.IShellCodeReflectionMethodInvoke;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

@ShellCodeInfo(
        name = "Reflection Mixin Plugin Method Call",
        description = "It will use a URLClassLoader of a random plugin to load the target plugin class and invoke the method. " +
                "A regular method invoke doesn't work since it uses PluginClassLoader.",
        requireVarManager = true,
        requireMethodNodeModification = true,
        stacksContent = {"Return value of invoked method (as Object)"},
        requiredStacksContent = {},
        calledDirectly = true
)
@AllArgsConstructor
public class ShellCodeReflectionMixinPluginMethodCall extends ShellCode {

    private Method handler;
    private boolean staticTarget;

    @SneakyThrows
    private static void action() {
        Class.forName("CLASS_NAME_HERE", true, Bukkit.getPluginManager().getPlugins()[0].getClass().getClassLoader()).getDeclaredMethod("methodNameHere", int.class, float.class, double.class, char.class, boolean.class, byte.class, short.class, String.class, String[].class, int[].class).invoke(null);
    }

    @Override
    @SneakyThrows
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList out = new InsnList();
        Class<?>[] parameterTypes = handler.getParameterTypes();
        boolean hasCallBackInfo = false;
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (CallbackInfo.class.isAssignableFrom(parameterType) && i == parameterTypes.length - 1) {
                out.add(CallbackInfo.generateCallBackInfo());
                hasCallBackInfo = true;
            } else {
                out.add(ASMUtils.castToObject(i, parameterType));
            }
        }
        IShellCodeReflectionMethodInvoke shellCodeReflectionMethodInvoke = new IShellCodeReflectionMethodInvoke(handler);
        out.add(shellCodeReflectionMethodInvoke.generate(methodNode, varManager));
        if (hasCallBackInfo) {
            Integer varLocation = shellCodeReflectionMethodInvoke.getArgumentVarIndex().get(parameterTypes.length - 1);
            out.add(CallbackInfo.processCallBackInfo(methodNode, varManager, varLocation));
        }
        return out;
    }

    @SneakyThrows
    private static InsnList getPlugins() {
        InsnList out = new InsnList();
        out.add(new IShellCodeMethodInvoke(Bukkit.class.getDeclaredMethod("getPluginManager")).generate());
        out.add(new IShellCodeMethodInvoke(PluginManager.class.getDeclaredMethod("getPlugins")).generate());
        return out;
    }



}
