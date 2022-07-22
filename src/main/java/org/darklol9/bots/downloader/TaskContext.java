package org.darklol9.bots.downloader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.darklol9.bots.products.Product;

@Getter
@AllArgsConstructor(staticName = "of")
public class TaskContext {

    private User user;
    private Product product;
    @Setter
    private byte[] bytes;

}
