package com.change_vision.astah.quick.internal.ui;

public class Environment {

    private static final String OS_NAME = System.getProperty("os.name");

    public boolean isWindows() {
        return isOSNameMatch(OS_NAME, "Windows");
    }

    public boolean isMac() {
        return isOSNameMatch(OS_NAME, "Mac");
    }

    private boolean isOSNameMatch(String osName, String osNamePrefix) {
        return OS_NAME != null && osName.startsWith(osNamePrefix);
    }

}
