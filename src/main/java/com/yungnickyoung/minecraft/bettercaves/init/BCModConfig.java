package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BCModConfig {
    public static void init() {
        createDirectory();
        createReadMe();
        // Register config with Forge
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Configuration.SPEC, BCSettings.BASE_CONFIG_NAME);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCModConfig::configChanged);
    }

    /**
     * Parses the whitelisted dimensions string and updates the stored values.
     */
    public static void configChanged(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();

        if (config.getSpec() == Configuration.SPEC) {
            String rawStringofList = Configuration.whitelistedDimensions.get();
            int strLen = rawStringofList.length();

            // Validate the string's format
            if (strLen < 2 || rawStringofList.charAt(0) != '[' || rawStringofList.charAt(strLen - 1) != ']') {
                BetterCaves.LOGGER.error("INVALID VALUE FOR SETTING 'Whitelisted Dimension IDs'. Using empty list instead...");
                BetterCaves.whitelistedDimensions = Lists.newArrayList();
                return;
            }

            // Parse string to list
            List<String> inputListOfDimensionStrings = Lists.newArrayList(rawStringofList.substring(1, strLen - 1).split(",\\s*"));

            // Parse list of strings, removing any entries that don't match existing dimension names
            List<String> whitelistedDimensions = Lists.newArrayList();
            whitelistedDimensions.addAll(inputListOfDimensionStrings);

            BetterCaves.whitelistedDimensions = whitelistedDimensions;
        }
    }

    private static void createDirectory() {
        File parentDir = new File(FMLPaths.CONFIGDIR.get().toString(), BCSettings.CUSTOM_CONFIG_PATH);
        BetterCaves.customConfigDir = new File(parentDir, BCSettings.VERSION_PATH);
        try {
            String filePath = BetterCaves.customConfigDir.getCanonicalPath();
            if (BetterCaves.customConfigDir.mkdirs()) {
                BetterCaves.LOGGER.info("Creating directory for dimension-specific Better Caves configs at " + filePath);
            }
        } catch (IOException e) {
            BetterCaves.LOGGER.error("ERROR creating Better Caves config directory.");
        }
    }

    private static void createReadMe() {
        Path path = Paths.get(FMLPaths.CONFIGDIR.get().toString(), BCSettings.CUSTOM_CONFIG_PATH, "README.txt");
        File readme = new File(path.toString());
        if (!readme.exists()) {
            String readmeText =
                "This directory is for adding YUNG's Better Caves configurations specific to certain dimensions.\n" +
                "Starting with Minecraft 1.16, this directory serves as the base directory for all future versions.\n" +
                "\n" +
                "For example, to add a dimension-specific config to the Nether in 1.16, you need to first create a\n" +
                "directory named 1_16 in this folder.\n" +
                "(This will be created for you the first time you run YUNG's Better Caves for 1.16).\n" +
                "Then, in the 1_16 folder, create a config file named DIM_minecraft-the_nether.toml.\n" +
                "\n" +
                "NOTE -- YOU MUST HAVE THE DIMENSIONS YOU WANT TO USE WHITELISTED (OR HAVE GLOBAL WHITELISTING ENABLED)\n" +
                "IN THE BASE CONFIG FILE FOR THIS TO WORK.\n" +
                "\n" +
                "FOR MORE INFORMATION, CHECK OUT THE WIKI -- https://github.com/yungnickyoung/YUNGs-Better-Caves/wiki";
            try {
                Files.write(path, readmeText.getBytes());
            } catch (IOException e) {
                BetterCaves.LOGGER.error("Unable to create README file!");
            }
        }
    }
}
