package com.dragoncommissions.mixbukkit.agent;

import com.dragoncommissions.mixbukkit.MixBukkit;
import io.github.kasukusakura.jsa.JvmSelfAttach;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.lang.management.ManagementFactory;

public class JVMAttacher {

    private MixBukkit mixBukkit;

    public JVMAttacher(MixBukkit mixBukkit) {
        this.mixBukkit = mixBukkit;
    }

    @SneakyThrows
    public void attach() {
        JvmSelfAttach.init(new File("tmp"));
        MixBukkit.INSTRUMENTATION = JvmSelfAttach.getInstrumentation();

    }

    public int getCurrentPID() {
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

}
