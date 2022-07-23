package org.darklol9.bots.tasks;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.darklol9.bots.JBot;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.products.Product;
import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Getter
public class TaskQueue implements Runnable {

    private final LinkedBlockingQueue<TaskContext> queue;
    private TaskContext currentTask;

    public TaskQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    @SneakyThrows
    @Override
    public void run() {

        Logger logger = JBot.getInstance().getLogger();

        while (true) {

            if (!queue.isEmpty()) {
                TaskContext task = queue.peek();
                TaskRunner runner = new TaskRunner(task);
                currentTask = task;
                runner.process();

                User user = task.getUser();
                Product product = task.getProduct();

                try {
                    user.openPrivateChannel().queue(pc -> {
                        pc.sendFile(task.getBytes(), product.getName() + ".jar").queue(m -> {
                            m.delete().queueAfter(60, TimeUnit.SECONDS);
                        });
                    });
                } catch (Exception ex) {
                    //
                }

                if (queue.remove(currentTask)) {
                    currentTask = null;
                }

                logger.info("Task completed, " + user.getAsTag() + " downloaded product " + product.getName());

                Thread.sleep(1000);
            }

            Thread.sleep(50);
        }
    }

    @SneakyThrows
    public void enqueue(TaskContext ctx) {
        queue.add(ctx);

        JBot bot = JBot.getInstance();
        InteractionHook hook = ctx.getHook();

        hook.sendMessage(String.format(bot.getConfig().getString("messages.enqueue"),
                bot.getTaskQueue().size())).queue();
    }

    public int size() {
        return queue.size();
    }
}
