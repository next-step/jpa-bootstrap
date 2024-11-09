package bootstrap;

import java.io.File;
import java.net.URL;

public class FileSystemExplorer {

    public File resolveBaseDir(String basePackage) {
        final URL resourcePath = getResourcePath(basePackage);
        if (resourcePath == null) {
            return new File("");
        }
        return new File(resourcePath.getFile());
    }

    private URL getResourcePath(String basePackage) {
        String path = basePackage.replace(".", "/");
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    public boolean isInvalidDirectory(File file) {
        return !file.exists() || !file.isDirectory() || file.listFiles() == null;
    }

}
