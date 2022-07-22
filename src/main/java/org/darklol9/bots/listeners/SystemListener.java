package org.darklol9.bots.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.darklol9.bots.JBot;
import org.darklol9.bots.data.Storage;
import org.darklol9.bots.downloader.FileDownloader;
import org.darklol9.bots.events.IListener;
import org.darklol9.bots.events.Listener;
import org.darklol9.bots.utils.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.stream.Collectors;

public class SystemListener implements Listener {

    @IListener
    public void onEvent(GuildReadyEvent event) {

        JBot bot = JBot.getInstance();
        Logger logger = bot.getLogger();
        Guild guild = event.getGuild();
        YamlConfiguration config = bot.getConfig();

        // Download Panel
        {
            long channelId = config.getLong("panels.download.channel");
            TextChannel channel = guild.getTextChannelById(channelId);
            if (channel != null) {
                long messageId = Storage.getPanelId("download");
                Message panel = null;
                if (messageId != 0) {
                    for (Message message : channel.getIterableHistory()) {
                        if (message.getIdLong() == messageId) {
                            panel = message;
                            break;
                        }
                    }
//                    if (panel != null) {
//                        panel.delete().complete();
//                        panel = null;
//                    }
                }
                if (panel == null) {
                    logger.info("Creating download panel");
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(config.getString("panels.download.title"));
                    builder.setDescription(config.getString("panels.download.description"));
                    builder.setFooter(config.getString("panels.download.footer"));
                    builder.setColor(config.getInt("panels.download.color"));
                    builder.setTimestamp(Instant.now());
                    panel = channel.sendMessageEmbeds(builder.build())
                            .setActionRow(bot.getProducts().stream()
                                    .map(product -> Button.primary(product.getId(), product.getName())).collect(Collectors.toList()))
                            .complete();
                    Storage.setPanelId("download", panel.getIdLong());
                    logger.info("Download panel created");
                }
            } else {
                logger.warn("Download panel channel not found or is not set");
            }
        }
    }

    @IListener
    public void onEvent(ButtonInteractionEvent event) {
        JBot bot = JBot.getInstance();
        Logger logger = bot.getLogger();
        Button button = event.getButton();
        bot.getProducts().stream().filter(product -> product.getId().equals(button.getId())).forEach(product -> {
            event.deferReply().setEphemeral(true).queue();
            FileDownloader.process(event.getUser(), product, event.getHook());
        });
    }

}
