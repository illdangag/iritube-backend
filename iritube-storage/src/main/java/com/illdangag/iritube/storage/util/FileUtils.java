package com.illdangag.iritube.storage.util;

import java.io.File;
import java.util.Objects;

public class FileUtils {
    public static void scanFile(File rootDirectory, ScanFileFunc scanFileFunc) {
        File[] files = rootDirectory.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isFile()) {
                scanFileFunc.func(files[i]);
            } else if (files[i].isDirectory()) {
                scanFile(files[i], scanFileFunc);  // 재귀함수 호출
            }
        }
    }

    @FunctionalInterface
    public interface ScanFileFunc {
        void func(File file);
    }
}
