package org.darklol9.bots.database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.SneakyThrows;
import org.darklol9.bots.JBot;
import org.darklol9.bots.utils.configuration.ConfigurationSection;
import org.darklol9.bots.utils.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

@Getter
public class DatabaseManager {

    private final String query;
    private Session session;
    private Connection connection;

    @SneakyThrows
    public DatabaseManager(JBot bot) {

        YamlConfiguration config = bot.getConfig();
        ConfigurationSection conf = config.getConfigurationSection("database");
        Logger logger = bot.getLogger();

        query = conf.getString("query");

        if (conf.getBoolean("use-ssl")) {
            ConfigurationSection ssl = conf.getConfigurationSection("ssl");
            JSch jsch = new JSch();
            session = jsch.getSession(ssl.getString("username"), ssl.getString("host"), ssl.getInt("port"));
            session.setPassword(ssl.getString("password"));
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.setPortForwardingL(conf.getInt("port"), "localhost", conf.getInt("port"));
        }

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = String.format("jdbc:mysql://%s:%d/%s",
                conf.getString("host"), conf.getInt("port"), conf.getString("dbname"));

        String username = conf.getString("username");
        String password = conf.getString("password");

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                logger.info("Connected to database");
            }
        } catch (Exception e) {
            logger.error("Failed to connect to database");
//            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void close() {
        if (connection != null) connection.close();
        if (session != null) session.disconnect();
    }

}
