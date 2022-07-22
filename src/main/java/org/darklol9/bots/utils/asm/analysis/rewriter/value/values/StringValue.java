package org.darklol9.bots.utils.asm.analysis.rewriter.value.values;

import org.darklol9.bots.utils.asm.analysis.rewriter.value.CodeReferenceValue;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StringValue extends CodeReferenceValue {

    private final String value;

    public StringValue(BasicValue type, AbstractInsnNode node, String value) {
        super(type, node);
        this.value = Objects.requireNonNull(value);
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
        if (obj instanceof StringValue) {
            return ((StringValue) obj).value.equals(value);
        }
        return false;
    }

    @Override
    public InsnList cloneInstructions() {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(value));
        return list;
    }

    @Override
    public List<AbstractInsnNode> getInstructions() {
        return Collections.singletonList(node);
    }

    @Override
    public Object getStackValueOrNull() {
        return value;
    }
}
