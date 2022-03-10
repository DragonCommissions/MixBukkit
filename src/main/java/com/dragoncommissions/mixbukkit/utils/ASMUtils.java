package com.dragoncommissions.mixbukkit.utils;

import javassist.CtClass;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

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
        if (type.isArray()) {
            if (type == byte[].class) return new VarInsnNode(Opcode.IALOAD, varNumber);
            if (type == char[].class) return new VarInsnNode(Opcode.IALOAD, varNumber);
            if (type == double[].class) return new VarInsnNode(Opcode.DALOAD, varNumber);
            if (type == float[].class) return new VarInsnNode(Opcode.FALOAD, varNumber);
            if (type == int[].class) return new VarInsnNode(Opcode.FALOAD, varNumber);
            if (type == long[].class) return new VarInsnNode(Opcode.LALOAD, varNumber);
            if (type == short[].class) return new VarInsnNode(Opcode.SALOAD, varNumber);
            if (type == boolean[].class) return new VarInsnNode(Opcode.IALOAD, varNumber);
            return new VarInsnNode(Opcode.AALOAD, varNumber);
        }
        if (type == byte.class) result = "I" + result;
        else if (type == char.class) result = "I" + result;
        else if (type == double.class) result = "D" + result;
        else if (type == float.class) result = "F" + result;
        else if (type == int.class) result = "I" + result;
        else if (type == long.class) result = "L" + result;
        else if (type == short.class) result = "I" + result;
        else if (type == boolean.class) result = "I" + result;
        else result = "A" + result;
        if (varNumber <= 3) {
            result = result + "_" + varNumber;
            return new InsnNode(((int) Opcode.class.getDeclaredField(result).get(null)));
        }
        return new VarInsnNode(((int) Opcode.class.getDeclaredField(result).get(null)), varNumber);
    }

    @SneakyThrows
    public static ClassNode toClassNode(CtClass ctClass) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(ctClass.toBytecode());
        reader.accept(node, 0);
        return node;
    }

    public static byte[] fromClassNode(ClassNode node) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

}
