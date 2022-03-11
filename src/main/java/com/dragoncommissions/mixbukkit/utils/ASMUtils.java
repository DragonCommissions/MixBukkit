package com.dragoncommissions.mixbukkit.utils;

import com.dragoncommissions.mixbukkit.MixBukkit;
import javassist.CtClass;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class ASMUtils {



    public static String toDescriptorTypeName(String type) {
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
                    return "[" + toDescriptorTypeName( type.substring( 0, type.length() - 2 ) );
                }
                String clazzType = type.replace( '.', '/' );

                return "L" + clazzType + ";";
        }
    }

    public static String getDescriptor(Class<?> returnType, Class<?>... arguments) {
        StringBuilder out = new StringBuilder("(");
        for (Class<?> argument : arguments) {
            out.append(toDescriptorTypeName(argument.getName()));
        }
        out.append(")").append(toDescriptorTypeName(returnType.getName()));
        return out.toString();
    }


    @SneakyThrows
    public static AbstractInsnNode loadVar(Class<?> type, int varNumber) {
        String result = "LOAD";
        if (type == byte.class) result = "I" + result;
        else if (type == char.class) result = "I" + result;
        else if (type == double.class) result = "D" + result;
        else if (type == float.class) result = "F" + result;
        else if (type == int.class) result = "I" + result;
        else if (type == long.class) result = "L" + result;
        else if (type == short.class) result = "I" + result;
        else if (type == boolean.class) result = "I" + result;
        else result = "A" + result;
        return new VarInsnNode(((int) Opcode.class.getField(result).get(null)), varNumber);
    }

    @SneakyThrows
    public static AbstractInsnNode genReturnNode(Class<?> type) {
        String result = "RETURN";
        if (type == byte.class) result = "I" + result;
        else if (type == char.class) result = "I" + result;
        else if (type == double.class) result = "D" + result;
        else if (type == float.class) result = "F" + result;
        else if (type == int.class) result = "I" + result;
        else if (type == long.class) result = "L" + result;
        else if (type == short.class) result = "I" + result;
        else if (type == boolean.class) result = "I" + result;
        else result = "A" + result;
        return new InsnNode(((int) Opcode.class.getField(result).get(null)));
    }

    @SneakyThrows
    public static ClassNode toClassNode(CtClass ctClass) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(ctClass.toBytecode());
        reader.accept(node, 0);
        return node;
    }

    @SneakyThrows
    public static ClassNode toClassNode(byte[] bytecode) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytecode);
        reader.accept(node, 0);
        return node;
    }

    @SneakyThrows
    public static byte[] fromClassNode(ClassNode node) {

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        try {
            node.accept(writer);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if (MixBukkit.DEBUG) {
            try {
                File outFile = new File("/tmp/" + UUID.randomUUID() + ".class");
                System.out.println("Wrote to " + outFile);
                FileOutputStream outputStream = new FileOutputStream(outFile);
                outputStream.write(writer.toByteArray());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return writer.toByteArray();
    }


    @SneakyThrows
    public static AbstractInsnNode pushInt(int value) {
        if (value == -1) {
            return new InsnNode(Opcode.ICONST_M1);
        }
        if (value <= 5 && value >= 0) {
            return new InsnNode(value + 3);
        }
        if (value < 255) {
            return new IntInsnNode(Opcode.BIPUSH, value);
        }
        return new IntInsnNode(Opcode.SIPUSH, value);
    }

    public static Class<?> getObjectedType(Class<?> type) {
        if (type == byte.class) return Byte.class;
        else if (type == char.class) return Character.class;
        else if (type == double.class) return Double.class;
        else if (type == float.class) return Float.class;
        else if (type == int.class) return Integer.class;
        else if (type == long.class) return Long.class;
        else if (type == short.class) return Short.class;
        else if (type == boolean.class) return Boolean.class;
        return type;
    }

    public static String getObjectedTypeName(Class<?> type) {
        if (type == byte.class) return Byte.class.getName().replace(".", "/");
        else if (type == char.class) return Character.class.getName().replace(".", "/");
        else if (type == double.class) return Double.class.getName().replace(".", "/");
        else if (type == float.class) return Float.class.getName().replace(".", "/");
        else if (type == int.class) return Integer.class.getName().replace(".", "/");
        else if (type == long.class) return Long.class.getName().replace(".", "/");
        else if (type == short.class) return Short.class.getName().replace(".", "/");
        else if (type == boolean.class) return Boolean.class.getName().replace(".", "/");
        else return "";
    }

    public static AbstractInsnNode generateGetClassNode(Class<?> type) {
        if (type == byte.class) return new FieldInsnNode(Opcode.GETSTATIC, Byte.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == char.class) return new FieldInsnNode(Opcode.GETSTATIC, Character.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == double.class) return new FieldInsnNode(Opcode.GETSTATIC, Double.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == float.class) return new FieldInsnNode(Opcode.GETSTATIC, Float.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == int.class) return new FieldInsnNode(Opcode.GETSTATIC, Integer.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == long.class) return new FieldInsnNode(Opcode.GETSTATIC, Long.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == short.class) return new FieldInsnNode(Opcode.GETSTATIC, Short.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        else if (type == boolean.class) return new FieldInsnNode(Opcode.GETSTATIC, Boolean.class.getName().replace(".", "/"), "TYPE", "Ljava/lang/Class;");
        return new LdcInsnNode(Type.getType(type));
    }

    public static InsnList castToObject(int varLocation, Class<?> type) {
        InsnList list = new InsnList();
        AbstractInsnNode insnNode = loadVar(type, varLocation);
        list.add(insnNode);
        String objectedType = getObjectedTypeName(type);
        if (objectedType.equals("")) return list;
        list.add(new MethodInsnNode(Opcode.INVOKESTATIC, objectedType, "valueOf", getDescriptor(getObjectedType(type), type)));
        return list;
    }


    public static int test(int a, float b, double c, char d, boolean e, byte f, short g, String h, String[] i, int[] j) {
        try {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                ClassLoader classLoader = plugin.getClass().getClassLoader();
                Class<?> loadedClass = Class.forName("CLASS_NAME_HERE", true, classLoader);
                Method methodNameHere = loadedClass.getDeclaredMethod("methodNameHere", int.class, float.class, double.class, char.class, boolean.class, byte.class, short.class, String.class, String[].class, int[].class);
                return (int) methodNameHere.invoke(null, a, b, c, d, e, f, g, h, i, j);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static InsnList generateMethodCall(MethodNode method, int latestVarCount, Method handler, boolean isTargetStatic) {
        InsnList out = new InsnList();
        latestVarCount++;
        int varNumOf11 = latestVarCount++;
        int varNumOf12 = latestVarCount++;
        int varNumOf13 = latestVarCount++;
        int varNumOf14 = latestVarCount++;
        int varNumOf15 = latestVarCount++;
        int varNumOf16 = latestVarCount++;
        int varNumOf17 = latestVarCount++;

        LabelNode label0 = new LabelNode();
        LabelNode label1 = new LabelNode();
        LabelNode label2 = new LabelNode();
        LabelNode label3 = new LabelNode();
        LabelNode label4 = new LabelNode();
        LabelNode label5 = new LabelNode();
        LabelNode label6 = new LabelNode();
        LabelNode label7 = new LabelNode();
        LabelNode label8 = new LabelNode();
        LabelNode label9 = new LabelNode();

        out.add(label0);
        out.add(new MethodInsnNode(184, "org/bukkit/Bukkit", "getPluginManager", "()Lorg/bukkit/plugin/PluginManager;", false));
        out.add(new MethodInsnNode(185, "org/bukkit/plugin/PluginManager", "getPlugins", "()[Lorg/bukkit/plugin/Plugin;", true));
        out.add(new VarInsnNode(58, varNumOf11));
        out.add(new VarInsnNode(25, varNumOf11));
        out.add(new InsnNode(190));
        out.add(new VarInsnNode(54, varNumOf12));
        out.add(new InsnNode(3));
        out.add(new VarInsnNode(54, varNumOf13));
        out.add(label1);
        out.add(new VarInsnNode(21, varNumOf13));
        out.add(new VarInsnNode(21, varNumOf12));
        out.add(new JumpInsnNode(162, label7));
        out.add(new VarInsnNode(25, varNumOf11));
        out.add(new VarInsnNode(21, varNumOf13));
        out.add(new InsnNode(50));
        out.add(new VarInsnNode(58, varNumOf14));
        out.add(label2);
        out.add(new VarInsnNode(25, varNumOf14));
        out.add(new MethodInsnNode(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
        out.add(new MethodInsnNode(182, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false));
        out.add(new VarInsnNode(58, varNumOf15));
        out.add(label3);
        out.add(new LdcInsnNode(handler.getDeclaringClass().getName()));
        out.add(new InsnNode(4));
        out.add(new VarInsnNode(25, varNumOf15));
        out.add(new MethodInsnNode(184, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false));
        out.add(new VarInsnNode(58, varNumOf16));
        out.add(label4);
        out.add(new VarInsnNode(25, varNumOf16));
        out.add(new LdcInsnNode(handler.getName()));
        out.add(ASMUtils.pushInt(handler.getParameterTypes().length));
        out.add(new TypeInsnNode(189, "java/lang/Class"));
        for (int i = 0; i < handler.getParameterTypes().length; i++) {
            out.add(new InsnNode(Opcode.DUP));
            out.add(ASMUtils.pushInt(i));
            out.add(ASMUtils.generateGetClassNode(handler.getParameterTypes()[i]));
            out.add(new InsnNode(Opcode.AASTORE));
        }
        out.add(new MethodInsnNode(182, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));
        out.add(new VarInsnNode(58, varNumOf17));
        out.add(label5);
        out.add(new VarInsnNode(25, varNumOf17));
        out.add(new InsnNode(1));
        out.add(ASMUtils.pushInt(handler.getParameterTypes().length));
        out.add(new TypeInsnNode(189, "java/lang/Object"));
        int baseNumber = isTargetStatic?0:1;
        for (int i = 0; i < handler.getParameterTypes().length; i++) {
            out.add(new InsnNode(Opcode.DUP));
            out.add(ASMUtils.pushInt(i));
            InsnList list = ASMUtils.castToObject(baseNumber + i, handler.getParameterTypes()[i]);
            for (AbstractInsnNode insnNode : list) {
                out.add(insnNode);
            }
            out.add(new InsnNode(Opcode.AASTORE));
        }
        out.add(new MethodInsnNode(182, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false));
        out.add(new InsnNode(87)); // This pops the return value of it
        out.add(label6);
        out.add(new JumpInsnNode(167, label7));
        out.add(label7);
        out.add(new JumpInsnNode(167, label9));
        out.add(label8);
        out.add(new VarInsnNode(58, varNumOf11));
        out.add(label9);
        method.tryCatchBlocks.add(new TryCatchBlockNode(label0, label7, label8, "java/lang/Exception"));
        method.localVariables.add(new LocalVariableNode("classLoader", "Ljava/lang/ClassLoader;", null, label1, label2, varNumOf15));
        method.localVariables.add(new LocalVariableNode("loadedClass", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", label3, label2, varNumOf16));
        method.localVariables.add(new LocalVariableNode("methodNameHere", "Ljava/lang/reflect/Method;", null, label4, label2, varNumOf17));
        method.localVariables.add(new LocalVariableNode("plugin", "Lorg/bukkit/plugin/Plugin;", null, label5, label2, varNumOf14));
        return out;
    }

}
