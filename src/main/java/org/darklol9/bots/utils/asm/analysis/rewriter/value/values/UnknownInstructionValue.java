package org.darklol9.bots.utils.asm.analysis.rewriter.value.values;

import org.darklol9.bots.utils.asm.analysis.rewriter.value.CodeReferenceValue;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.Collections;
import java.util.List;

public class UnknownInstructionValue extends CodeReferenceValue {

    public UnknownInstructionValue(BasicValue type, AbstractInsnNode node) {
        super(type, node);
        if (node.getType() == AbstractInsnNode.LABEL || node.getType() == AbstractInsnNode.JUMP_INSN)
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isKnownValue() {
        return false;
    }

    @Override
    public boolean isRequiredInCode() {
        return true;
    }

    @Override
    public CodeReferenceValue combine() {
        return this;
    }

    @Override
    public boolean equalsWith(CodeReferenceValue obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        UnknownInstructionValue other = (UnknownInstructionValue) obj;
        return node.equals(other.node);
    }

    @Override
    public InsnList cloneInstructions() {
        InsnList list = new InsnList();
        list.add(node.clone(null));
        return list;
    }

    @Override
    public List<AbstractInsnNode> getInstructions() {
        return Collections.singletonList(node);
    }

    @Override
    public Object getStackValueOrNull() {
        return null;
    }
}
