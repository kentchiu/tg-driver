package com.kent.tg.client;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * home folder structure example
 * <pre>
 * ├── database
 * ├── logs
 * └── tdlight-session
 *     ├── data
 *     │    ├── db.sqlite
 *     │    ├── profile_photos
 *     │    ├── td.binlog
 *     │    ├── temp
 *     │    ├── tg.properties
 *     │    └── thumbnails
 *     └── downloads
 *         ├── photos
 *         │     ├── 4904477671778529471_121.jpg
 *         │     ├── 4913598962544520043_120.jpg
 *         │     └── 4935966306113530625_121.png
 *         ├── temp
 *         └── videos
 *               ├── 1234.mp4
 *               ├── abcd.mov
 *               └── foo_bar.mp4
 * </pre>
 */
public class TgFileUtils {

    public static final String HOME = "";
    public static final String SESSION_HOME = "tdlight-session";
    public static final String VIDEO_HOME = "tdlight-session/downloads/videos";
    public static final String PHOTO_HOME = "tdlight-session/downloads/photos";
    public static final String PROFILE_HOME = "tdlight-session/data/profile_photos";
    public static final String THUMBNAIL_HOME = "tdlight-session/data/thumbnails";
    public static final String BINLOG_FILE = "database/td.binlog";
    public static final String DB_FILE = "database/db.sqlite";
    public static final String CONFIG_FILE = "tdlight-session/data/tg.properties";
    //    private static Path TG_HOME;
    private static TgFileUtils instance;
    private final Logger logger = LoggerFactory.getLogger(TgFileUtils.class);
    private Path home;

    /**
     * Telegram File Utils
     *
     * @return Telegram File Utils Instance.
     */
    public static TgFileUtils getInstance() {
        if (instance == null) {
            instance = new TgFileUtils();
        }
        return instance;
    }

    @NotNull
    public static String toRelatePath(String absolutePath) {
        String p = absolutePath.replaceAll("\\\\", "/");
        String relatePath;
        if (StringUtils.contains(p, "/data/")) {
            relatePath = "data/" + StringUtils.substringAfter(p, "/data/");
        } else if (StringUtils.contains(p, "/downloads/")) {
            relatePath = "downloads/" + StringUtils.substringAfter(p, "/downloads/");
        } else {
            relatePath = p;
        }
        return relatePath;
    }

    private void TgFileUtils() {

    }

    /**
     * @param tgHome Location of tg-driver folder, null value is acceptable if folder is already initialized.
     */

    public void init(Path tgHome) {
        this.home = tgHome;
        try {
            Files.createDirectories(tgHome);
            Files.createDirectories(tgHome.resolve("tdlight-session"));
            Files.createDirectories(tgHome.resolve("logs"));

            // folder may not be created if telegram server not start yet, so that, we create it manually
            // for permission reason, we create folder instead created by TG
            Files.createDirectories(tgHome.resolve(VIDEO_HOME));
            Files.createDirectories(tgHome.resolve(PHOTO_HOME));
            Files.createDirectories(tgHome.resolve(THUMBNAIL_HOME));
            Files.createDirectories(tgHome.resolve(PROFILE_HOME));
            Files.createDirectories(tgHome.resolve(SESSION_HOME + "/downloads/temp"));
            Files.createDirectories(tgHome.resolve(SESSION_HOME + "/data/temp"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("CREATE TG HOME FAIL", e);
        }
    }


    public Path getPathByName(String name) {
        if (this.home == null) {
            throw new IllegalStateException("Call init() once before further operations");
        }
        return this.home.resolve(name);
    }

    public void deleteFileByName(String name) {
        Path file = getPathByName(name);
        try {
            logger.warn("Deleting file {}", file);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            String msg = "Delete %s fail".formatted(file.toAbsolutePath().toString());
            logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    public Path getFileByLocalFilePath(String localFilePath) {
        return Paths.get(TgFileUtils.getInstance().getPathByName(TgFileUtils.SESSION_HOME).toAbsolutePath().toString(), localFilePath);
    }

    public void deleteProfileHome() {
        Path profile = getPathByName(PROFILE_HOME);
        try {
            FileSystemUtils.deleteRecursively(profile);
        } catch (IOException e) {
            logger.warn("Delete profile home [" + profile + "] fail", e);
            throw new RuntimeException(e);
        }
    }

    public File[] listAllVideoFiles() {
        Path videoHome = getPathByName(TgFileUtils.VIDEO_HOME);
        File[] allVideoFiles = videoHome.toFile().listFiles();
        return allVideoFiles;
    }
}
