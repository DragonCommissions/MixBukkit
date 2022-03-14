package com.dragoncommissions.testmixin;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMixin extends JavaPlugin implements Listener {


    @Override
    @SneakyThrows
    public void onEnable() {
        MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, AutoMapper.getMappingAsStream());

//        EDragonArrowShieldRemover.register(plugin);

    }



}
