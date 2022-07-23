package org.darklol9.bots.tasks.obfuscation.proguard;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.output.NullPrintStream;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.products.Product;
import org.darklol9.bots.tasks.Task;
import org.darklol9.bots.utils.asm.ASMContext;
import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ProGuard;

import java.io.File;
import java.io.PrintStream;

public class ProguardObfuscator implements Task {

    @Override
    @SneakyThrows
    public File process(TaskContext ctx, ASMContext asmCtx, File input) {
        ProguardConfig config = new ProguardConfig();
        User user = ctx.getUser();
        Product product = ctx.getProduct();

        File output = new File(input.getParentFile(), "proguard.jar");
        File conf = config.createConfig(user, product, input, output);

        PrintStream out = System.out;
        System.setOut(new NullPrintStream());

        Configuration configuration = new Configuration();

        ConfigurationParser parser = new ConfigurationParser(
                new String[]{"@" + conf.getAbsolutePath()}, System.getProperties());

        try {
            parser.parse(configuration);
        } finally {
            parser.close();
        }

        ProGuard proGuard = new ProGuard(configuration);
        proGuard.execute();

        System.setOut(out);

        return output;
    }
}
