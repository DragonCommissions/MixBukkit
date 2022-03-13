package com.dragoncommissions.mixbukkit.utils;

import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TextifierSupport;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.util.*;

public class CustomTextifier extends MethodVisitor {
    public static final String[] OPCODES = new String[]{"NOP", "ACONST_NULL", "ICONST_M1", "ICONST_0", "ICONST_1", "ICONST_2", "ICONST_3", "ICONST_4", "ICONST_5", "LCONST_0", "LCONST_1", "FCONST_0", "FCONST_1", "FCONST_2", "DCONST_0", "DCONST_1", "BIPUSH", "SIPUSH", "LDC", "LDC_W", "LDC2_W", "ILOAD", "LLOAD", "FLOAD", "DLOAD", "ALOAD", "ILOAD_0", "ILOAD_1", "ILOAD_2", "ILOAD_3", "LLOAD_0", "LLOAD_1", "LLOAD_2", "LLOAD_3", "FLOAD_0", "FLOAD_1", "FLOAD_2", "FLOAD_3", "DLOAD_0", "DLOAD_1", "DLOAD_2", "DLOAD_3", "ALOAD_0", "ALOAD_1", "ALOAD_2", "ALOAD_3", "IALOAD", "LALOAD", "FALOAD", "DALOAD", "AALOAD", "BALOAD", "CALOAD", "SALOAD", "ISTORE", "LSTORE", "FSTORE", "DSTORE", "ASTORE", "ISTORE_0", "ISTORE_1", "ISTORE_2", "ISTORE_3", "LSTORE_0", "LSTORE_1", "LSTORE_2", "LSTORE_3", "FSTORE_0", "FSTORE_1", "FSTORE_2", "FSTORE_3", "DSTORE_0", "DSTORE_1", "DSTORE_2", "DSTORE_3", "ASTORE_0", "ASTORE_1", "ASTORE_2", "ASTORE_3", "IASTORE", "LASTORE", "FASTORE", "DASTORE", "AASTORE", "BASTORE", "CASTORE", "SASTORE", "POP", "POP2", "DUP", "DUP_X1", "DUP_X2", "DUP2", "DUP2_X1", "DUP2_X2", "SWAP", "IADD", "LADD", "FADD", "DADD", "ISUB", "LSUB", "FSUB", "DSUB", "IMUL", "LMUL", "FMUL", "DMUL", "IDIV", "LDIV", "FDIV", "DDIV", "IREM", "LREM", "FREM", "DREM", "INEG", "LNEG", "FNEG", "DNEG", "ISHL", "LSHL", "ISHR", "LSHR", "IUSHR", "LUSHR", "IAND", "LAND", "IOR", "LOR", "IXOR", "LXOR", "IINC", "I2L", "I2F", "I2D", "L2I", "L2F", "L2D", "F2I", "F2L", "F2D", "D2I", "D2L", "D2F", "I2B", "I2C", "I2S", "LCMP", "FCMPL", "FCMPG", "DCMPL", "DCMPG", "IFEQ", "IFNE", "IFLT", "IFGE", "IFGT", "IFLE", "IF_ICMPEQ", "IF_ICMPNE", "IF_ICMPLT", "IF_ICMPGE", "IF_ICMPGT", "IF_ICMPLE", "IF_ACMPEQ", "IF_ACMPNE", "GOTO", "JSR", "RET", "TABLESWITCH", "LOOKUPSWITCH", "IRETURN", "LRETURN", "FRETURN", "DRETURN", "ARETURN", "RETURN", "GETSTATIC", "PUTSTATIC", "GETFIELD", "PUTFIELD", "INVOKEVIRTUAL", "INVOKESPECIAL", "INVOKESTATIC", "INVOKEINTERFACE", "INVOKEDYNAMIC", "NEW", "NEWARRAY", "ANEWARRAY", "ARRAYLENGTH", "ATHROW", "CHECKCAST", "INSTANCEOF", "MONITORENTER", "MONITOREXIT", "WIDE", "MULTIANEWARRAY", "IFNULL", "IFNONNULL"};
    public static final String[] TYPES = new String[]{"", "", "", "", "T_BOOLEAN", "T_CHAR", "T_FLOAT", "T_DOUBLE", "T_BYTE", "T_SHORT", "T_INT", "T_LONG"};
    public static final String[] HANDLE_TAG = new String[]{"", "H_GETFIELD", "H_GETSTATIC", "H_PUTFIELD", "H_PUTSTATIC", "H_INVOKEVIRTUAL", "H_INVOKESTATIC", "H_INVOKESPECIAL", "H_NEWINVOKESPECIAL", "H_INVOKEINTERFACE"};
    private static final String UNSUPPORTED_OPERATION = "Must be overridden";
    private static final String USAGE = "Prints a disassembled view of the given class.\nUsage: Textifier [-nodebug] <fully qualified class name or class file name>";
    public static final int INTERNAL_NAME = 0;
    public static final int FIELD_DESCRIPTOR = 1;
    public static final int FIELD_SIGNATURE = 2;
    public static final int METHOD_DESCRIPTOR = 3;
    public static final int METHOD_SIGNATURE = 4;
    public static final int CLASS_SIGNATURE = 5;
    public static final int HANDLE_DESCRIPTOR = 9;
    private static final String CLASS_SUFFIX = ".class";
    private static final String DEPRECATED = "// DEPRECATED\n";
    private static final String RECORD = "// RECORD\n";
    private static final String INVISIBLE = " // invisible\n";
    private static final List<String> FRAME_TYPES = Collections.unmodifiableList(Arrays.asList("T", "I", "F", "D", "J", "N", "U"));
    protected String tab;
    protected String tab2;
    protected String tab3;
    protected String ltab;
    protected Map<Label, String> labelNames;
    private int access;
    private int numAnnotationValues;
    public final List<Object> text = new ArrayList<>();
    protected final StringBuilder stringBuilder = new StringBuilder();

