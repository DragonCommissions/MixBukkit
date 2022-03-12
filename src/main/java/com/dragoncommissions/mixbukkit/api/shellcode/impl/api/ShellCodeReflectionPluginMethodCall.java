package com.dragoncommissions.mixbukkit.api.shellcode.impl.api;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.ShellCodeMethodInvoke;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.ShellCodeNewArrayAndAddContent;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.inner.ShellCodePushInt;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;

@ShellCodeInfo(
        name = "Reflection Plugin Method Call",
        description = "It will use a URLClassLoader of a random plugin to load the target plugin class and invoke the method. " +
                "A regular method invoke doesn't work since it uses PluginClassLoader.",
        requireVarManager = true,
        requireMethodNodeModification = true,
        stacksContent = {"Return value of invoked method (as Object)"},
        requiredStacksContent = {},
        calledDirectly = true
)
@AllArgsConstructor
public class ShellCodeReflectionPluginMethodCall extends ShellCode {

    private Method handler;
    private boolean staticTarget;

    private static void action() {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (int i = 0; i < plugins.length; i++) {
            try {
                ClassLoader classLoader = plugins[i].getClass().getClassLoader();
                Class<?> loadedClass = Class.forName("CLASS_NAME_HERE", true, classLoader);
                Method methodNameHere = loadedClass.getDeclaredMethod("methodNameHere", int.class, float.class, double.class, char.class, boolean.class, byte.class, short.class, String.class, String[].class, int[].class);
                methodNameHere.invoke(null);
            } catch (Exception ignored) {}
        }

    }

    @Override
    @SneakyThrows
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        LabelNode toTry = new LabelNode();
        LabelNode onError = new LabelNode();
        LabelNode afterTry = new LabelNode();
        LabelNode finishExecution = new LabelNode();
        LabelNode finishedAll = new LabelNode();
        methodNode.maxStack = 100;

        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(toTry, onError, onError, Throwable.class.getName().replace(".", "/")));
        int plugins = varManager.allocateVarNumber();
        int pluginsLength = varManager.allocateVarNumber();
        int i = varManager.allocateVarNumber();
        int classLoader = varManager.allocateVarNumber();
        int method = varManager.allocateVarNumber();

        InsnList out = new InsnList();

        // Init "i" variable for "for" loop
        // (init i variable for for loop, without quotes it sounds confusing lol)
        out.add(new ShellCodePushInt(0).generate());
        out.add(new VarInsnNode(Opcode.ISTORE, i));
        out.add(new InsnNode(Opcode.ACONST_NULL));
        out.add(new VarInsnNode(Opcode.ASTORE, method));

        // Get plugins
        out.add(getPlugins());
        out.add(new InsnNode(Opcode.DUP)); // Duplicate result of getPlugins for further use (get length of it)
        out.add(new VarInsnNode(Opcode.ASTORE, plugins));

        // Get length of it
        out.add(new InsnNode(Opcode.ARRAYLENGTH));
        out.add(new VarInsnNode(Opcode.ISTORE, pluginsLength));

        // Start trying
        out.add(toTry);
        // Get the class of the method and push to stack
        out.add(new VarInsnNode(Opcode.ALOAD, plugins));
        out.add(new VarInsnNode(Opcode.ILOAD, i));
        out.add(new VarInsnNode(Opcode.ILOAD, i)); // Start i++
        out.add(new ShellCodePushInt(1).generate());
        out.add(new InsnNode(Opcode.IADD));
        out.add(new VarInsnNode(Opcode.ISTORE, i));
        out.add(new InsnNode(Opcode.AALOAD));
        out.add(new ShellCodeMethodInvoke(Object.class.getDeclaredMethod("getClass")).generate());
        out.add(new ShellCodeMethodInvoke(Class.class.getDeclaredMethod("getClassLoader")).generate());
        out.add(new VarInsnNode(Opcode.ASTORE, classLoader));
        out.add(new LdcInsnNode(handler.getDeclaringClass().getName()));
        out.add(new ShellCodePushInt(1).generate()); // true
        out.add(new VarInsnNode(Opcode.ALOAD, classLoader));
        out.add(new ShellCodeMethodInvoke(Class.class.getDeclaredMethod("forName", String.class, boolean.class, ClassLoader.class)).generate());
        // Get the method, and push it to stack
        out.add(new LdcInsnNode(handler.getName()));
        out.add(new ShellCodeNewArrayAndAddContent(handler.getParameterTypes().length, Class.class, index -> {
            InsnList list = new InsnList();
            list.add(ASMUtils.generateGetClassNode(handler.getParameterTypes()[index]));
            return list;
        }).generate());
        out.add(new ShellCodeMethodInvoke(Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class)).generate());
        out.add(new VarInsnNode(Opcode.ASTORE, method));
        out.add(new JumpInsnNode(Opcode.GOTO, finishExecution));

        // What will happen if anything happens on trying
        out.add(onError);
        out.add(new ShellCodeMethodInvoke(Throwable.class.getDeclaredMethod("printStackTrace")).generate());
        out.add(new JumpInsnNode(Opcode.GOTO, afterTry));

        out.add(afterTry);
        // if i < pluginsLength -> go toTry & execute i++
        out.add(new VarInsnNode(Opcode.ILOAD, i));
        out.add(new VarInsnNode(Opcode.ILOAD, pluginsLength));
        out.add(new JumpInsnNode(Opcode.IF_ICMPLT, toTry));
        // Or.. if not, count it as finish execution, and throw NPE if method is not found
        out.add(new JumpInsnNode(Opcode.GOTO, finishExecution));

        out.add(finishExecution);
        // Call the method!
        out.add(new VarInsnNode(Opcode.ALOAD, method));
        out.add(new InsnNode(Opcode.ACONST_NULL));
        out.add(new ShellCodeNewArrayAndAddContent(handler.getParameterTypes().length, Object.class, index -> {
            return ASMUtils.castToObject(index, handler.getParameterTypes()[index]);
        }).generate());
        out.add(new ShellCodeMethodInvoke(Method.class.getDeclaredMethod("invoke", Object.class, Object[].class)).generate());

        return out;
    }

    @SneakyThrows
    private static InsnList getPlugins() {
        InsnList out = new InsnList();
        out.add(new ShellCodeMethodInvoke(Bukkit.class.getDeclaredMethod("getPluginManager")).generate());
        out.add(new ShellCodeMethodInvoke(PluginManager.class.getDeclaredMethod("getPlugins")).generate());
        return out;
    }
}
