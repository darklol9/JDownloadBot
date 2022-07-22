package org.darklol9.bots.tasks;

import org.darklol9.bots.downloader.TaskContext;

public interface Task {

    void process(TaskContext ctx);

}
