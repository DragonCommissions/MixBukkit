package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.MActionInsertHandler;
import com.dragoncommissions.mixbukkit.api.handler.MHandlerMethod;
import com.dragoncommissions.mixbukkit.api.locator.HLocatorReturn;
import com.dragoncommissions.mixbukkit.api.locator.HLocatorTop;
import net.minecraft.world.level.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMixin extends JavaPlugin {

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        MixinPlugin mixinPlugin = MixBukkit.registerMixinPlugin(this, TestMixin.class.getClassLoader().getResourceAsStream("mapping.csrg"));
        try {
            mixinPlugin.registerMixin("Hook Tick",
                    new MActionInsertHandler(new MHandlerMethod(TestMixin.class.getDeclaredMethod("tick"), false), new HLocatorTop()),
                    Level.class,
                    "tickBlockEntities",
                    void.class);
            mixinPlugin.registerMixin("Hook Tick 2",
                    new MActionInsertHandler(new MHandlerMethod(TestMixin.class.getDeclaredMethod("tic2"), false), new HLocatorReturn()),
                    Level.class,
                    "tickBlockEntities",
                    void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tick() {
        System.out.println("Tick!");
    }

    public static void tic2() {
        System.out.println("end Tick!");
    }

}
