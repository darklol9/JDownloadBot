package org.darklol9.bots.tasks;

import lombok.Getter;
import org.darklol9.bots.tasks.obfuscation.proguard.ProguardObfuscator;
import org.darklol9.bots.tasks.watermarking.ConstantsWatermark;
import org.darklol9.bots.tasks.watermarking.FieldWatermark;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TaskManager {

    private final List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();

        tasks.add(new ConstantsWatermark());
        tasks.add(new FieldWatermark());
        tasks.add(new ProguardObfuscator());
    }

}
