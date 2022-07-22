package org.darklol9.bots.utils.asm.analysis.rewriter;

import org.darklol9.bots.utils.asm.analysis.rewriter.value.CodeReferenceValue;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.List;

public interface ICRReferenceHandler {

    Object getFieldValueOrNull(BasicValue v, String owner, String name, String desc);

    Object getMethodReturnOrNull(BasicValue v, String owner, String name, String desc,
                                 List<? extends CodeReferenceValue> values);

}
