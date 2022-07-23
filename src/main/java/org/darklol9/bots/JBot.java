package org.darklol9.bots;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.darklol9.bots.database.DatabaseManager;
import org.darklol9.bots.events.EventCaller;
import org.darklol9.bots.listeners.SystemListener;
import org.darklol9.bots.products.Product;
import org.darklol9.bots.tasks.TaskManager;
import org.darklol9.bots.tasks.TaskQueue;
import org.darklol9.bots.utils.configuration.ConfigurationSection;
import org.darklol9.bots.utils.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class JBot {

    @Getter
    private static JBot instance;

    private JDA application;

    private YamlConfiguration config;

    private Logger logger;

    private DatabaseManager database;

    private List<Product> products;

    private TaskManager taskManager;

    private TaskQueue taskQueue;

    @SneakyThrows
    public void run() {
        //------------------------------------------//
        instance = this;
        logger = JDALogger.getLog(JBot.class);
        //------------------------------------------//
        config = YamlConfiguration.loadConfiguration(new File("config.yml"));
        //------------------------------------------//
        database = new DatabaseManager(this);
        Runtime.getRuntime().addShutdownHook(new Thread(database::close));
        taskManager = new TaskManager();
        taskQueue = new TaskQueue();
        Thread queue = new Thread(taskQueue);
        queue.setName("TaskQueue");
        queue.start();
        //------------------------------------------//
        products = new ArrayList<>();
        ConfigurationSection productsSection = config.getConfigurationSection("products");
        for (String key : productsSection.getKeys(false))
            products.add(new Product(key));
        logger.info("Loaded " + products.size() + " product(s)");
        //------------------------------------------//
        JDABuilder builder = JDABuilder.createDefault(config.getString("token"));
        builder.setStatus(OnlineStatus.ONLINE);
        //------------------------------------------//
        EventCaller caller = new EventCaller(
                Arrays.asList(new SystemListener())
        );
        builder.addEventListeners(caller);
        //------------------------------------------//
        application = builder.build().awaitReady();
        //------------------------------------------//
    }
}
