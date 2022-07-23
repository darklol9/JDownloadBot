package org.darklol9.bots.downloader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.darklol9.bots.products.Product;

@AllArgsConstructor(staticName = "of")
@Getter
public class TaskContext {

    private User user;
    private Product product;
    private InteractionHook hook;

    @Setter
    private byte[] bytes;


}
