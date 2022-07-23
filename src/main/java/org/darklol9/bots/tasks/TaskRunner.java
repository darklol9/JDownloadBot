package org.darklol9.bots.tasks;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.IOUtils;
import org.darklol9.bots.JBot;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.products.Product;
import org.darklol9.bots.utils.asm.ASMContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class TaskRunner {

    private final TaskContext ctx;

    public TaskRunner(TaskContext ctx) {
        this.ctx = ctx;
    }

    @SneakyThrows
    public void process() {
        JBot bot = JBot.getInstance();
        List<Task> tasks = bot.getTaskManager().getTasks();
        User user = ctx.getUser();
        Product product = ctx.getProduct();

        File tmp = new File("tmp");
        if (!tmp.mkdir() && !tmp.exists()) return;

        File input = new File(tmp + File.separator + "input.jar");
        FileOutputStream fos = new FileOutputStream(input);
        fos.write(ctx.getBytes());
        fos.close();

        for (Task task : tasks) {
            input = task.process(ctx, new ASMContext(input), input);
        }

        System.gc();

        FileInputStream fis = new FileInputStream(input);
        ctx.setBytes(IOUtils.toByteArray(fis));
        fis.close();

    }

}
