package org.darklol9.bots.tasks.obfuscation.proguard;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.darklol9.bots.products.Product;
import org.darklol9.bots.tasks.obfuscation.ObfuscationConfig;
import org.darklol9.bots.utils.FileUtil;

import java.io.File;
import java.io.FileWriter;

public class ProguardConfig implements ObfuscationConfig {

    @SneakyThrows
    @Override
    public File createConfig(User user, Product product, File input, File output) {

        File parentFolder = input.getParentFile();

        File dictionary = new File(parentFolder + File.separator + "dictionary.txt");
        dictionary.createNewFile();

        FileWriter writer = new FileWriter(dictionary, true);

        int amount = RandomUtils.nextInt(100, 1000);
        for (int i = 0; i < amount; i++) {
            String s = RandomStringUtils.randomAlphabetic(3).toLowerCase();
            writer.write("Polar_" + s + "\n");
        }

        writer.close();

        File config = new File(parentFolder + File.separator + "proguard.pro");
        config.createNewFile();

        writer = new FileWriter(config);

        StringBuilder contents = new StringBuilder(
                "-injars " + input.getAbsolutePath() + "\n" +
                        "-outjars " + output.getAbsolutePath() + "\n" + "\n"
        );

        for (String library : product.getLibraries()) {
            for (File file : FileUtil.walkFolder(new File(library))) {
                contents.append("-libraryjars ").append(file.getAbsolutePath()).append("\n");
            }
        }

        String path = System.getProperty("sun.boot.class.path");
        if (path != null) {
            String[] pathFiles = path.split(";");
            File parent = new File(pathFiles[0]).getParentFile();

            for (File file : FileUtil.walkFolder(parent)) {
                if (file.getName().endsWith(".jar")) {
                    contents.append("-libraryjars ").append(file.getAbsolutePath()).append("\n");
                }
            }
        }

        contents.append("\n" +
                "-dontskipnonpubliclibraryclassmembers\n" +
                "-forceprocessing\n" +
                "-dontshrink\n" +
                "-dontoptimize\n" +
                "-obfuscationdictionary '" + dictionary.getAbsolutePath() + "'\n" +
                "-classobfuscationdictionary '" + dictionary.getAbsolutePath() + "'\n" +
                "-packageobfuscationdictionary '" + dictionary.getAbsolutePath() + "'\n" +
                "-useuniqueclassmembernames\n" +
                "-dontusemixedcaseclassnames\n" +
                "-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod\n" +
                "-keepparameternames\n" +
                "-renamesourcefileattribute SourceFile\n" +
                "-ignorewarnings\n" +
                "-dontwarn\n" +
                "-flattenpackagehierarchy ''\n" +
//                "-overloadaggressively\n" +
                "\n" +
                "\n" +
                "# Keep - Applications. Keep all application classes, along with their 'main' methods.\n" +
                "-keepclasseswithmembers public class * {\n" +
                "    public static void main(java.lang.String[]);\n" +
                "}\n" +
                "\n" +
                "# Also keep - Enumerations. Keep the special static methods that are required in\n" +
                "# enumeration classes.\n" +
                "-keepclassmembers enum  * {\n" +
                "    public static **[] values();\n" +
                "    public static ** valueOf(java.lang.String);\n" +
                "}\n" +
                "\n" +
                "\n");

        writer.write(contents.toString());

        writer.close();

        return config;
    }
}
