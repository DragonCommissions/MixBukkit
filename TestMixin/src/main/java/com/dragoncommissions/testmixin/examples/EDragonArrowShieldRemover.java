package com.dragoncommissions.testmixin.examples;

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

    /*
     Original Sourcecode of AbstractDragonSittingPhase#onHurt(DamageSource, float):
     public float onHurt(DamageSource var0, float var1) {
         if (var0.getDirectEntity() instanceof AbstractArrow) {   // A
            var0.getDirectEntity().setSecondsOnFire(1);   // B
            return 0.0F;    // C
         } else {
            return super.onHurt(var0, var1);
         }
     }


     this code prevents players from attacking dragons with arrows while dragon is in sitting phase.
     There's no way to make it possible with vanilla bukkit API other than tracing down EnderDragon#hurt(EnderDragonPart, DamageSource, float)
     Well, that's not a good idea.

     In this case, Mixin is the best solution (if you are fine making it only works with 1 version)

     In this mixin, it modifies the original onHurt method, makes a call to EDragonArrowShieldRemover#onHurt(AbstractDragonSittingPhase, DamageSource, float, CallbackInfo)
     It checks of the damage source is arrow, if it is then it will return the original damage instead of 0 (0 is returned in vanilla if source is arrow)
     Since the "A" will never be called, the arrow will never bounce off again.
     */

    public static void onHurt(AbstractDragonSittingPhase phase, DamageSource source, float damage, CallbackInfo info) {
        if (source.getDirectEntity() instanceof AbstractArrow) {
            info.setReturned(true);
            info.setReturnValue(damage);
            Bukkit.broadcastMessage("Successfully hit ender dragon with arrow");
        }
    }

}
