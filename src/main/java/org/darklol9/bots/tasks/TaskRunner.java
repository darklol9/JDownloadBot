package org.darklol9.bots.tasks;

import lombok.AllArgsConstructor;
import org.darklol9.bots.JBot;
import org.darklol9.bots.downloader.TaskContext;

import java.util.List;

@AllArgsConstructor
public class TaskRunner {

    private TaskContext ctx;

    public void process() {
        JBot bot = JBot.getInstance();
        List<Task> tasks = bot.getTaskManager().getTasks();
        tasks.forEach(obfuscator -> obfuscator.process(ctx));
    }

}
