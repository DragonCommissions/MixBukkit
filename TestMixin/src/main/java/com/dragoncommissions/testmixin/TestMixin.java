package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorPostMethodCall;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorPreMethodCall;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorTop;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeComment;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMixin extends JavaPlugin implements Listener {

    @Override
    public void onDisable() {

    }

    public void shellCodeCommentTest() {

    }

    @Override
    public void onEnable() {
        MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, AutoMapper.getMappingAsStream());
        try {
            plugin.registerMixin(
                    "Pre Method Call Test",
                    new MActionInsertShellCode(
                            new ShellCodeComment("Pre Method Call"),
                            new HLocatorPreMethodCall(Bukkit.class.getDeclaredMethod("broadcastMessage", String.class))
                    ),
                    InjectionTest.class, // Target class
                    "injectionTest_methodCallLocator",  // Deobfuscated Method Name
                    void.class // Return Type
            );
            plugin.registerMixin(
                    "Post Method Call Test",
                    new MActionInsertShellCode(
                            new ShellCodeComment("Post Method Call"),
                            new HLocatorPostMethodCall(Bukkit.class.getDeclaredMethod("broadcastMessage", String.class))
                    ),
                    InjectionTest.class, // Target class
                    "injectionTest_methodCallLocator",  // Deobfuscated Method Name
                    void.class // Return Type
            );
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

}
