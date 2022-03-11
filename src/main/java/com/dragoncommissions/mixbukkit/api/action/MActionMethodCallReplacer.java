package com.dragoncommissions.mixbukkit.api.action;

import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;

@AllArgsConstructor
public class MActionMethodCallReplacer implements MixinAction {

    private Method handler;
    private boolean isTargetStatic;

    @Override
    public void action(Class<?> owner, MethodNode method) {
        method.localVariables.clear();
        InsnList out = new InsnList();
        int latestVarCount = -1;
        latestVarCount++;
        int varNumOf11 = latestVarCount++;
        int varNumOf12 = latestVarCount++;
        int varNumOf13 = latestVarCount++;
        int varNumOf14 = latestVarCount++;
        int varNumOf15 = latestVarCount++;
        int varNumOf16 = latestVarCount++;
        int varNumOf17 = latestVarCount++;
        int varNumOf18 = latestVarCount++;

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
        LabelNode label10 = new LabelNode();

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
        out.add(new VarInsnNode(Opcode.ASTORE, varNumOf18));
//        out.add(new InsnNode(87)); // This pops the return value of it
        out.add(new JumpInsnNode(167, label10));
        out.add(label6);
        out.add(new JumpInsnNode(167, label7));
        out.add(label7);
        out.add(new JumpInsnNode(167, label9));
        out.add(label8);
        out.add(new VarInsnNode(58, varNumOf11));
        out.add(label9);
        out.add(new InsnNode(Opcode.ACONST_NULL));
        out.add(new VarInsnNode(Opcode.ASTORE, varNumOf18));
        out.add(label10);
        out.add(new VarInsnNode(Opcode.ALOAD, varNumOf18));
        out.add(new InsnNode(Opcode.ARETURN));
        method.tryCatchBlocks.add(new TryCatchBlockNode(label0, label5, label8, "java/lang/Exception"));
        method.localVariables.add(new LocalVariableNode("classLoader", "Ljava/lang/ClassLoader;", null, label1, label2, varNumOf15));
        method.localVariables.add(new LocalVariableNode("loadedClass", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", label3, label2, varNumOf16));
        method.localVariables.add(new LocalVariableNode("methodNameHere", "Ljava/lang/reflect/Method;", null, label4, label2, varNumOf17));
        method.localVariables.add(new LocalVariableNode("plugin", "Lorg/bukkit/plugin/Plugin;", null, label5, label2, varNumOf14));
        method.instructions = out;
    }
}
