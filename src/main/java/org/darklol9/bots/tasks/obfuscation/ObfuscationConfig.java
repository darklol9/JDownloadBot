package org.darklol9.bots.tasks.obfuscation;

import net.dv8tion.jda.api.entities.User;
import org.darklol9.bots.products.Product;

import java.io.File;

public interface ObfuscationConfig {

    File createConfig(User user, Product product, File input, File output);

}
