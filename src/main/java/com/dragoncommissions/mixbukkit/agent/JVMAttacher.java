package com.dragoncommissions.mixbukkit.agent;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.sun.tools.attach.VirtualMachine;
import io.github.karlatemp.unsafeaccessor.Unsafe;
import io.github.karlatemp.unsafeaccessor.UnsafeAccess;
import io.github.kasukusakura.jsa.JvmSelfAttach;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JVMAttacher {

    private MixBukkit mixBukkit;

    public JVMAttacher(MixBukkit mixBukkit) {
        this.mixBukkit = mixBukkit;
    }

    @SneakyThrows
    public void attach() {
        JvmSelfAttach.init(new File(System.getProperty("java.io.tmpdir")));
        MixBukkit.INSTRUMENTATION = JvmSelfAttach.getInstrumentation();

    }

    public int getCurrentPID() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    static final UnsafeAccess UA = UnsafeAccess.getInstance();

}
