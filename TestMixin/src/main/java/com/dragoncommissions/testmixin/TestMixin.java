package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.handler.MHandlerMethod;
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
            mixinPlugin.registerMixin("Hook Tick", new HLocatorTop(), new MHandlerMethod(TestMixin.class.getDeclaredMethod("tick"), false), Level.class, "tickBlockEntities", void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        System.out.println("Tick!");
    }

}
