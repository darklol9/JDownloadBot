package org.darklol9.bots.utils.asm;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ASMContext {

    private final HashSet<ClassNode> classes;
    private final HashMap<String, byte[]> resources = new HashMap<>();

    @SneakyThrows
    public ASMContext(File input) {
        this.classes = new HashSet<>();
        this.loadJar(input);
    }

    public HashSet<ClassNode> getClasses() {
        return classes;
    }

    public HashMap<String, byte[]> getResources() {
        return resources;
    }

    public void loadJar(File inputFile) throws Exception {
        if (!inputFile.exists()) return;
        JarFile inputJar = new JarFile(inputFile);
        for (Enumeration<JarEntry> iter = inputJar.entries(); iter.hasMoreElements(); ) {
            JarEntry entry = iter.nextElement();
            if (entry.isDirectory()) continue;
            try (InputStream in = inputJar.getInputStream(entry)) {
                byte[] bytes = IOUtils.toByteArray(in);
                if (entry.getName().endsWith(".class") || entry.getName().endsWith(".class/")) {

                    ClassReader reader = new ClassReader(bytes);
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode, ClassReader.SKIP_FRAMES);

                    classes.add(classNode);

                } else {
                    resources.put(entry.getName(), bytes);
                }
            }
        }
        inputJar.close();
    }

    public ClassNode assureLoaded(String owner) {
        if (owner == null) return null;
        for (ClassNode classNode : classes) {
            if (classNode.name.equals(owner)) return classNode;
        }
        return null;
//        throw new NoClassDefFoundError(owner);
    }

    @SneakyThrows
    public void save(File output) {
        JarOutputStream out = new JarOutputStream(new FileOutputStream(output));
        for (ClassNode classNode : classes) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS); // The obfuscators will compute frames for us
            classNode.accept(cw);
            out.putNextEntry(new JarEntry(classNode.name + ".class"));
            out.write(cw.toByteArray());
        }

        resources.forEach((name, data) -> {
            try {
                out.putNextEntry(new JarEntry(name));
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        out.close();
    }
}
