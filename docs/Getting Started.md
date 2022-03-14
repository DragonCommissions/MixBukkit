# Getting Started
In this tutorial, I'll be teaching you how to start coding your first mixin plugin!
## Out of date
This document is outdated. Require an update.

## Requirements
1. Some basic Java knowledge
2. Some basic Reflection knowledge
3. Understand that doing hacky things like this won't make your plugin cross version support, unless you do things other than things I mentioned in this tutorial.

## Add Dependency
We haven't published an artifact to any repository yet, but you can install it to your
local maven repository or add it as a system scoped library.
### Method 1: Build & Install it to your maven repository
1. Clone this repository:
```shell
$ git clone https://github.com/DragonCommissions/MixBukkit
```
2. Build MixBukkit project:
```shell
$ mvn clean package install
```
> Note: Install maven `mvn` command doesn't work
3. Add MixBukkit to your project dependencies:
```xml
<dependency>
    <groupId>com.dragoncommissions</groupId>
    <artifactId>MixBukkit</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Method 2: Install it from command line
```shell
$ mvn install:install-file -Dfile=<path to MixBukkit.jar (plugin file)> -DgroupId=com.dragoncommissions -DartifactId=MixBukkit -Dversion=1.0-SNAPSHOT -Dpackaging=jar
```
```xml
<dependency>
    <groupId>com.dragoncommissions</groupId>
    <artifactId>MixBukkit</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Method 3: Add the plugin jar as library
```xml
<dependency>
    <groupId>com.dragoncommissions</groupId>
    <artifactId>MixBukkit</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${basedir}/"relative path to MixBukkit.jar"</systemPath>
</dependency>
```

> Note: You don't need to shade MixBukkit, shading it won't work.

## Create your first mixin
You don't need any extra file (as resources) for MixBukkit, which means
you can finish all of them in Java. 
Also, If you want a full example, you can check out `TestMixin/`, that's the project
where we test features of MixBukkit. And I'll be using TestMixin as example here.
<br>
There are a few major steps:
1. Get the `MixinPlugin` instance
   1. Obtain Members Mappings (Optional)
2. Add your own mixin

