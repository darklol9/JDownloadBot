package org.darklol9.bots.downloader;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.apache.commons.io.IOUtils;
import org.darklol9.bots.JBot;
import org.darklol9.bots.database.DatabaseManager;
import org.darklol9.bots.products.Product;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FileDownloader {

    @SneakyThrows
    public static void process(User user, Product product, InteractionHook hook) {
        JBot bot = JBot.getInstance();
        DatabaseManager db = bot.getDatabase();
        Connection connection = db.getConnection();
        try {
            if (connection != null) {
                PreparedStatement stmt = connection.prepareStatement(
                        String.format(db.getQuery(), product.getTable()));
                stmt.setBigDecimal(1, BigDecimal.valueOf(user.getIdLong()));
                ResultSet result = stmt.executeQuery();
                boolean whitelisted = result.next();
                result.close();
                stmt.close();
                if (whitelisted) {

                    int index = 0;
                    for (TaskContext ctx : bot.getTaskQueue().getQueue()) {
                        index++;
                        if (ctx.getUser().getIdLong() == user.getIdLong()) {
                            hook.sendMessage(String.format(bot.getConfig().getString("messages.already-queued"),
                                    index, ctx.getProduct().getName())).queue();
                            return;
                        }
                    }

                    URL url = new URL(product.getUrl());
                    byte[] bytes = IOUtils.toByteArray(url);
                    TaskContext ctx = TaskContext.of(user, product, hook, bytes);
                    bot.getTaskQueue().enqueue(ctx);
                } else {
                    hook.sendMessage(String.format(bot.getConfig().getString("messages.not-whitelisted"), product.getName())).queue();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
