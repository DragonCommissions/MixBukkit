package com.dragoncommissions.mixbukkit.utils;

import com.dragoncommissions.mixbukkit.MixBukkit;
import javassist.CtClass;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
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
        File file = new File("/tmp/trol.class");
        FileOutputStream writer = new FileOutputStream(file);
        writer.write(ctClass.toBytecode());
        writer.close();
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
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        if (MixBukkit.DEBUG) {
            try {
                File outFile = new File("/tmp/" + UUID.randomUUID() + ".class");
                System.out.println("Wrote to " + outFile);
                FileOutputStream outputStream = new FileOutputStream(outFile);
                outputStream.write(writer.toByteArray());
                outputStream.close();
            } catch (Exception ignored) {}
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


    public static void test(int a, float b, double c, char d, boolean e, byte f, short g, String h, String[] i, int[] j) {
        try {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                ClassLoader classLoader = plugin.getClass().getClassLoader();
                Class<?> loadedClass = Class.forName("CLASS_NAME_HERE", true, classLoader);
                Method methodNameHere = loadedClass.getDeclaredMethod("methodNameHere", int.class, float.class, double.class, char.class, boolean.class, byte.class, short.class, String.class, String[].class, int[].class);
                methodNameHere.invoke(null, a, b, c, d, e, f, g, h, i, j);
                break;
            }
        } catch (Exception ignored) {}
    }

}
