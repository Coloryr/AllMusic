package com.coloryr.allmusic.server.core.utils;

import com.coloryr.allmusic.server.core.AllMusic;
import com.coloryr.allmusic.server.core.IMusicApi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class MusicApiLoader {
    private static final byte VERSION = '2';

    public static List<IMusicApi> loadFromDirectory(File dir) {
        List<IMusicApi> instances = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            return instances;
        }

        File[] jarFiles = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jarFiles == null) return instances;

        for (File jarFile : jarFiles) {
            AllMusic.log.data("<light_purple>[AllMusic]<yellow>尝试加载api：" + jarFile.getName());
            instances.addAll(loadFromJar(jarFile));
        }
        return instances;
    }

    private static List<IMusicApi> loadFromJar(File jarFile) {
        List<IMusicApi> instances = new ArrayList<>();

        try (JarFile jar = new JarFile(jarFile)) {
            ZipEntry entry1 = jar.getEntry("version");

            if (entry1 == null) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>旧版API，跳过加载：" + jarFile.getName());
                return instances;
            }

            InputStream stream = jar.getInputStream(entry1);
            int size = stream.available();
            byte[] temp = new byte[size];
            stream.read(temp);

            if (temp[0] != VERSION) {
                AllMusic.log.data("<light_purple>[AllMusic]<red>旧版API，跳过加载：" + jarFile.getName());
                return instances;
            }

            Enumeration<JarEntry> entries = jar.entries();
            List<String> classNames = new ArrayList<>();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    String className = name.replace("/", ".")
                            .replace(".class", "");
                    classNames.add(className);
                }
            }

            URL url = jarFile.toURI().toURL();
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, MusicApiLoader.class.getClassLoader())) {
                for (String className : classNames) {
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (!clazz.isInterface() && IMusicApi.class.isAssignableFrom(clazz)) {
                            IMusicApi instance = (IMusicApi) clazz.getDeclaredConstructor().newInstance();
                            instances.add(instance);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instances;
    }
}
