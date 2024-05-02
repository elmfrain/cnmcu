package com.elmfer.cnmcu.util;

import java.io.InputStream;

import net.minecraft.util.Identifier;

/**
 * Mod's own resource loader for loading resources like models and textures.
 * This guarantees that an asset can be loaded at any time.
 * Useful for loading assets at the very start of the game when Minecraft's resource manager is not yet initialized.
 */
public class ResourceLoader {
    
    private ResourceLoader() {
        
    }
    
    public static InputStream getInputStream(Identifier location) {
        return ResourceLoader.class
                .getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath());
    }
}
