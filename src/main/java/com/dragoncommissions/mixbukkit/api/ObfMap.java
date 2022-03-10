package com.dragoncommissions.mixbukkit.api;

import com.dragoncommissions.mixbukkit.MixBukkit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ObfMap {

    // Deobf -> obf
    private Map<FieldMapping, String> fieldMappings = new HashMap<>();
    private Map<MethodMapping, String> methodMappings = new HashMap<>();

    public ObfMap(InputStream memberMap) {
        if (memberMap == null) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Mapping is null");
            }
            return;
        }
        Scanner scanner = new Scanner(memberMap);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("#")) continue;
            String[] s = line.split(" ");
            if (s.length == 4) { // Method mapping, <owner> <obf> <desc> <deobf>
                methodMappings.put(new MethodMapping(s[0], s[2], s[3]), s[1]);
            } else if (s.length == 3) { // Field mapping, <owner> <obf> <deobf>
                fieldMappings.put(new FieldMapping(s[0], s[2]), s[1]);
            } else {
                System.out.println("Illegal Mapping: " + line + "   (Length: " + s.length + ")");
            }
        }
    }

    public String resolveMapping(FieldMapping fieldMapping) {
        String s = fieldMappings.get(fieldMapping);
        if (s == null) return fieldMapping.getFieldName();
        return s;
    }

    public String resolveMapping(MethodMapping methodMapping) {
        String s = methodMappings.get(methodMapping);
        if (s == null) return methodMapping.getMethodName();
        return s;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @ToString
    public static class FieldMapping {
        private String ownerName; // Replaced / with .
        private String fieldName;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @ToString
    public static class MethodMapping {
        private String ownerName; // Replaced / with .
        private String descriptor;
        private String methodName;
    }

}
