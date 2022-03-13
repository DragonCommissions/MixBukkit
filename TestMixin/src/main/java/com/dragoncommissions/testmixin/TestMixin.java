package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorTop;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMixin extends JavaPlugin implements Listener {

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, AutoMapper.getMappingAsStream());
        try {
            plugin.registerMixin(
                    "Hurt Test",
                    new MActionInsertShellCode(
                            new ShellCodeReflectionMixinPluginMethodCall(TestMixin.class.getDeclaredMethod("hurt", LivingEntity.class, DamageSource.class, float.class, CallbackInfo.class), false),
                            new HLocatorTop()
                    ),
                    LivingEntity.class, // Target class
                    "hurt",  // Deobfuscated Method Name
                    boolean.class,  // Return Type
                    DamageSource.class, float.class // Parameter Types
            );
            getServer().getPluginManager().registerEvents(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hurt(LivingEntity entity, DamageSource damageSource, float damage, CallbackInfo callBackInfo) {
        if (entity instanceof Slime) {
            Bukkit.broadcastMessage(entity.getDisplayName().getString() + " gets hurt from " + damageSource.getMsgId() + "  (Damage amount: " + damage + ")");
            callBackInfo.setReturned(true);
            callBackInfo.setReturnValue(false);
        }
    }

    @EventHandler
    public void onHurt(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            event.setCancelled(true);
        }
    }

}
