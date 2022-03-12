package com.dragoncommissions.mixbukkit;

import com.dragoncommissions.mixbukkit.agent.ClassesManager;
import com.dragoncommissions.mixbukkit.agent.JVMAttacher;
import com.dragoncommissions.mixbukkit.api.ObfMap;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.utils.io.BukkitErrorOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class MixBukkit extends JavaPlugin {

    public final static String VERSION                              = "0.1";
    public final static BuildType BUILD_TYPE                        = BuildType.SNAPSHOT;
    public static boolean DEBUG                                     = BUILD_TYPE.isDevBuild();
    public static boolean WRITE_TRANSFORMED_CLASS                   = false;
    public static boolean SAFE_MODE                                 = !DEBUG;
    public static Instrumentation INSTRUMENTATION                   = null;
    public static boolean PREPARED                                  = false;
    public static BukkitErrorOutputStream ERROR_OUTPUT_STREAM       = new BukkitErrorOutputStream();

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

        loadConfig();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "=-=-=-=-= MixBukkit Loader =-=-=-=-=");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Version: " + VERSION);
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Build Type: " + BUILD_TYPE);
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "Agent: " + pluginFile);
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "");
        if (!SAFE_MODE) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Warning: Safe mode is disabled! It might load invalid class and crash the Server/JVM");
        }
        if (!DEBUG) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// If you wish to see debug messages, please enable \"debug-mode\" in your config file");
        } else {
            if (!WRITE_TRANSFORMED_CLASS) {
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// If you wish to see transformed version of class (for testing purposes), you can enable \"write-transformed-class\" in config!");
            }
        }
        if (WRITE_TRANSFORMED_CLASS) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Write output class enabled! Transformed classes will be renamed and go into your temp folder.");
        }
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
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW  + "- Preparing class transformers...");
        ClassesManager.init();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "- Finished preparing class transformers!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "[!] Finished loading MixBukkit!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN   + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        PREPARED = true;
    }

    private void loadConfig() {
        try {

            YamlConfiguration config = new YamlConfiguration();
            File configFile = new File(getDataFolder(), "config.yml");
            if (!getDataFolder().exists()) getDataFolder().mkdirs();
            if (!configFile.exists()) configFile.createNewFile();

            config.load(configFile);
            if (!config.contains("safe-mode")) config.set("safe-mode", SAFE_MODE);
            if (!config.contains("debug-mode")) config.set("debug-mode", DEBUG);
            if (!config.contains("write-transformed-class")) config.set("write-transformed-class", WRITE_TRANSFORMED_CLASS);
            SAFE_MODE = config.getBoolean("safe-mode");
            DEBUG = config.getBoolean("debug-mode");
            WRITE_TRANSFORMED_CLASS = config.getBoolean("write-transformed-class");
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
