package org.darklol9.bots.tasks.obfuscation.proguard;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.darklol9.bots.downloader.TaskContext;
import org.darklol9.bots.products.Product;
import org.darklol9.bots.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;

public class ProguardObfuscator implements Task {

    @Override
    @SneakyThrows
    public void process(TaskContext ctx) {
        ProguardConfig config = new ProguardConfig();
        User user = ctx.getUser();
        Product product = ctx.getProduct();
        File tmp = new File(String.format("downloads/tmp/%s/%s", user.getIdLong(), product.getName()));
        if (!tmp.exists()) tmp.mkdirs();
//        File conf = config.createConfig(tmp);

        File input = new File(tmp + File.separator + "input.jar");
        FileOutputStream fos = new FileOutputStream(input);
        fos.write(ctx.getBytes());
        fos.close();

//        File output = new File(tmp + File.separator + "output.jar");
//        FileInputStream fis = new FileInputStream(output);
//        ctx.setBytes(IOUtils.toByteArray(fis));
//        fis.close();

        input.delete();
    }
}
