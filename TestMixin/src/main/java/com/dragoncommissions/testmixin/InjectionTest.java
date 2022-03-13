package com.dragoncommissions.testmixin;

import org.bukkit.Bukkit;

public class InjectionTest {

    public void injectionTest_methodCallLocator() {
        if (System.currentTimeMillis() % 2 == 0) {
            Bukkit.broadcastMessage("0001");
            return;
        } else {
            Bukkit.broadcastMessage("0002");
        }
        int a = 1 + 1;
        Bukkit.broadcast(a + "hi", "null");
    }

}
