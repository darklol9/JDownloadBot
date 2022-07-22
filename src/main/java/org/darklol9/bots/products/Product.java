package org.darklol9.bots.products;

import lombok.Getter;
import org.darklol9.bots.JBot;
import org.darklol9.bots.utils.configuration.file.YamlConfiguration;

@Getter
public class Product {

    private final String id;

    private final String name;
    private final String url;

    private final String table;

    public Product(String id) {
        YamlConfiguration config = JBot.getInstance().getConfig();
        this.id = id;
        this.name = config.getString("products." + id + ".name");
        this.url = config.getString("products." + id + ".url");
        this.table = config.getString("products." + id + ".table");
    }

}
