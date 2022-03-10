package com.dragoncommissions.mixbukkit.api;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.agent.ClassesManager;
import com.dragoncommissions.mixbukkit.api.handler.MHandlerMethod;
import com.dragoncommissions.mixbukkit.api.handler.MixinHandler;
import com.dragoncommissions.mixbukkit.api.locator.HookLocator;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.CtClass;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MixinPlugin {

    private ObfMap obfMap;
    private Plugin plugin;

    private List<String> registeredMixins = new ArrayList<>();

    public MixinPlugin(Plugin plugin, ObfMap obfMap) {
        this.plugin = plugin;
        this.obfMap = obfMap;
    }

    @SneakyThrows
    public boolean registerMixin(String namespace, HookLocator hookLocator, MixinHandler mixinHandler, Class<?> owner, String deObfMethodName, Class<?> returnType, Class<?>... arguments) {
        if (registeredMixins.contains(namespace)) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Mixin with namespace: " + namespace + " is already registered! Skipping...");
            }
        }
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Registering Mixin: " + plugin.getName() + ":" + namespace);
        }
        String descriptor = ASMUtils.getDescriptor(returnType, arguments);
        String obfMethodName = obfMap.resolveMapping(new ObfMap.MethodMapping(owner.getName().replace(".", "/"), descriptor, deObfMethodName));
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Obfuscated method name: " + obfMethodName);
        }
        CtClass ctClass = ClassesManager.getClass(owner.getName());
        if (ctClass == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find target class: " + owner.getName());
            return false;
        }
        ClassNode classNode = ASMUtils.toClassNode(ctClass);
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(obfMethodName) && method.desc.equals(descriptor)) {

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Found Method to hook!");
                }

                // Copy hookLocator.getLineNumber(method) to listHooks
                int[] hooks = hookLocator.getLineNumber(method);
                List<Integer> listHooks = new ArrayList<>();
                for (int hook : hooks) {
                    listHooks.add(hook);
                }

                // Copy mixinHandler.getInstructions() to insnList
                InsnList insnList = new InsnList();
                AbstractInsnNode[] instructions = mixinHandler.getInstructions();
                for (AbstractInsnNode insnNode : instructions) {
                    insnList.add(insnNode);
                }

                // Hook!
                InsnList newInstructions = new InsnList();
                for (int i = 0; i < method.instructions.size(); i++) {
                    if (listHooks.contains(i)) {
                        newInstructions.add(insnList);
                    }
                    newInstructions.add(method.instructions.get(0));
                }
                method.instructions = newInstructions;
                try {
                    MixBukkit.INSTRUMENTATION.redefineClasses(new ClassDefinition(owner, ASMUtils.fromClassNode(classNode)));
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not redefine class: " + owner.getSimpleName());
                }
                return true;
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find target method");
        return false;
    }

}
