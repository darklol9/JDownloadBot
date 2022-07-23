package org.darklol9.bots.tasks.watermarking;

import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.tasks.Task;
import org.darklol9.bots.utils.CryptUtil;
import org.darklol9.bots.utils.asm.ASMContext;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;

public class ConstantsWatermark implements Task {

    private final char[] password = new char[]{
            0x14, 0x01, 0x24, 0x64, 0x75, 0x124, 0xff1, 0x42f, 0xdad, 0xaa3, 0x64f, 0xccd, 0xddd1, 0xf89, 0x142f, 0x142, 0x0991
    };

    private final byte[] salt = new byte[]{
            (byte) 0xc4, 0x42, (byte) 0x91, (byte) 0x84, (byte) 0xde, 0x68, (byte) 0xfa, 0x4a
    };

    @Override
    public File process(TaskContext ctx, ASMContext asmCtx, File input) {

        CryptUtil.init(password, salt);

        User user = ctx.getUser();
        File output = new File(input.getParentFile(), "c_watermark.jar");
        HashSet<ClassNode> classes = asmCtx.getClasses();

        classes.forEach(classNode -> {

            String cid = CryptUtil.aes(user.getId() + "_Polar_" + RandomStringUtils.randomAlphabetic(5));

            for (MethodNode method : classNode.methods) {

                if (Modifier.isNative(method.access) || Modifier.isAbstract(method.access))
                    continue;

                String id = CryptUtil.aes(user.getId() + "_Polar_" + RandomStringUtils.randomAlphabetic(5));

                method.signature = "()T" + id + ";";

                if (method.parameters == null)
                    method.parameters = new ArrayList<>();
                method.parameters.add(new ParameterNode(id, ACC_MANDATED));

            }
        });
        asmCtx.save(output);
        return output;
    }
}
