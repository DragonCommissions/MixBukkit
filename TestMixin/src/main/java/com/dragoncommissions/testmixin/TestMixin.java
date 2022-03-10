package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.MActionCallSuper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.EnderMan;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMixin extends JavaPlugin {

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, TestMixin.class.getClassLoader().getResourceAsStream("mapping.csrg"));
        try {
            plugin.registerMixin(
                    "Cursed Enderman Moment",
                    new MActionCallSuper(plugin),
                    EnderMan.class, // Target class
                    "hurt",  // Deobfuscated Method Name
                    boolean.class,  // Return Type
                    DamageSource.class, float.class // Parameter Types
            );
        } catch (Exception ignored) {}
    }

}
