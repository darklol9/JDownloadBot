package org.darklol9.bots.data;

import lombok.SneakyThrows;
import org.darklol9.bots.utils.configuration.file.YamlConfiguration;

import java.io.File;

public class Storage {

    private static final YamlConfiguration data;

    static {
        data = YamlConfiguration.loadConfiguration(new File("data.yml"));
    }

    public static long getPanelId(String panelType) {
        return data.getLong("panels." + panelType);
    }

    @SneakyThrows
    public static void setPanelId(String panelType, long id) {
        data.set("panels." + panelType, id);
        data.save(new File("data.yml"));
    }

}
