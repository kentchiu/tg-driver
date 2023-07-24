package com.kent.tg.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TgFileUtilsTest {

    @BeforeAll
    static void setUp() {
    }

    @AfterAll
    static void tearDown() {
    }

    private static void expectFolder(Path tempDir, String videoPath) {
        assertTrue(Files.exists(tempDir.resolve(videoPath)));
        assertTrue(Files.isDirectory(tempDir.resolve(videoPath)));
    }

    @Test
    void getInstance(@TempDir Path tempDir) {
        assertFalse(Files.exists(tempDir.resolve("tdlight-session")));
        TgFileUtils instance = TgFileUtils.getInstance();
        instance.init(tempDir);

        expectFolder(tempDir, "tdlight-session");
        expectFolder(tempDir, "logs");
        expectFolder(tempDir, "tdlight-session/downloads/videos");
        expectFolder(tempDir, "tdlight-session/data");
    }

    @Test
    void getPathByName(@TempDir Path tempDir) {
        TgFileUtils instance = TgFileUtils.getInstance();
        instance.init(tempDir);
        assertEquals(tempDir, instance.getPathByName(TgFileUtils.HOME));
        assertEquals(tempDir.resolve("tdlight-session"), instance.getPathByName(TgFileUtils.SESSION_HOME));
        assertEquals(tempDir.resolve("tdlight-session/downloads/videos"),
                instance.getPathByName(TgFileUtils.VIDEO_HOME));
        assertEquals(tempDir.resolve("tdlight-session/downloads/photos"),
                instance.getPathByName(TgFileUtils.PHOTO_HOME));
        assertEquals(tempDir.resolve("tdlight-session/data/thumbnails"),
                instance.getPathByName(TgFileUtils.THUMBNAIL_HOME));
        assertEquals(tempDir.resolve("tdlight-session/data/profile_photos"),
                instance.getPathByName(TgFileUtils.PROFILE_HOME));
        assertEquals(tempDir.resolve("tdlight-session/data/tg.properties"),
                instance.getPathByName(TgFileUtils.CONFIG_FILE));
        assertEquals(tempDir.resolve("database/db.sqlite"), instance.getPathByName(TgFileUtils.DB_FILE));
        assertEquals(tempDir.resolve("database/td.binlog"), instance.getPathByName(TgFileUtils.BINLOG_FILE));
    }

}
