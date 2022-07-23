package org.darklol9.bots.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<File> walkFolder(File folder) {
        List<File> files = new ArrayList<>();
        if (!folder.isDirectory()) {
            if (folder.getName().endsWith(".jar"))
                files.add(folder);
            return files;
        }
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(walkFolder(file));
            } else {
                if (file.getName().endsWith(".jar"))
                    files.add(file);
            }
        }
        return files;
    }

}
