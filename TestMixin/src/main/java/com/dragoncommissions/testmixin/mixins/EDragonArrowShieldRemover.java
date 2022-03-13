package com.dragoncommissions.testmixin.mixins;

import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.action.impl.MActionInsertShellCode;
import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorHead;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.ShellCodeReflectionMixinPluginMethodCall;
import lombok.SneakyThrows;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.bukkit.Bukkit;

public class EDragonArrowShieldRemover {

    @SneakyThrows
    public static void register(MixinPlugin plugin) {
        plugin.registerMixin("dragon_arrow_blocker_remove",
                    new MActionInsertShellCode(
                            new ShellCodeReflectionMixinPluginMethodCall(EDragonArrowShieldRemover.class.getDeclaredMethod("onHurt", AbstractDragonSittingPhase.class, DamageSource.class, float.class, CallbackInfo.class)),
                            new HLocatorHead()
                    ), AbstractDragonSittingPhase.class, "onHurt", float.class, DamageSource.class, float.class);
    }

    public static void onHurt(AbstractDragonSittingPhase phase, DamageSource source, float damage, CallbackInfo info) {
        if (source.getDirectEntity() instanceof AbstractArrow) {
            info.setReturned(true);
            info.setReturnValue(damage);
            Bukkit.broadcastMessage("Successfully hit ender dragon with arrow");
        }
    }

}
