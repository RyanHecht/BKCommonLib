package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;

/**
 * Tests the correct functioning of map canvas functions by visual inspection
 */
public class MapCanvasFunctionsTest {

    @Ignore
    @Test
    public void testPixelMove() {
        MapTexture map = MapTexture.createEmpty(200, 200);
        map.fillRectangle(0, 0, 50, 50, MapColorPalette.COLOR_RED);
        map.fillRectangle(20, 40, 100, 90, MapColorPalette.COLOR_BLUE);

        MapDebugWindow.showMap(map);

        map.movePixels(-10, -30);

        MapDebugWindow.showMapForever(map);
    }

    @Ignore
    @Test
    public void testDepthBuffer() {
        MapTexture map = MapTexture.createEmpty(64, 64);
        
        // Load the source textures
        MapResourcePack texturePack = MapResourcePack.VANILLA;

        // Test brush masks with depth buffer
        //map.setBrushMask(texturePack.getTexture("blocks/vine"));

        map.setDrawDepth(0);
        map.draw(texturePack.getTexture("blocks/brick"), 5, 5);
        assertFalse(map.hasMoreDepth());

        map.setDrawDepth(1);
        map.draw(texturePack.getTexture("blocks/coal_ore"), 12, 12);
        assertFalse(map.hasMoreDepth());

        map.setDrawDepth(0);
        map.clearRectangle(5, 5, 16, 16);
        assertTrue(map.hasMoreDepth());

        map.setDrawDepth(1);
        map.draw(texturePack.getTexture("blocks/coal_ore"), 12, 12);
        assertFalse(map.hasMoreDepth());

        map.setDrawDepth(3);
        map.draw(texturePack.getTexture("blocks/bookshelf"), 23, 23);
        assertFalse(map.hasMoreDepth());

        map.setDrawDepth(2);
        map.draw(texturePack.getTexture("blocks/grass_side"), 15, 18);
        assertFalse(map.hasMoreDepth());

        MapDebugWindow.showMapForever(map, 10);
    }

    //@Test
    public void testBrushMask() {
        MapTexture map = MapTexture.createEmpty(64, 64);

        // Load the source textures
        MapResourcePack texturePack = MapResourcePack.VANILLA;

        map.setBrushMask(texturePack.getTexture("blocks/ladder"));
        map.fill(MapColorPalette.COLOR_RED);

        map.draw(texturePack.getTexture("blocks/stone"), 30, 5);

        MapDebugWindow.showMapForever(map, 10);
    }
}
