package com.dragoncommissions.mixbukkit;

import com.dragoncommissions.mixbukkit.agent.ClassesManager;
import com.dragoncommissions.mixbukkit.agent.JVMAttacher;
import com.dragoncommissions.mixbukkit.api.ObfMap;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public class MixBukkit extends JavaPlugin {

    public final static String VERSION               = "0.1";
    public final static BuildType BUILD_TYPE         = BuildType.SNAPSHOT;
    public static boolean DEBUG                      = BUILD_TYPE.isDevBuild();
    public static Instrumentation INSTRUMENTATION    = null;
    public static boolean PREPARED                   = false;

    @Getter
    private File pluginFile;

    @Getter
    private static JVMAttacher jvmAttacher;

    public static ClassesManager classesManager;


    @Getter
    private static final Map<String, MixinPlugin> plugins = new HashMap<>();

    @Override
    public void onDisable() {

    }

    @Override
    @SneakyThrows
    public void onEnable() {
        URLClassLoader parent = ((URLClassLoader) getClassLoader().getParent());
        pluginFile = getFile();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "=-=-=-=-= MixBukkit Loader =-=-=-=-=");
        getServer().getConsoleSender().sendMessage(ChatColor.RED     + "!! WARNING !!");
        getServer().getConsoleSender().sendMessage(ChatColor.RED     + "/reload is not supported yet! Don't use /reload");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Version: " + VERSION);
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Build Type: " + BUILD_TYPE + (BUILD_TYPE.isDevBuild()?(ChatColor.RED + "  DON'T USE IT IN PRODUCTION SERVER"):""));
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Agent: " + pluginFile);
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "~~ Started loading ~~");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + " - Attaching to JVM...");
        jvmAttacher = new JVMAttacher(this);
        jvmAttacher.attach();
        if (INSTRUMENTATION == null) {
            setEnabled(false);
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "- Failed grabbing instrumentation! If you believe this is an issue, please open a ticket");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "======= FAILED GETTING INSTRUMENTATION ======");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please check those things before opening an issue:");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "1. Do you have -XX:+DisableAttachMechanism? If yes, remove it from server start command.");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "2. Does the server have permission to spawn a process? If no, give it. Normally yes unless you are using server panel that limits the privilege");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "");
            throw new NullPointerException("Instrumentation is null");
        }
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "- Finished Attaching!");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "- Preparing class pool...");
        ClassesManager.init();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "- Finished preparing class pool!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "[!] Finished loading MixBukkit!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        PREPARED = true;
    }

    public static MixinPlugin registerMixinPlugin(Plugin plugin, InputStream membersMapStream) {
        MixinPlugin mixinPlugin = plugins.get(plugin.getName());
        if (mixinPlugin != null) {
            return mixinPlugin;
        }
        mixinPlugin = new MixinPlugin(plugin, new ObfMap(membersMapStream));
        plugins.put(plugin.getName(), mixinPlugin);
        try {
            Method getFile = JavaPlugin.class.getDeclaredMethod("getFile");
            getFile.setAccessible(true);
            File pluginFile = ((File) getFile.invoke(plugin));
            pluginFile = pluginFile.getAbsoluteFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return mixinPlugin;
    }

    @SneakyThrows
    public static void addLibrary(File file) {
        INSTRUMENTATION.appendToSystemClassLoaderSearch(new JarFile(file));

        if (DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Loading " + file.getAbsolutePath());
        }
    }

    @AllArgsConstructor
    @Getter
    public enum BuildType {
        SNAPSHOT(true),
        BETA(false),
        RELEASE(false);

        boolean devBuild;
    }
}