### Get the `MixinPlugin` instance
To do this, you need to obtain a members mappings first.
Luckily, we have a full auto mapper loader that detects version and remap status.
This also means if you are running a remapped version of Spigot, it won't break,
but I won't be talking about this in this tutorial.
<br>
Mapping in MixBukkit is used to improve your coding experience. Since methods of inner minecraft
(also known as NMS, but those classes have their package name now, so I'll call it
inner minecraft) are obfuscated, it's not easy to figure out what are those, so 
people usually use remapped spigot as dependency instead of unmapped. And
when you are using remapped version of it (Check [Spigot's Official Post](https://www.spigotmc.org/threads/spigot-bungeecord-1-17-1-17-1.510208/#post-4184317)), you'll see deobfuscated names, but they
are not their real name, which means if you use those name to find methods with
reflection, it won't find it successfully. With the auto mapper we are having, it will
map the field name string input (let's say: `tickBlockEntities`) into obfuscated name
(`tickBlockEntities` was `R`, so it will be looking for `R` instead of a not existing method: 
`tickBlockEntities`)
<br>
Here's how you do it with auto mapping downloader:
```java
MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, AutoMapper.getMappingAsStream());
```
Here's how you do it with your own mapping:
```java
MixinPlugin plugin = MixBukkit.registerMixinPlugin(this, TestMixin.class.getClassLoader().getResourceAsStream("mappings.csrg"));
```
> Note: Don't forget to replace TestMixin to your main class name, and mappings.csrg to the location of the mapping

### Add your own Mixin
#### Keywords you need to know first
##### ShellCode
Also be known as Bytecode Generator. For example: `ShellCodeReflectionPluginMethodCall` uses reflection to call plugin methods. Every shellcode should get an annotation: `ShellCodeInfo`, which contains information about the shellcode. While implementing your own the shellcode, you should always annotate it with @ShellCodeInfo with required information.
To get a list of ShellCodes, simply type `ShellCode` in your IDE and let it auto completes:
![](https://storage.gato.host/61068f9c11c02e002297ebf2/iwGtPu8wD.png)

##### MixinAction (MAction)
MixinAction has ability to modify entire method,
which is the lowest level of mixin. If you want to do something special
other than inserting shellcode (for example: replace it with a super call, trash the method),
you can use this. Same as shellcode, you can do get a list of MixinAction with `MAction` and let the IDE list them for you.
Here's an example MixinAction (MActionMethodTrasher), which trashes entire method and replace them with nothing. Note that it doesn't work with variables requires a return value:
![](https://storage.gato.host/61068f9c11c02e002297ebf2/ov_KRsORz.png)

##### HookLocator (HLocator)
HookLocator will return a list of instruction index.
Let's say you want to inject a shellcode into the top of the method,
you would want to use `new MActionInsertShellCode(shellCode, new HLocatorTop())`

<br>

#### Example
After knowing some keywords, you can start adding your own mixins.
Let's use `ShellCodeReflectionMixinPluginMethodCall` as example here, since it will
be the most used shellcode.<br>
Here's an example:
```java
plugin.registerMixin(
        "Test Mixin", // Namespace of the mixin, used to identify them/avoid imjecting same mixin multiple times, so any char is allowed
        new MActionInsertShellCode(
                new ShellCodeReflectionMixinPluginMethodCall(TestMixin.class.getDeclaredMethod("hurt", EnderMan.class, DamageSource.class, float.class, CallbackInfo.class), false),
                // If you want a document of ShellCodeReflectionMixinPluginMethodCall, check the docs for that (obviously not Getting Started.md)
                new HLocatorTop()
                // Inject to top of the method
        ),
        EnderMan.class, // Target class
        "hurt",  // Deobfuscated Method Name
        boolean.class,  // Return Type
        DamageSource.class, float.class // Parameter Types
);
```

As you can see, it'll be looking for `TestMixin.hurt()` (Line 4), here's the hurt() example:
```java
public static void hurt(EnderMan test, DamageSource damageSource, float damage, CallbackInfo callBackInfo) {
    Bukkit.broadcastMessage(test.getDisplayName().getString() + " gets hurt from " + damageSource.getMsgId() + "  (Damage amount: " + damage + ")");
    callBackInfo.setReturned(true); // If it's true, it will return something
    callBackInfo.setReturnValue(false); // Return value of it. hurt() in vanilla returns a boolean, so I returned boolean
}
```

## Limitation
Since we don't want to make the framework/library/api very heavy, we decided to
self-attach & redefine classes (like hotswap) instead of transform every class.
If you have ever used HotSwap, you know the limitation. No new method, no new field
, no structure change, and no new class define. I think all you need to beware of is
no new method for overriding. Here's an example:

```java
public class LivingEntity {
   public void hurt(DamageSource source, float damage) {
      // Blablabla
   }
}

public class Skeleton extends LivingEntity {

}
```
In this case, `hurt` is not defined in Skeleton class, but LivingEntity class, you are not allowed
to create mixin in `Skeleton.hurt`, so technically you can't hook into `Skeleton.hurt()`.
This is a pretty bad thing, but if we think outside the box:
```java
 public static void hurt(LivingEntity entity, DamageSource damageSource, float damage, CallbackInfo callBackInfo) {
     if (entity instanceof Skeleton) {
         Bukkit.broadcastMessage(entity.getDisplayName().getString() + " gets hurt from " + damageSource.getMsgId() + "  (Damage amount: " + damage + ")");
         callBackInfo.setReturned(true);
         callBackInfo.setReturnValue(false);
     }
 }
```
Yes, you can just check the class and see if it's an instance of Skeleton.
It's same as hooking into `Skeleton.hurt()`

## Mapping API
While using `HLocatorMethodInvoke`, it requires a java reflection api `Method` object,
which can be obtained with `getDeclaredMethod`, but obviously it won't work
since the method name on the server is obfuscated. We provided an API for this,
you can simply call `plugin.getMethod()` to solve this problem, it takes
basically same parameter as getDeclaredMethod, but it will find the obfuscated version
according to the members map loaded.<br>
