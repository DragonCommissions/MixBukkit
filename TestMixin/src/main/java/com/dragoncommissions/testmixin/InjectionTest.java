package com.dragoncommissions.testmixin;

import org.bukkit.Bukkit;

public class InjectionTest {

    public InjectionTest testValue;

    public void injectionTest_methodCallLocator() {
        if (System.currentTimeMillis() % 2 == 0) {
            Bukkit.broadcastMessage("0001");
            testValue = this;
            String reader = testValue.toString();
            return;
        } else {
            Bukkit.broadcastMessage("0002");
            testValue = null;
        }
        int a = 1 + 1;
        Bukkit.broadcast(a + "hi", "null");
    }

}
