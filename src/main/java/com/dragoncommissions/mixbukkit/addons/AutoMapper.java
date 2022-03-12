package com.dragoncommissions.mixbukkit.addons;

import com.dragoncommissions.mixbukkit.MixBukkit;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.eclipse.jgit.api.Git;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoMapper {

    private static boolean prepared = false;
    private static File mappingFile;

    @SneakyThrows
    public static InputStream getMappingAsStream() {
        if (!prepared) {
            try {
                prepareMapping();
            } catch (Exception e) {
                if (MixBukkit.DEBUG) {
                    e.printStackTrace();
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Error loading mapping! Have you connected to the internet?");
                if (MixBukkit.SAFE_MODE) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Server shutdown because safe mode is on, not loading mapping correctly may cause critical bugs/saves corruption.");
                    Bukkit.getServer().shutdown();
                    throw e;
                }
            }
            prepared = true;
        }
        if (mappingFile == null) return null; // Don't load any mapping
        return new FileInputStream(mappingFile);
    }
    @SneakyThrows
    private static void prepareMapping() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Detecting mapping...");
        if (!shouldLoadMapping()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[!] You don't need any mapping for this build!");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Detecting mapping...");
        mappingFile = new File("mappings.csrg");
        if (mappingFile.exists()) {
            if (!mappingFile.isDirectory()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[!] Pre-downloaded mapping detected! Using it. If anything went wrong, please try deleting " + ChatColor.DARK_GRAY + mappingFile.getAbsolutePath() + ChatColor.GREEN + " and try again");
                return;
            }
            mappingFile.delete();
        }
        File buildDataDir = new File(System.getProperty("user.home"), "BuildData");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Fetching BuildData version from Spigot API...");
        Gson gson = new Gson();
        URLConnection connection = new URL("https://hub.spigotmc.org/versions/" + getMCVersion() + ".json").openConnection();
        JsonObject object = gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
        String buildDataVersion = object.get("refs").getAsJsonObject().get("BuildData").getAsString();
        Git buildData = null;
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Fetched BuildData Version: " + buildDataVersion + "!");
        if (buildDataDir.exists()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Found Spigot's BuildData cache at " + buildDataDir.getAbsolutePath() + "! Doing some simple verification...");
            try {
                buildData = Git.open(buildDataDir);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Verified! Updating BuildData...");
                buildData.pull().call();
            } catch (Exception e) {
                buildDataDir.delete();
            }
        }
        if (!buildDataDir.exists()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Cloning Spigot's BuildData repository to " + buildDataDir.getAbsolutePath() + " . It should take a while (Usually around 35 MB), but it's a one time process (across every server)");
            buildData = Git.cloneRepository().setURI("https://hub.spigotmc.org/stash/scm/spigot/builddata.git").setDirectory(buildDataDir).call();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Successfully fetched BuildData! Switching to " + buildDataVersion);
        buildData.checkout().setName(buildDataVersion).call();
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Checking version info...");
        VersionInfo versionInfo = gson.fromJson(new FileReader(new File(buildDataDir, "info.json")), VersionInfo.class);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Scanning for members mapping...");
        File classMappings = new File(buildDataDir, "mappings/" + versionInfo.classMappings);
        if (versionInfo.memberMappings == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Didn't find a members mapping! Building one...");
            MapUtil mapUtil = new MapUtil();
            mapUtil.loadBuk(classMappings);
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Downloading Minecraft's Mappings & Building Members Mappings...");
            InputStream inputStream = new URL(versionInfo.mappingsUrl).openConnection().getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true) {
                int read = inputStream.read();
                if (read == -1) break;
                outputStream.write(read);
            }
            mapUtil.makeFieldMaps(new String(outputStream.toByteArray()), mappingFile, true);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[!] Found a pre-built members mapping! Extracting...");
            mappingFile.createNewFile();
            Files.copy(new File(buildDataDir, "mappings/" + versionInfo.memberMappings).toPath(), mappingFile.toPath());
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[!] Finished loading mappings!");

    }

    private static String getMCVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    private static boolean shouldLoadMapping() {
        return Integer.parseInt(getMCVersion().split("\\.")[1]) >= 17 && isObfuscatedBuild();
        // Remapped option is only available after 1.17
    }

    private static boolean isObfuscatedBuild() {
        try {
            Class<?> aClass = Class.forName("net.minecraft.world.entity.EntityLiving");
            return true;
        } catch (Throwable ignored) {}
        return false;
    }

    /**
     * Source: Spigot BuildTools
     */
    @Data
    @AllArgsConstructor
    private static class VersionInfo
    {

        private String minecraftVersion;
        private String accessTransforms;
        private String classMappings;
        private String memberMappings;
        private String packageMappings;
        private String minecraftHash;
        private String classMapCommand;
        private String memberMapCommand;
        private String finalMapCommand;
        private String decompileCommand;
        private String serverUrl;
        private String mappingsUrl;
        private String spigotVersion;
        private int toolsVersion = -1;

        public VersionInfo(String minecraftVersion, String accessTransforms, String classMappings, String memberMappings, String packageMappings, String minecraftHash)
        {
            this.minecraftVersion = minecraftVersion;
            this.accessTransforms = accessTransforms;
            this.classMappings = classMappings;
            this.memberMappings = memberMappings;
            this.packageMappings = packageMappings;
            this.minecraftHash = minecraftHash;
        }

        public VersionInfo(String minecraftVersion, String accessTransforms, String classMappings, String memberMappings, String packageMappings, String minecraftHash, String decompileCommand)
        {
            this.minecraftVersion = minecraftVersion;
            this.accessTransforms = accessTransforms;
            this.classMappings = classMappings;
            this.memberMappings = memberMappings;
            this.packageMappings = packageMappings;
            this.minecraftHash = minecraftHash;
            this.decompileCommand = decompileCommand;
        }

        public String getShaServerHash()
        {
            return hashFromUrl( serverUrl );
        }

        public String getShaMappingsHash()
        {
            return hashFromUrl( mappingsUrl );
        }
        private static final Pattern URL_PATTERN = Pattern.compile( "https://launcher.mojang.com/v1/objects/([0-9a-f]{40})/.*" );

        public static String hashFromUrl(String url)
        {
            if ( url == null )
            {
                return null;
            }

            Matcher match = URL_PATTERN.matcher( url );
            return ( match.find() ) ? match.group( 1 ) : null;
        }
    }
    private static class MapUtil
    {

        private static final Pattern MEMBER_PATTERN = Pattern.compile( "(?:\\d+:\\d+:)?(.*?) (.*?) \\-> (.*)" );
        //
        private List<String> header = new ArrayList<>();
        private final BiMap<String, String> obf2Buk = HashBiMap.create();
        private final BiMap<String, String> moj2Obf = HashBiMap.create();

        public void loadBuk(File bukClasses) throws IOException
        {
            for ( String line : Files.readAllLines( bukClasses.toPath() ) )
            {
                if ( line.startsWith( "#" ) )
                {
                    header.add( line );
                    continue;
                }

                String[] split = line.split( " " );
                if ( split.length == 2 )
                {
                    obf2Buk.put( split[0], split[1] );
                }
            }
        }

        public void makeFieldMaps(String mojIn, File fields, boolean includeMethods) throws IOException
        {
            List<String> lines = new ArrayList<>();
            if ( includeMethods )
            {
                for (String line : mojIn.split("\n")) {
                    lines.add(line);
                    if ( line.startsWith( "#" ) )
                    {
                        continue;
                    }

                    if ( line.endsWith( ":" ) )
                    {
                        String[] parts = line.split( " -> " );
                        String orig = parts[0].replace( '.', '/' );
                        String obf = parts[1].substring( 0, parts[1].length() - 1 ).replace( '.', '/' );

                        moj2Obf.put( orig, obf );
                    }
                }
            }

            List<String> outFields = new ArrayList<>( header );

            String currentClass = null;
            outer:
            for ( String line : mojIn.split("\n") )
            {
                if ( line.startsWith( "#" ) )
                {
                    continue;
                }
                line = line.trim();

                if ( line.endsWith( ":" ) )
                {
                    currentClass = null;

                    String[] parts = line.split( " -> " );
                    String orig = parts[0].replace( '.', '/' );
                    String obf = parts[1].substring( 0, parts[1].length() - 1 ).replace( '.', '/' );

                    String buk = deobfClass( obf, obf2Buk );
                    if ( buk == null )
                    {
                        continue;
                    }

                    currentClass = buk;
                } else if ( currentClass != null )
                {
                    Matcher matcher = MEMBER_PATTERN.matcher( line );
                    matcher.find();

                    String obf = matcher.group( 3 );
                    String nameDesc = matcher.group( 2 );
                    if ( !nameDesc.contains( "(" ) )
                    {
                        if ( nameDesc.equals( obf ) || nameDesc.contains( "$" ) )
                        {
                            continue;
                        }
                        if ( !includeMethods && ( obf.equals( "if" ) || obf.equals( "do" ) ) )
                        {
                            obf += "_";
                        }

                        outFields.add( currentClass + " " + obf + " " + nameDesc );
                    } else if ( includeMethods )
                    {
                        String sig = csrgDesc( moj2Obf, obf2Buk, nameDesc.substring( nameDesc.indexOf( '(' ) ), matcher.group( 1 ) );
                        String mojName = nameDesc.substring( 0, nameDesc.indexOf( '(' ) );

                        if ( obf.equals( mojName ) || mojName.contains( "$" ) || obf.equals( "<init>" ) || obf.equals( "<clinit>" ) )
                        {
                            continue;
                        }
                        outFields.add( currentClass + " " + obf + " " + sig + " " + mojName );
                    }
                }
            }

            Collections.sort( outFields );
            fields.createNewFile();
            Files.write( fields.toPath(), outFields );
        }

        public void makeCombinedMaps(File out, File... members) throws IOException
        {
            List<String> combined = new ArrayList<>( header );

            for ( Map.Entry<String, String> map : obf2Buk.entrySet() )
            {
                combined.add( map.getKey() + " " + map.getValue() );
            }

            for ( File member : members )
            {
                for ( String line : Files.readAllLines( member.toPath() ) )
                {
                    if ( line.startsWith( "#" ) )
                    {
                        continue;
                    }
                    line = line.trim();

                    String[] split = line.split( " " );
                    if ( split.length == 3 )
                    {
                        String clazz = split[0];
                        String orig = split[1];
                        String targ = split[2];

                        combined.add( deobfClass( clazz, obf2Buk.inverse() ) + " " + orig + " " + targ );
                    } else if ( split.length == 4 )
                    {
                        String clazz = split[0];
                        String orig = split[1];
                        String desc = split[2];
                        String targ = split[3];

                        combined.add( deobfClass( clazz, obf2Buk.inverse() ) + " " + orig + " " + toObf( desc, obf2Buk.inverse() ) + " " + targ );
                    }
                }
            }

            Files.write( out.toPath(), combined );
        }

        public static String deobfClass(String obf, Map<String, String> classMaps)
        {
            String buk = classMaps.get( obf );
            if ( buk == null )
            {
                StringBuilder inner = new StringBuilder();

                while ( buk == null )
                {
                    int idx = obf.lastIndexOf( '$' );
                    if ( idx == -1 )
                    {
                        return null;
                    }
                    inner.insert( 0, obf.substring( idx ) );
                    obf = obf.substring( 0, idx );

                    buk = classMaps.get( obf );
                }

                buk += inner;
            }
            return buk;
        }

        public static String toObf(String desc, Map<String, String> map)
        {
            desc = desc.substring( 1 );
            StringBuilder out = new StringBuilder( "(" );
            if ( desc.charAt( 0 ) == ')' )
            {
                desc = desc.substring( 1 );
                out.append( ')' );
            }
            while ( desc.length() > 0 )
            {
                desc = obfType( desc, map, out );
                if ( desc.length() > 0 && desc.charAt( 0 ) == ')' )
                {
                    desc = desc.substring( 1 );
                    out.append( ')' );
                }
            }
            return out.toString();
        }

        public static String obfType(String desc, Map<String, String> map, StringBuilder out)
        {
            int size = 1;
            switch ( desc.charAt( 0 ) )
            {
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'S':
                case 'Z':
                case 'V':
                    out.append( desc.charAt( 0 ) );
                    break;
                case '[':
                    out.append( "[" );
                    return obfType( desc.substring( 1 ), map, out );
                case 'L':
                    String type = desc.substring( 1, desc.indexOf( ";" ) );
                    size += type.length() + 1;
                    out.append( "L" ).append( map.containsKey( type ) ? map.get( type ) : type ).append( ";" );
            }
            return desc.substring( size );
        }

        private static String csrgDesc(Map<String, String> first, Map<String, String> second, String args, String ret)
        {
            String[] parts = args.substring( 1, args.length() - 1 ).split( "," );
            StringBuilder desc = new StringBuilder( "(" );
            for ( String part : parts )
            {
                if ( part.isEmpty() )
                {
                    continue;
                }
                desc.append( toJVMType( first, second, part ) );
            }
            desc.append( ")" );
            desc.append( toJVMType( first, second, ret ) );
            return desc.toString();
        }

        private static String toJVMType(Map<String, String> first, Map<String, String> second, String type)
        {
            switch ( type )
            {
                case "byte":
                    return "B";
                case "char":
                    return "C";
                case "double":
                    return "D";
                case "float":
                    return "F";
                case "int":
                    return "I";
                case "long":
                    return "J";
                case "short":
                    return "S";
                case "boolean":
                    return "Z";
                case "void":
                    return "V";
                default:
                    if ( type.endsWith( "[]" ) )
                    {
                        return "[" + toJVMType( first, second, type.substring( 0, type.length() - 2 ) );
                    }
                    String clazzType = type.replace( '.', '/' );
                    String obf = deobfClass( clazzType, first );
                    String mappedType = deobfClass( ( obf != null ) ? obf : clazzType, second );

                    return "L" + ( ( mappedType != null ) ? mappedType : clazzType ) + ";";
            }
        }
    }


}
