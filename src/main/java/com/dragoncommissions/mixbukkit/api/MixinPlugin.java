package com.dragoncommissions.mixbukkit.api;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.agent.ClassesManager;
import com.dragoncommissions.mixbukkit.api.action.MixinAction;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.List;

public class MixinPlugin {

    @Getter
    private ObfMap obfMap;
    @Getter
    private Plugin plugin;

    private List<String> registeredMixins = new ArrayList<>();

    public MixinPlugin(Plugin plugin, ObfMap obfMap) {
        this.plugin = plugin;
        this.obfMap = obfMap;
    }

    @SneakyThrows
    public boolean registerMixin(String namespace, MixinAction mixinAction, Class<?> owner, String deObfMethodName, Class<?> returnType, Class<?>... arguments) {
        if (registeredMixins.contains(namespace)) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Mixin with namespace: " + namespace + " is already registered! Skipping...");
            }
            return false;
        }
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Registering Mixin: " + plugin.getName() + ":" + namespace);
        }
        String descriptor = ASMUtils.getDescriptor(returnType, arguments);
        String obfMethodName = obfMap.resolveMapping(new ObfMap.MethodMapping(owner.getName().replace(".", "/"), descriptor, deObfMethodName));
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Obfuscated method name: " + obfMethodName);
        }

        ClassNode classNode = ClassesManager.getClassNode(owner.getName());
        if (classNode == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find target class: " + owner.getName());
            return false;
        }
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(obfMethodName) && method.desc.equals(descriptor)) {
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Found Method to hook!");
                }

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Processing...");
                }
                mixinAction.action(owner, method);

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Assembling...");
                }
                byte[] data = ASMUtils.fromClassNode(classNode);
                try {
                    if (MixBukkit.DEBUG) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Redefining class...");
                    }

                    MixBukkit.INSTRUMENTATION.redefineClasses(new ClassDefinition(owner, data));
                    ClassesManager.classNodes.put(owner.getName(), classNode);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not redefine class: " + owner.getSimpleName());
                }
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Successfully hooked " + namespace);
                }
                registeredMixins.add(namespace);
                return true;
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find target method");
        return false;
    }

}
