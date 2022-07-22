package org.darklol9.bots.utils.asm.analysis.rewriter.value.values;

import org.darklol9.bots.utils.asm.analysis.rewriter.value.CodeReferenceValue;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NumberValue extends CodeReferenceValue {

    private final Object value;

    public NumberValue(BasicValue type, AbstractInsnNode node, Object value) {
        super(type, node);
        if (value instanceof Number || value instanceof Character || value instanceof Boolean) {
            this.value = Objects.requireNonNull(value);
        } else {
            throw new IllegalArgumentException("not a number");
        }
    }

    @Override
    public boolean isKnownValue() {
        return true;
    }

    @Override
    public boolean isRequiredInCode() {
        return false;
    }

    @Override
    public CodeReferenceValue combine() {
        return this;
    }

    @Override
    public boolean equalsWith(CodeReferenceValue obj) {
        if (this == obj)
            return true;
        if (obj instanceof NumberValue) {
            return ((NumberValue) obj).value.equals(value);
        }
        return false;
    }

    @Override
    public InsnList cloneInstructions() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(getStackValueOrNull()));
        return list;
    }

    @Override
    public List<AbstractInsnNode> getInstructions() {
        return Collections.singletonList(node);
    }

    @Override
    public Object getStackValueOrNull() {
        if (value instanceof Character) {
            return (int) (Character) value;
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        return value;
    }
}
