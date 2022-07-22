package org.darklol9.bots.utils.asm;

import org.objectweb.asm.ClassWriter;

public class ContextClassWriter extends ClassWriter {

    public ContextClassWriter(int flags) {
        super(null, flags);
    }

    public String getCommonSuperClass(String type1, String type2) {
//        return HierarchyUtils.getCommonSuperClass1(type1, type2);
        return super.getCommonSuperClass(type1, type2);
    }

}
