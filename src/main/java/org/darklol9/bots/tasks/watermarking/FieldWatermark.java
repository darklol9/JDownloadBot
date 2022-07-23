package org.darklol9.bots.tasks.watermarking;

import net.dv8tion.jda.api.entities.User;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.tasks.Task;
import org.darklol9.bots.utils.asm.ASMContext;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.File;
import java.util.HashSet;
import java.util.Random;

public class FieldWatermark implements Task {

    @Override
    public File process(TaskContext ctx, ASMContext asmCtx, File input) {
        Random random = new Random();
        User user = ctx.getUser();
        File output = new File(input.getParentFile(), "f_watermark.jar");
        HashSet<ClassNode> classes = asmCtx.getClasses();
        classes.stream().filter(cn -> cn.fields.size() >= 3 && cn.methods.size() >= 3)
                .forEach(classNode -> {
                    FieldNode node = new FieldNode(
                            (random.nextBoolean() ? ACC_PRIVATE : ACC_PUBLIC)
                                    | (random.nextBoolean() ? ACC_STATIC : 0)
                                    | (random.nextBoolean() ? ACC_FINAL : 0),
                            "watermark", "J", user.getAsTag(), null);
                    classNode.fields.add(node);
                });
        asmCtx.save(output);
        return output;
    }
}
