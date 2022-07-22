package org.darklol9.bots.utils.asm;

import org.darklol9.bots.utils.asm.analysis.stack.ConstantTracker;
import org.darklol9.bots.utils.asm.analysis.stack.ConstantValue;
import org.darklol9.bots.utils.asm.analysis.stack.IConstantReferenceHandler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.*;

public class AsmUtils implements Opcodes {

    public static final int MAX_INSTRUCTIONS = 0xFFFF;
    private static final Printer printer = new Textifier();
    private static final TraceMethodVisitor methodPrinter = new TraceMethodVisitor(printer);

    public static InsnList println(String message) {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        list.add(new LdcInsnNode(message));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        return list;
    }

    public static boolean isPushInt(AbstractInsnNode insn) {
        if (insn == null) return false;
        int op = insn.getOpcode();
        return (op >= ICONST_M1 && op <= ICONST_5)
                || op == BIPUSH
                || op == SIPUSH
                || (op == LDC && ((LdcInsnNode) insn).cst instanceof Integer);
    }

    public static int getPushedInt(AbstractInsnNode insn) {
        int op = insn.getOpcode();
        if (op >= ICONST_M1 && op <= ICONST_5) {
            return op - ICONST_0;
        }
        if (op == BIPUSH || op == SIPUSH) {
            return ((IntInsnNode) insn).operand;
        }
        if (op == LDC) {
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
                return (int) cst;
            }
        }
        throw new IllegalArgumentException("insn is not a push int instruction");
    }

    public static MethodNode getClinit(ClassWrapper classNode) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("<clinit>")) {
                return method;
            }
        }
        MethodNode clinit = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null);
        clinit.instructions = new InsnList();
        clinit.instructions.add(new InsnNode(RETURN));
        classNode.methods.add(clinit);
        return clinit;
    }

    public static AbstractInsnNode pushInt(int value) {
        if (value >= -1 && value <= 5) {
            return new InsnNode(ICONST_0 + value);
        }
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            return new IntInsnNode(BIPUSH, value);
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            return new IntInsnNode(SIPUSH, value);
        }
        return new LdcInsnNode(value);
    }

    public static boolean isPushLong(AbstractInsnNode insn) {
        if (insn == null) return false;
        int op = insn.getOpcode();
        return op == LCONST_0
                || op == LCONST_1
                || (op == LDC && ((LdcInsnNode) insn).cst instanceof Long);
    }

    public static long getPushedLong(AbstractInsnNode insn) {
        int op = insn.getOpcode();
        if (op == LCONST_0) {
            return 0;
        }
        if (op == LCONST_1) {
            return 1;
        }
        if (op == LDC) {
            Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Long) {
                return (long) cst;
            }
        }
        throw new IllegalArgumentException("insn is not a push long instruction");
    }

    public static AbstractInsnNode pushLong(long value) {
        if (value == 0) {
            return new InsnNode(LCONST_0);
        }
        if (value == 1) {
            return new InsnNode(LCONST_1);
        }
        return new LdcInsnNode(value);
    }

    public static int codeSize(MethodNode methodNode) {
        CodeSizeEvaluator evaluator = new CodeSizeEvaluator(null);
        methodNode.accept(evaluator);
        return evaluator.getMaxSize();
    }

    public static void unboxPrimitive(String desc, InsnList list) {
        switch (desc) {
            case "I":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
                break;
            case "Z":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
                break;
            case "B":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Byte"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false));
                break;
            case "C":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Character"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false));
                break;
            case "S":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Short"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false));
                break;
            case "J":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Long"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false));
                break;
            case "F":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Float"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false));
                break;
            case "D":
                list.add(new TypeInsnNode(CHECKCAST, "java/lang/Double"));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false));
                break;
            default:
                if (!desc.equals("Lnull;") && !desc.equals("Ljava/lang/Object;"))
                    list.add(new TypeInsnNode(CHECKCAST, desc.startsWith("L") && desc.endsWith(";") ?
                            desc.substring(1, desc.length() - 1) : desc));
        }
    }

    public static void boxPrimitive(String desc, InsnList list) {
        switch (desc) {
            case "I":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                break;
            case "Z":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
                break;
            case "B":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false));
                break;
            case "C":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false));
                break;
            case "S":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false));
                break;
            case "J":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false));
                break;
            case "F":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false));
                break;
            case "D":
                list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false));
                break;
            default:
                if (!desc.equals("Lnull;") && !desc.equals("Ljava/lang/Object;")) {
                    list.add(new TypeInsnNode(CHECKCAST, desc.startsWith("L") && desc.endsWith(";") ?
                            desc.substring(1, desc.length() - 1) : desc));
                }
                break;
        }
    }

    public static String print(AbstractInsnNode insnNode) {
        if (insnNode == null) return "null";
        insnNode.accept(methodPrinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString().trim();
    }

//    public static FieldNode findField(Obf obf, String owner, String name, String desc) {
//        ClassWrapper classNode = obf.assureLoaded(owner);
//        if (classNode == null) return null;
//        return findField(classNode, name, desc);
//    }

    public static FieldNode findField(ClassWrapper classNode, String name, String desc) {
        for (FieldNode field : classNode.fields) {
            if ((name == null || field.name.equals(name)) && (desc == null || field.desc.equals(desc))) {
                return field;
            }
        }
        return null;
    }

//    public static MethodNode findMethod(Obf obf, String owner, String name, String descriptor) {
//        ClassWrapper classNode = obf.assureLoaded(owner);
//        if (classNode == null) return null;
//        return findMethod(classNode, name, descriptor);
//    }

    public static MethodNode findMethod(ClassWrapper classNode, String name, String descriptor) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(name) && (descriptor == null || method.desc.equals(descriptor))) {
                return method;
            }
        }
        return null;
    }

    public static InsnList iterate(InsnList instructions, AbstractInsnNode start, AbstractInsnNode end) {
        InsnList list = new InsnList();
        boolean f = false;
        for (AbstractInsnNode instruction : instructions) {
            if (!f && instruction == start) {
                f = true;
            }
            if (f) {
                list.add(instruction);
            }
            if (instruction == end) {
                break;
            }
        }
        return list;
    }

    public static ClassWrapper clone(ClassWrapper classNode) {
        ClassWrapper c = new ClassWrapper(classNode.modify);
        classNode.accept(c);
        return c;
    }

    public static void boxClass(InsnList list, Type type) {
        switch (type.getDescriptor()) {
            case "I":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;"));
                break;
            case "Z":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;"));
                break;
            case "B":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;"));
                break;
            case "C":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;"));
                break;
            case "S":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;"));
                break;
            case "J":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;"));
                break;
            case "F":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"));
                break;
            case "D":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;"));
                break;
            case "V":
                list.add(new FieldInsnNode(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;"));
                break;
            default:
                list.add(new LdcInsnNode(type));
                break;
        }
    }

    public static MethodNode createMethod(int access, String name, String desc) {
        MethodNode m = new MethodNode(access, name, desc, null, null);
        m.instructions = new InsnList();
        return m;
    }

    public static void boxReturn(Type returnType, InsnList list) {
        Random r = new Random();
        switch (returnType.getOpcode(IRETURN)) {
            case IRETURN:
                list.add(pushInt(r.nextInt()));
                break;
            case LRETURN:
                list.add(pushLong(r.nextLong()));
                break;
            case FRETURN:
                list.add(new LdcInsnNode(r.nextFloat()));
                break;
            case DRETURN:
                list.add(new LdcInsnNode(r.nextDouble()));
                break;
            case ARETURN:
                list.add(new InsnNode(ACONST_NULL));
                break;
            case RETURN:
                break;
            default:
                throw new IllegalArgumentException("Unknown return type: " + returnType);
        }
        list.add(new InsnNode(returnType.getOpcode(IRETURN)));
    }

    public static String parentName(String name) {
        if (name.contains("/")) {
            return name.substring(0, name.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    public static void preverify(ClassWrapper classNode, MethodNode method) {
        Analyzer<SourceValue> analyzer = new Analyzer<>(new SourceInterpreter());
        try {
            analyzer.analyzeAndComputeMaxs(classNode.name, method);
        } catch (AnalyzerException e) {
            System.out.println("Failed to preverify method: " + classNode.name + "." + method.name + method.desc);
            e.printStackTrace();
        }
    }

    /**
     * Creates a string incrementing in numerical value.
     * Example: a, b, c, ... z, aa, ab ...
     *
     * @param index Name index.
     * @return Generated String
     */
    public static String generateName(int index) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase(Locale.ROOT);
        char[] charz = alphabet.toCharArray();
        int alphabetLength = charz.length;
        int m = 8;
        final char[] array = new char[m];
        int n = m - 1;
        while (index > charz.length - 1) {
            int k = Math.abs(-(index % alphabetLength));
            array[n--] = charz[k];
            index /= alphabetLength;
            index -= 1;
        }
        array[n] = charz[index];
        return new String(array, n, m - n);
    }

    public static InsnList grabArgs(boolean virt, Type[] _args) {
        InsnList list = new InsnList();
        int locals = 0;
        if (virt)
            locals++;
        for (Type arg : _args) {
            list.add(new VarInsnNode(arg.getOpcode(ILOAD), locals));
            locals += arg.getSize();
        }
        return list;
    }

    public static String getExceptionClass(List<ClassWrapper> libs) {
        while (true) {
            for (ClassWrapper lib : libs) {
                if (new Random().nextBoolean()) {
                    if (lib.name.endsWith("Exception") || lib.superName.endsWith("Exception")) {
                        return lib.name;
                    }
                }
            }
        }
    }

    public static InsnList storeStack(boolean virtual, Type[] types) {
        InsnList list = new InsnList();

        Type[] args = new Type[types.length + (virtual ? 1 : 0)];

        System.arraycopy(types, 0, args, virtual ? 1 : 0, types.length);

        if (virtual) {
            args[0] = Type.getType("Ljava/lang/Object;");
        }

        list.add(pushInt(args.length));
        list.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));

        for (int i = args.length - 1; i >= 0; i--) {
            Type arg = args[i];
            InsnList sub = new InsnList();
            if (arg.getSize() > 1) {
                sub.add(new InsnNode(DUP_X2));
                sub.add(new InsnNode(DUP_X2));
                sub.add(new InsnNode(POP));
                sub.add(pushInt(i));
                sub.add(new InsnNode(DUP_X2));
                sub.add(new InsnNode(POP));
            } else {
                sub.add(new InsnNode(DUP_X1));
                sub.add(new InsnNode(SWAP));
                sub.add(pushInt(i));
                sub.add(new InsnNode(SWAP));
            }
            boxPrimitive(arg.getDescriptor(), sub);
            sub.add(new InsnNode(AASTORE));
            list.add(sub);
        }

        return list;
    }

    public static List<AbstractInsnNode> grabLabel(MethodNode method, LabelNode start) {
        boolean started = false;
        List<AbstractInsnNode> list = new ArrayList<>();
        for (AbstractInsnNode instruction : method.instructions) {
            if (started && instruction instanceof LabelNode)
                return list;
            if (instruction instanceof LabelNode)
                started = instruction.equals(start);
            else if (started) {
                list.add(instruction);
            }
        }
        return list;
    }

    public static List<AbstractInsnNode> findInsn(MethodNode method, int... opcodes) {
        List<AbstractInsnNode> list = new ArrayList<>();
        for (AbstractInsnNode instruction : method.instructions) {
            for (int opcode : opcodes) {
                if (instruction.getOpcode() == opcode) {
                    list.add(instruction);
                    break;
                }
            }
        }
        return list;
    }

    public static Map<AbstractInsnNode, Frame<ConstantValue>> computeFrames(ClassWrapper classNode, MethodNode method, IConstantReferenceHandler handler) {
        Analyzer<ConstantValue> analyzer =
                new Analyzer<>(new ConstantTracker(handler, Modifier.isStatic(method.access), method.maxLocals, method.desc, new Object[0]));

        Map<AbstractInsnNode, Frame<ConstantValue>> frames = new HashMap<>();
        try {
            Frame<ConstantValue>[] _frames = analyzer.analyzeAndComputeMaxs(classNode.name, method);
            for (AbstractInsnNode instruction : method.instructions)
                frames.put(instruction, _frames[instruction.index]);
            return frames;
        } catch (AnalyzerException e) {
//            e.printStackTrace();
            throw new RuntimeException("Failed to compute frames");
        }
    }

}

