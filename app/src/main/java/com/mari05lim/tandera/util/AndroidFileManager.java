package com.mari05lim.tandera.util;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.util.FileManager;

import java.io.File;

/**
 * DEV Mari05liM
 */
public class AndroidFileManager implements FileManager {

    private final File mBaseDir;

    public AndroidFileManager(File baseDir) {
        mBaseDir = Preconditions.checkNotNull(baseDir, "baseDir cannot be null");
    }

    @Override
    public File getFile(String filename) {
        return new File(mBaseDir, filename);
    }

}