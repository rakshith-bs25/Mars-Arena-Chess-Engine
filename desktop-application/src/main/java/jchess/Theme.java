package jchess;

import java.io.File;

/**
 * Simple value object for UI themes.
 * Replaces raw String theme names with a reference type.
 */
public final class Theme {

    private final String name;

    public Theme(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "default";
        } else {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    /** Returns relative path like "theme/default/images/Preview.png". */
    public String imagePath(String imageName) {
        return "theme/" + name + "/images/" + imageName;
    }

    /** Absolute file system path based on jar/project location. */
    public String absoluteImagePath(String imageName) {
        String base = GUI.getJarPath() + File.separator + "theme" + File.separator + name + File.separator + "images";
        return base + File.separator + imageName;
    }
}
