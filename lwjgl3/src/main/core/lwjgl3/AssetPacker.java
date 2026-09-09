package core.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public final class AssetPacker {

    public static void main(String[] args) {
        TexturePacker.Settings settings =
            new TexturePacker.Settings();

        settings.paddingX = 2;
        settings.paddingY = 2;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;

        settings.duplicatePadding = true;
        settings.bleed = true;
        settings.combineSubdirectories = true;
        settings.flattenPaths = false;

        settings.maxWidth = 8192*2;
        settings.maxHeight = 8192*2;
        settings.pot = true;

//        TexturePacker.process(
//            settings,
//            "assets/unpacked/tiles/tiles",
//            "assets/unpacked/packed",
//            "tiles"
//        );
//
//        TexturePacker.process(
//            settings,
//            "assets/unpacked/blocks/industrial",
//            "assets/unpacked/packed",
//            "blocks"
//        );

        TexturePacker.process(
            settings,
            "../assets/unpacked/tiles/tiles",
            "../assets/packed/tiles/tiles",
            "tile assets"
        );
    }
}
