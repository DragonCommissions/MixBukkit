![MixBukkit](https://github.com/fan87/MixBukkit/raw/master/MixBukkit.png)
MixBukkit is a mixin framework inspired by SpongePowered's Mixin framework using ASM.

## When will you need it
Let's say you want to hook something in `Skeleton.class`, but obviously you can't just edit spigot source code. Having a custom bulid just for a plugin is ridiculous. MixBukkit provides an easy to use API that allows you to hook anything in NMS/CraftBukkit/Spigot API/Plugins and even libraries.

## Environment
In theory, it should work from Java 8 ~ Java latest, Linux & Windows & MacOS, but I only tested it on Linux with Java 17.<br>
Minecraft version is not limited, but it will result in mapping different

## Basic Usage
### Prepare a working mapping (Optional)
1. Run buildtool with this following command: `java -jar BuildTool.jar --rev <version that supports remapping> --remapped`
2. After that, go to your local maven repository (usually `{user.home}/.m2/repository`), and copy `minecraft-server-<version>-maps-spigot-members.csrg`. For example, it's in `/home/fan87/.m2/repository/org/spigotmc/minecraft-server/1.18.1-R0.1-SNAPSHOT/` on my computer
3. Paste that file into the same directory as `plugin.yml`, and name it to anything you want. For example: `mapping.csrg`

If you don't do this step, it's fine, but you'll have to use Bukkit's mapping instead of remapped name.

### Register `MixinPlugin`
```java
// onEnable()
MixinPlugin mixinPlugin = MixBukkit.registerMixinPlugin(this, TestMixin.class.getClassLoader().getResourceAsStream("mapping.csrg" /* Type the mapping location here */));
```
After registering MixinPlugin, you can start registering mixins.


Here's an example:
```java
mixinPlugin.registerMixin("Hook Tick", // Any name you want, use to identify, so reloading plugin won't kill it
                    new MActionInsertHandler( // MAction is shorten name of MaxinAction, basically decides what to do with detected method.
                    // In this case, InsertHandler will insert instructions given by `MixinHandler` into location given by the `HookLocator`
                            new MHandlerMethod(TestMixin.class.getDeclaredMethod("tick"), false),  // MixinHandler, decides what to add. In thise case, it will add a method call (TestMixin.tick())
                            new HLocatorTop() // HookLocator, HLocatorTop will return the first line of code (which is 0), so when method is executed by Minecraft, it will also be executed
                    ),
                    Level.class, // The class you want to hook. `Level.class` is a class in NMS
                    "tickBlockEntities", // The method you want to hook. The name of it depends on your mapping. In this case, `tickBlockEntities()`(remapped.jar) is `R()`(server.jar), but you can pass `tickBlockEntities` instead of unreadable name
                    void.class,  // The return type. This method returns nothing, so `void`
                    new Class[] {} // Arguments. This method has no argument, so leave it empty.
        );
```

## Gallery
![](https://storage.gato.host/61068f9c11c02e002297ebf2/ZPMCC0j-t.png)
