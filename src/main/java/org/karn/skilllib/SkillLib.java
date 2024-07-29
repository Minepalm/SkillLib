package org.karn.skilllib;

import org.bukkit.plugin.java.JavaPlugin;

public final class SkillLib extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("SkillLib online!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