    public CustomTextifier() {
        this(589824);
        if (this.getClass() != CustomTextifier.class) {
            throw new IllegalStateException();
        }
    }

    protected CustomTextifier(int api) {
        super(api);
        this.tab = "\t ";
        this.tab2 = "\t \t ";
        this.tab3 = "\t \t \t ";
        this.ltab = "\t\t   ";
    }


    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((access & '耀') == 0) {
            this.access = access;
            int majorVersion = version & '\uffff';
            int minorVersion = version >>> 16;
            this.stringBuilder.setLength(0);
            this.stringBuilder.append("// class version ").append(majorVersion).append('.').append(minorVersion).append(" (").append(version).append(")\n");
            if ((access & 131072) != 0) {
                this.stringBuilder.append("// DEPRECATED\n");
            }

            if ((access & 65536) != 0) {
                this.stringBuilder.append("// RECORD\n");
            }

            this.appendRawAccess(access);
            this.appendDescriptor(5, signature);
            if (signature != null) {
                this.appendJavaDeclaration(name, signature);
            }

            this.appendAccess(access & -32801);
            if ((access & 8192) != 0) {
                this.stringBuilder.append("@interface ");
            } else if ((access & 512) != 0) {
                this.stringBuilder.append("interface ");
            } else if ((access & 16384) == 0) {
                this.stringBuilder.append("class ");
            }

            this.appendDescriptor(0, name);
            if (superName != null && !"java/lang/Object".equals(superName)) {
                this.stringBuilder.append(" extends ");
                this.appendDescriptor(0, superName);
            }

            if (interfaces != null && interfaces.length > 0) {
                this.stringBuilder.append(" implements ");

                for(int i = 0; i < interfaces.length; ++i) {
                    this.appendDescriptor(0, interfaces[i]);
                    if (i != interfaces.length - 1) {
                        this.stringBuilder.append(' ');
                    }
                }
            }

            this.stringBuilder.append(" {\n\n");
            this.text.add(this.stringBuilder.toString());
        }
    }

    public void visitSource(String file, String debug) {
        this.stringBuilder.setLength(0);
        if (file != null) {
            this.stringBuilder.append(this.tab).append("// compiled from: ").append(file);
        }

        if (debug != null) {
            this.stringBuilder.append(this.tab).append("// debug info: ").append(debug);
        }

        if (this.stringBuilder.length() > 0) {
            this.text.add(this.stringBuilder.toString());
        }

    }

    public CustomTextifier visitModule(String name, int access, String version) {
        this.stringBuilder.setLength(0);
        if ((access & 32) != 0) {
            this.stringBuilder.append("open ");
        }

        this.stringBuilder.append("module ").append(name).append(" { ").append(version == null ? "" : "// " + version).append("\n\n");
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier((String)null);
    }

    public void visitNestHost(String nestHost) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("NESTHOST ");
        this.appendDescriptor(0, nestHost);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("OUTERCLASS ");
        this.appendDescriptor(0, owner);
        this.stringBuilder.append(' ');
        if (name != null) {
            this.stringBuilder.append(name).append(' ');
        }

        this.appendDescriptor(3, descriptor);

        this.text.add(this.stringBuilder.toString());
    }


    public void visitClassAttribute(Attribute attribute) {
        this.text.add("\n");
        this.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("NESTMEMBER ");
        this.appendDescriptor(0, nestMember);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("PERMITTEDSUBCLASS ");
        this.appendDescriptor(0, permittedSubclass);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access & -33);
        this.stringBuilder.append(this.tab);
        this.appendAccess(access);
        this.stringBuilder.append("INNERCLASS ");
        this.appendDescriptor(0, name);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, outerName);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, innerName);

        this.text.add(this.stringBuilder.toString());
    }

    public CustomTextifier visitRecordComponent(String name, String descriptor, String signature) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("RECORDCOMPONENT ");
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }

        this.stringBuilder.append(this.tab);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(name);

        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier((String)null);
    }

    public CustomTextifier visitField(int access, String name, String descriptor, String signature, Object value) {
        this.stringBuilder.setLength(0);

        if ((access & 131072) != 0) {
            this.stringBuilder.append(this.tab).append("// DEPRECATED\n");
        }

        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access);
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }

        this.stringBuilder.append(this.tab);
        this.appendAccess(access);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(name);
        if (value != null) {
            this.stringBuilder.append(" = ");
            if (value instanceof String) {
                this.stringBuilder.append('"').append(value).append('"');
            } else {
                this.stringBuilder.append(value);
            }
        }


        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier((String)null);
    }

    public CustomTextifier visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.stringBuilder.setLength(0);

        if ((access & 131072) != 0) {
            this.stringBuilder.append(this.tab).append("// DEPRECATED\n");
        }

        this.stringBuilder.append(this.tab);
        this.appendRawAccess(access);
        if (signature != null) {
            this.stringBuilder.append(this.tab);
            this.appendDescriptor(4, signature);
            this.stringBuilder.append(this.tab);
            this.appendJavaDeclaration(name, signature);
        }

        this.stringBuilder.append(this.tab);
        this.appendAccess(access & -193);
        if ((access & 256) != 0) {
            this.stringBuilder.append("native ");
        }

        if ((access & 128) != 0) {
            this.stringBuilder.append("varargs ");
        }

        if ((access & 64) != 0) {
            this.stringBuilder.append("bridge ");
        }

        if ((this.access & 512) != 0 && (access & 1032) == 0) {
            this.stringBuilder.append("default ");
        }

        this.stringBuilder.append(name);
        this.appendDescriptor(3, descriptor);
        if (exceptions != null && exceptions.length > 0) {
            this.stringBuilder.append(" throws ");
            String[] var6 = exceptions;
            int var7 = exceptions.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String exception = var6[var8];
                this.appendDescriptor(0, exception);
                this.stringBuilder.append(' ');
            }
        }


        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier((String)null);
    }

    public void visitClassEnd() {
        this.text.add("}\n");
    }

    public void visitMainClass(String mainClass) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("  // main class ").append(mainClass);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitPackage(String packaze) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append("  // package ").append(packaze);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitRequire(String require, int access, String version) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("requires ");
        if ((access & 32) != 0) {
            this.stringBuilder.append("transitive ");
        }

        if ((access & 64) != 0) {
            this.stringBuilder.append("static ");
        }

        this.stringBuilder.append(require).append(';');
        this.appendRawAccess(access);
        if (version != null) {
            this.stringBuilder.append("  // version ").append(version);
        }

        this.text.add(this.stringBuilder.toString());
    }

    public void visitExport(String packaze, int access, String... modules) {
        this.visitExportOrOpen("exports ", packaze, access, modules);
    }

    public void visitOpen(String packaze, int access, String... modules) {
        this.visitExportOrOpen("opens ", packaze, access, modules);
    }

    private void visitExportOrOpen(String method, String packaze, int access, String... modules) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(method);
        this.stringBuilder.append(packaze);
        if (modules != null && modules.length > 0) {
            this.stringBuilder.append(" to");
        } else {
            this.stringBuilder.append(';');
        }

        this.appendRawAccess(access);
        if (modules != null && modules.length > 0) {
            for(int i = 0; i < modules.length; ++i) {
                this.stringBuilder.append(this.tab2).append(modules[i]);
                this.stringBuilder.append(i != modules.length - 1 ? ",\n" : ";\n");
            }
        }

        this.text.add(this.stringBuilder.toString());
    }

    public void visitUse(String use) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("uses ");
        this.appendDescriptor(0, use);
        this.stringBuilder.append(";\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitProvide(String provide, String... providers) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("provides ");
        this.appendDescriptor(0, provide);
        this.stringBuilder.append(" with\n");

        for(int i = 0; i < providers.length; ++i) {
            this.stringBuilder.append(this.tab2);
            this.appendDescriptor(0, providers[i]);
            this.stringBuilder.append(i != providers.length - 1 ? ",\n" : ";\n");
        }

        this.text.add(this.stringBuilder.toString());
    }

    public void visitModuleEnd() {
    }

    public void visit(String name, Object value) {
        this.visitAnnotationValue(name);
        if (value instanceof String) {
            this.visitString((String)value);
        } else if (value instanceof Type) {
            this.visitType((Type)value);
        } else if (value instanceof Byte) {
            this.visitByte((Byte)value);
        } else if (value instanceof Boolean) {
            this.visitBoolean((Boolean)value);
        } else if (value instanceof Short) {
            this.visitShort((Short)value);
        } else if (value instanceof Character) {
            this.visitChar((Character)value);
        } else if (value instanceof Integer) {
            this.visitInt((Integer)value);
        } else if (value instanceof Float) {
            this.visitFloat((Float)value);
        } else if (value instanceof Long) {
            this.visitLong((Long)value);
        } else if (value instanceof Double) {
            this.visitDouble((Double)value);
        } else if (value.getClass().isArray()) {
            this.stringBuilder.append('{');
            int i;
            if (value instanceof byte[]) {
                byte[] byteArray = (byte[])value;

                for(i = 0; i < byteArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitByte(byteArray[i]);
                }
            } else if (value instanceof boolean[]) {
                boolean[] booleanArray = (boolean[])value;

                for(i = 0; i < booleanArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitBoolean(booleanArray[i]);
                }
            } else if (value instanceof short[]) {
                short[] shortArray = (short[])value;

                for(i = 0; i < shortArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitShort(shortArray[i]);
                }
            } else if (value instanceof char[]) {
                char[] charArray = (char[])value;

                for(i = 0; i < charArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitChar(charArray[i]);
                }
            } else if (value instanceof int[]) {
                int[] intArray = (int[])value;

                for(i = 0; i < intArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitInt(intArray[i]);
                }
            } else if (value instanceof long[]) {
                long[] longArray = (long[])value;

                for(i = 0; i < longArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitLong(longArray[i]);
                }
            } else if (value instanceof float[]) {
                float[] floatArray = (float[])value;

                for(i = 0; i < floatArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitFloat(floatArray[i]);
                }
            } else if (value instanceof double[]) {
                double[] doubleArray = (double[])value;

                for(i = 0; i < doubleArray.length; ++i) {
                    this.maybeAppendComma(i);
                    this.visitDouble(doubleArray[i]);
                }
            }

            this.stringBuilder.append('}');
        }

        this.text.add(this.stringBuilder.toString());
    }

    private void visitInt(int value) {
        this.stringBuilder.append(value);
    }

    private void visitLong(long value) {
        this.stringBuilder.append(value).append('L');
    }

    private void visitFloat(float value) {
        this.stringBuilder.append(value).append('F');
    }

    private void visitDouble(double value) {
        this.stringBuilder.append(value).append('D');
    }

    private void visitChar(char value) {
        this.stringBuilder.append("(char)").append(value);
    }

    private void visitShort(short value) {
        this.stringBuilder.append("(short)").append(value);
    }

    private void visitByte(byte value) {
        this.stringBuilder.append("(byte)").append(value);
    }

    private void visitBoolean(boolean value) {
        this.stringBuilder.append(value);
    }

    private void visitString(String value) {
        appendString(this.stringBuilder, value);
    }

    public static void appendString(StringBuilder stringBuilder, String string) {
        stringBuilder.append('"');

        for(int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '\n') {
                stringBuilder.append("\\n");
            } else if (c == '\r') {
                stringBuilder.append("\\r");
            } else if (c == '\\') {
                stringBuilder.append("\\\\");
            } else if (c == '"') {
                stringBuilder.append("\\\"");
            } else if (c >= ' ' && c <= 127) {
                stringBuilder.append(c);
            } else {
                stringBuilder.append("\\u");
                if (c < 16) {
                    stringBuilder.append("000");
                } else if (c < 256) {
                    stringBuilder.append("00");
                } else if (c < 4096) {
                    stringBuilder.append('0');
                }

                stringBuilder.append(Integer.toString(c, 16));
            }
        }

        stringBuilder.append('"');
    }

    private void visitType(Type value) {
        this.stringBuilder.append(value.getClassName()).append(".class");
    }

    public void visitEnum(String name, String descriptor, String value) {
        this.visitAnnotationValue(name);
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('.').append(value);
        this.text.add(this.stringBuilder.toString());
    }

    public CustomTextifier visitAnnotation(String name, String descriptor) {
        this.visitAnnotationValue(name);
        this.stringBuilder.append('@');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append('(');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier(")");
    }

    public CustomTextifier visitArray(String name) {
        this.visitAnnotationValue(name);
        this.stringBuilder.append('{');
        this.text.add(this.stringBuilder.toString());
        return this.addNewTextifier("}");
    }

    public void visitAnnotationEnd() {
    }

    private void visitAnnotationValue(String name) {
        this.stringBuilder.setLength(0);
        this.maybeAppendComma(this.numAnnotationValues++);
        if (name != null) {
            this.stringBuilder.append(name).append('=');
        }

    }


    public void visitRecordComponentAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitRecordComponentEnd() {
    }


    public void visitFieldAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitFieldEnd() {
    }

    public void visitParameter(String name, int access) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("// parameter ");
        this.appendAccess(access);
        this.stringBuilder.append(' ').append(name == null ? "<no name>" : name);
        this.text.add(this.stringBuilder.toString());
    }


    public void visitMethodAttribute(Attribute attribute) {
        this.visitAttribute(attribute);
    }

    public void visitCode() {
    }

    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        rip++;
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.ltab);
        this.stringBuilder.append("FRAME ");
        switch(type) {
            case -1:
            case 0:
                this.stringBuilder.append("FULL [");
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append("] [");
                this.appendFrameTypes(numStack, stack);
                this.stringBuilder.append(']');
                break;
            case 1:
                this.stringBuilder.append("APPEND [");
                this.appendFrameTypes(numLocal, local);
                this.stringBuilder.append(']');
                break;
            case 2:
                this.stringBuilder.append("CHOP ").append(numLocal);
                break;
            case 3:
                this.stringBuilder.append("SAME");
                break;
            case 4:
                this.stringBuilder.append("SAME1 ");
                this.appendFrameTypes(1, stack);
                break;
            default:
                throw new IllegalArgumentException();
        }


        this.text.add(this.stringBuilder.toString());
    }

    private int rip = 1;

    public void visitInsn(int opcode) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitIntInsn(int opcode, int operand) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ').append(opcode == 188 ? TYPES[operand] : Integer.toString(operand));
        this.text.add(this.stringBuilder.toString());
    }

    public void visitVarInsn(int opcode, int var) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ').append(var);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTypeInsn(int opcode, String type) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, type);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, owner);
        this.stringBuilder.append('.').append(name).append(" : ");
        this.appendDescriptor(1, descriptor);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ');
        this.appendDescriptor(0, owner);
        this.stringBuilder.append('.').append(name).append(' ');
        this.appendDescriptor(3, descriptor);
        if (isInterface) {
            this.stringBuilder.append(" (itf)");
        }


        this.text.add(this.stringBuilder.toString());
    }

    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("INVOKEDYNAMIC").append(' ');
        this.stringBuilder.append(name);
        this.appendDescriptor(3, descriptor);
        this.stringBuilder.append(" [");

        this.stringBuilder.append(this.tab3);
        this.appendHandle(bootstrapMethodHandle);

        this.stringBuilder.append(this.tab3).append("// arguments:");
        if (bootstrapMethodArguments.length == 0) {
            this.stringBuilder.append(" none");
        } else {

            Object[] var5 = bootstrapMethodArguments;
            int var6 = bootstrapMethodArguments.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Object value = var5[var7];
                this.stringBuilder.append(this.tab3);
                if (value instanceof String) {
                    CustomPrinter.appendString(this.stringBuilder, (String)value);
                } else if (value instanceof Type) {
                    Type type = (Type)value;
                    if (type.getSort() == 11) {
                        this.appendDescriptor(3, type.getDescriptor());
                    } else {
                        this.visitType(type);
                    }
                } else if (value instanceof Handle) {
                    this.appendHandle((Handle)value);
                } else {
                    this.stringBuilder.append(value);
                }

                this.stringBuilder.append(", \n");
            }

            this.stringBuilder.setLength(this.stringBuilder.length() - 3);
        }


        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("]\n");
        this.text.add(this.stringBuilder.toString());
    }

    public void visitJumpInsn(int opcode, Label label) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append(OPCODES[opcode]).append(' ');
        this.appendLabel(label);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitLabel(Label label) {
        rip++;
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.ltab);
        this.appendLabel(label);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitLdcInsn(Object value) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("LDC ");
        if (value instanceof String) {
            CustomPrinter.appendString(this.stringBuilder, (String)value);
        } else if (value instanceof Type) {
            this.stringBuilder.append(((Type)value).getDescriptor()).append(".class");
        } else {
            this.stringBuilder.append(value);
        }


        this.text.add(this.stringBuilder.toString());
    }

    public void visitIincInsn(int var, int increment) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("IINC ").append(var).append(' ').append(increment);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("TABLESWITCH\n");

        for(int i = 0; i < labels.length; ++i) {
            this.stringBuilder.append(this.tab3).append(min + i).append(": ");
            this.appendLabel(labels[i]);

        }

        this.stringBuilder.append(this.tab3).append("default: ");
        this.appendLabel(dflt);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("LOOKUPSWITCH\n");

        for(int i = 0; i < labels.length; ++i) {
            this.stringBuilder.append(this.tab3).append(keys[i]).append(": ");
            this.appendLabel(labels[i]);

        }

        this.stringBuilder.append(this.tab3).append("default: ");
        this.appendLabel(dflt);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append(rip++).append(this.tab).append("MULTIANEWARRAY ");
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ').append(numDimensions);
        this.text.add(this.stringBuilder.toString());
    }


    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("TRYCATCHBLOCK ");
        this.appendLabel(start);
        this.stringBuilder.append(' ');
        this.appendLabel(end);
        this.stringBuilder.append(' ');
        this.appendLabel(handler);
        this.stringBuilder.append(' ');
        this.appendDescriptor(0, type);

        this.text.add(this.stringBuilder.toString());
    }


    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("LOCALVARIABLE ").append(name).append(' ');
        this.appendDescriptor(1, descriptor);
        this.stringBuilder.append(' ');
        this.appendLabel(start);
        this.stringBuilder.append(' ');
        this.appendLabel(end);
        this.stringBuilder.append(' ').append(index);
        if (signature != null) {
            this.stringBuilder.append(this.tab2);
            this.appendDescriptor(2, signature);
            this.stringBuilder.append(this.tab2);
            this.appendJavaDeclaration(name, signature);
        }

        this.text.add(this.stringBuilder.toString());
    }


    public void visitLineNumber(int line, Label start) {
        this.stringBuilder.setLength(0);
        rip++;
        this.stringBuilder.append(this.tab2).append("LINENUMBER ").append(line).append(' ');
        this.appendLabel(start);

        this.text.add(this.stringBuilder.toString());
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("MAXSTACK = ").append(maxStack);
        this.text.add(this.stringBuilder.toString());
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab2).append("MAXLOCALS = ").append(maxLocals);
        this.text.add(this.stringBuilder.toString());
    }

    public void visitMethodEnd() {
    }



    public void visitAttribute(Attribute attribute) {
        this.stringBuilder.setLength(0);
        this.stringBuilder.append(this.tab).append("ATTRIBUTE ");
        this.appendDescriptor(-1, attribute.type);
        if (attribute instanceof TextifierSupport) {
            if (this.labelNames == null) {
                this.labelNames = new HashMap();
            }

            ((TextifierSupport)attribute).textify(this.stringBuilder, this.labelNames);
        } else {
            this.stringBuilder.append(" : unknown\n");
        }

        this.text.add(this.stringBuilder.toString());
    }

    private void appendAccess(int accessFlags) {
        if ((accessFlags & 1) != 0) {
            this.stringBuilder.append("public ");
        }

        if ((accessFlags & 2) != 0) {
            this.stringBuilder.append("private ");
        }

        if ((accessFlags & 4) != 0) {
            this.stringBuilder.append("protected ");
        }

        if ((accessFlags & 16) != 0) {
            this.stringBuilder.append("final ");
        }

        if ((accessFlags & 8) != 0) {
            this.stringBuilder.append("static ");
        }

        if ((accessFlags & 32) != 0) {
            this.stringBuilder.append("synchronized ");
        }

        if ((accessFlags & 64) != 0) {
            this.stringBuilder.append("volatile ");
        }

        if ((accessFlags & 128) != 0) {
            this.stringBuilder.append("transient ");
        }

        if ((accessFlags & 1024) != 0) {
            this.stringBuilder.append("abstract ");
        }

        if ((accessFlags & 2048) != 0) {
            this.stringBuilder.append("strictfp ");
        }

        if ((accessFlags & 4096) != 0) {
            this.stringBuilder.append("synthetic ");
        }

        if ((accessFlags & '耀') != 0) {
            this.stringBuilder.append("mandated ");
        }

        if ((accessFlags & 16384) != 0) {
            this.stringBuilder.append("enum ");
        }

    }

    private void appendRawAccess(int accessFlags) {
        this.stringBuilder.append("// access flags 0x").append(Integer.toHexString(accessFlags).toUpperCase());
    }

    protected void appendDescriptor(int type, String value) {
        if (type != 5 && type != 2 && type != 4) {
            this.stringBuilder.append(value);
        } else if (value != null) {
            this.stringBuilder.append("// signature ").append(value);
        }

    }

    private void appendJavaDeclaration(String name, String signature) {
        TraceSignatureVisitor traceSignatureVisitor = new TraceSignatureVisitor(this.access);
        (new SignatureReader(signature)).accept(traceSignatureVisitor);
        this.stringBuilder.append("// declaration: ");
        if (traceSignatureVisitor.getReturnType() != null) {
            this.stringBuilder.append(traceSignatureVisitor.getReturnType());
            this.stringBuilder.append(' ');
        }

        this.stringBuilder.append(name);
        this.stringBuilder.append(traceSignatureVisitor.getDeclaration());
        if (traceSignatureVisitor.getExceptions() != null) {
            this.stringBuilder.append(" throws ").append(traceSignatureVisitor.getExceptions());
        }


    }

    protected void appendLabel(Label label) {
        if (this.labelNames == null) {
            this.labelNames = new HashMap();
        }

        String name = (String)this.labelNames.get(label);
        if (name == null) {
            name = "L" + this.labelNames.size();
            this.labelNames.put(label, name);
        }

        this.stringBuilder.append(name);
    }

    protected void appendHandle(Handle handle) {
        int tag = handle.getTag();
        this.stringBuilder.append("// handle kind 0x").append(Integer.toHexString(tag)).append(" : ");
        boolean isMethodHandle = false;
        switch(tag) {
            case 1:
                this.stringBuilder.append("GETFIELD");
                break;
            case 2:
                this.stringBuilder.append("GETSTATIC");
                break;
            case 3:
                this.stringBuilder.append("PUTFIELD");
                break;
            case 4:
                this.stringBuilder.append("PUTSTATIC");
                break;
            case 5:
                this.stringBuilder.append("INVOKEVIRTUAL");
                isMethodHandle = true;
                break;
            case 6:
                this.stringBuilder.append("INVOKESTATIC");
                isMethodHandle = true;
                break;
            case 7:
                this.stringBuilder.append("INVOKESPECIAL");
                isMethodHandle = true;
                break;
            case 8:
                this.stringBuilder.append("NEWINVOKESPECIAL");
                isMethodHandle = true;
                break;
            case 9:
                this.stringBuilder.append("INVOKEINTERFACE");
                isMethodHandle = true;
                break;
            default:
                throw new IllegalArgumentException();
        }


        this.stringBuilder.append(this.tab3);
        this.appendDescriptor(0, handle.getOwner());
        this.stringBuilder.append('.');
        this.stringBuilder.append(handle.getName());
        if (!isMethodHandle) {
            this.stringBuilder.append('(');
        }

        this.appendDescriptor(9, handle.getDesc());
        if (!isMethodHandle) {
            this.stringBuilder.append(')');
        }

        if (handle.isInterface()) {
            this.stringBuilder.append(" itf");
        }

    }

    private void maybeAppendComma(int numValues) {
        if (numValues > 0) {
            this.stringBuilder.append(", ");
        }

    }

    private void appendTypeReference(int typeRef) {
        TypeReference typeReference = new TypeReference(typeRef);
        switch(typeReference.getSort()) {
            case 0:
                this.stringBuilder.append("CLASS_TYPE_PARAMETER ").append(typeReference.getTypeParameterIndex());
                break;
            case 1:
                this.stringBuilder.append("METHOD_TYPE_PARAMETER ").append(typeReference.getTypeParameterIndex());
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            default:
                throw new IllegalArgumentException();
            case 16:
                this.stringBuilder.append("CLASS_EXTENDS ").append(typeReference.getSuperTypeIndex());
                break;
            case 17:
                this.stringBuilder.append("CLASS_TYPE_PARAMETER_BOUND ").append(typeReference.getTypeParameterIndex()).append(", ").append(typeReference.getTypeParameterBoundIndex());
                break;
            case 18:
                this.stringBuilder.append("METHOD_TYPE_PARAMETER_BOUND ").append(typeReference.getTypeParameterIndex()).append(", ").append(typeReference.getTypeParameterBoundIndex());
                break;
            case 19:
                this.stringBuilder.append("FIELD");
                break;
            case 20:
                this.stringBuilder.append("METHOD_RETURN");
                break;
            case 21:
                this.stringBuilder.append("METHOD_RECEIVER");
                break;
            case 22:
                this.stringBuilder.append("METHOD_FORMAL_PARAMETER ").append(typeReference.getFormalParameterIndex());
                break;
            case 23:
                this.stringBuilder.append("THROWS ").append(typeReference.getExceptionIndex());
                break;
            case 64:
                this.stringBuilder.append("LOCAL_VARIABLE");
                break;
            case 65:
                this.stringBuilder.append("RESOURCE_VARIABLE");
                break;
            case 66:
                this.stringBuilder.append("EXCEPTION_PARAMETER ").append(typeReference.getTryCatchBlockIndex());
                break;
            case 67:
                this.stringBuilder.append("INSTANCEOF");
                break;
            case 68:
                this.stringBuilder.append("NEW");
                break;
            case 69:
                this.stringBuilder.append("CONSTRUCTOR_REFERENCE");
                break;
            case 70:
                this.stringBuilder.append("METHOD_REFERENCE");
                break;
            case 71:
                this.stringBuilder.append("CAST ").append(typeReference.getTypeArgumentIndex());
                break;
            case 72:
                this.stringBuilder.append("CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            case 73:
                this.stringBuilder.append("METHOD_INVOCATION_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            case 74:
                this.stringBuilder.append("CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
                break;
            case 75:
                this.stringBuilder.append("METHOD_REFERENCE_TYPE_ARGUMENT ").append(typeReference.getTypeArgumentIndex());
        }

    }

    private void appendFrameTypes(int numTypes, Object[] frameTypes) {
        for(int i = 0; i < numTypes; ++i) {
            if (i > 0) {
                this.stringBuilder.append(' ');
            }

            if (frameTypes[i] instanceof String) {
                String descriptor = (String)frameTypes[i];
                if (descriptor.charAt(0) == '[') {
                    this.appendDescriptor(1, descriptor);
                } else {
                    this.appendDescriptor(0, descriptor);
                }
            } else if (frameTypes[i] instanceof Integer) {
                this.stringBuilder.append((String)FRAME_TYPES.get((Integer)frameTypes[i]));
            } else {
                this.appendLabel((Label)frameTypes[i]);
            }
        }

    }

    private CustomTextifier addNewTextifier(String endText) {
        CustomTextifier textifier = this.createTextifier();
        this.text.add(textifier.text);
        if (endText != null) {
            this.text.add(endText);
        }

        return textifier;
    }

    protected CustomTextifier createTextifier() {
        return new CustomTextifier(this.api);
    }
}
