package com.dragoncommissions.mixbukkit.utils.io;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.Scanner;

public class BukkitErrorOutputStream extends OutputStream {


    public BukkitErrorOutputStream() {

    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            System.err.print("\u001B[0m");
        }
        System.err.write(b);
        if (b == '\n') {
            System.err.print("\u001B[31m");
        }
    }
}
