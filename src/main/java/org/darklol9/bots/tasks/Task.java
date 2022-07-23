package org.darklol9.bots.tasks;

import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.utils.asm.ASMContext;
import org.objectweb.asm.Opcodes;

import java.io.File;

public interface Task extends Opcodes {

    File process(TaskContext ctx, ASMContext asmCtx, File input);

}
