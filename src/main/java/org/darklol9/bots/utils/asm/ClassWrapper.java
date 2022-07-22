package org.darklol9.bots.utils.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.TrashClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassWrapper extends ClassNode {

    public boolean modify;

    public ClassWrapper(boolean modify) {
        super(589824);
        this.modify = modify;
    }

    public ContextClassWriter createWriter() {
        return this.createWriter(ClassWriter.COMPUTE_FRAMES);
    }

    public ContextClassWriter createWriter(int flags) {
        return new ContextClassWriter(flags);
    }

    public byte[] compute() {
        return compute(false);
    }

    public byte[] compute(boolean suppress) {
        try {
            ClassWriter cw = createWriter();
            this.accept(cw);
            return cw.toByteArray();
        } catch (Exception ex) {
            if (!suppress) System.out.println("Failed to compute frames for " + this.name + ", " + ex.getMessage());
            ClassWriter cw = createWriter(ClassWriter.COMPUTE_MAXS);
            this.accept(cw);
            return cw.toByteArray();
        }
    }

    public byte[] trashCompute() {
        ClassWriter cw = new TrashClassWriter(ClassWriter.COMPUTE_FRAMES);
        this.accept(cw);
        return cw.toByteArray();
    }
}
