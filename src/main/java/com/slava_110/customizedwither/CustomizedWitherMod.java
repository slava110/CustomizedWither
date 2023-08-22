package com.slava_110.customizedwither;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "customizedwither")
public class CustomizedWitherMod {

    public static Logger logger = LogManager.getLogger("CustomizedWither");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        CustomizedWitherConfig.skullConfig.parseEffectsIntoCache();
    }
}
