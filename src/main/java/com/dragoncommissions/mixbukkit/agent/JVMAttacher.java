package com.dragoncommissions.mixbukkit.agent;

import com.dragoncommissions.mixbukkit.MixBukkit;
import io.github.karlatemp.unsafeaccessor.UnsafeAccess;
import io.github.kasukusakura.jsa.JvmSelfAttach;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.management.ManagementFactory;

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
