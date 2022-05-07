package com.github.idimabr.raphasignrepair.listeners;

import com.github.idimabr.raphasignrepair.RaphaSignRepair;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SignListener implements Listener {

    private RaphaSignRepair plugin;

    public SignListener(RaphaSignRepair plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSign(SignChangeEvent e){
        Player player = e.getPlayer();
        if(!player.hasPermission("raphasignrepair.create")) return;
        if(!e.getLine(0).equalsIgnoreCase(plugin.getConfig().getString("LineToCreateSign"))) return;

        int cost = plugin.getConfig().getInt("RepairCost");
        String costFormated = cost > 1000 ? Math.round((cost / 100.0) / 10.0) + "K" : cost + "c";

        String line1 = plugin.getConfig().getString("Sign.Line1").replace("%cost%", costFormated).replace("&","§");
        String line2 = plugin.getConfig().getString("Sign.Line2").replace("%cost%", costFormated).replace("&","§");
        String line3 = plugin.getConfig().getString("Sign.Line3").replace("%cost%", costFormated).replace("&","§");
        String line4 = plugin.getConfig().getString("Sign.Line4").replace("%cost%", costFormated).replace("&","§");

        if(line1.length() > 15)
            line1 = line1.substring(0, 15);
        if(line2.length() > 15)
            line2 = line2.substring(0, 15);
        if(line3.length() > 15)
            line3 = line3.substring(0, 15);
        if(line4.length() > 15)
            line4 = line4.substring(0, 15);

        e.setLine(0, line1);
        e.setLine(1, line2);
        e.setLine(2, line3);
        e.setLine(3, line4);
        player.sendMessage("§aPlaca de reparação criada com sucesso.");
        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Success")), 0.5f, 0.5f);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Block block = e.getClickedBlock();
        if(!block.getType().toString().contains("SIGN")) return;

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if(lines.length == 0) return;

        String line1 = plugin.getConfig().getString("Sign.Line1").replace("&","§");
        if(line1.length() > 15)
            line1 = line1.substring(0, 15);
        
        if(!lines[0].equals(line1)) return;

        ItemStack item = player.getItemInHand();
        if(item.getType().getMaxDurability() == 0 || item.getType() == Material.AIR) {
            player.sendMessage(plugin.getConfig().getString("Message.NotReparable").replace("&","§"));
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Error")), 0.5f, 0.5f);
            return;
        }

        if (item.getDurability() == 0) {
            player.sendMessage(plugin.getConfig().getString("Message.FullRepair").replace("&","§"));
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Error")), 0.5f, 0.5f);
            return;
        }

        int cost = plugin.getConfig().getInt("RepairCost");
        String costFormated = cost > 1000 ? Math.round((cost / 100.0) / 10.0) + "K" : cost + "c";

        if(plugin.getVault().getBalance(player.getName()) < cost){
            player.sendMessage(plugin.getConfig().getString("Message.NotMoney").replace("%cost%",costFormated).replace("&","§"));
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Error")), 0.5f, 0.5f);
            return;
        }
        
        item.setDurability((short) 0);
        player.setItemInHand(item);
        plugin.getVault().withdrawPlayer(player.getName(), cost);
        player.sendMessage(plugin.getConfig().getString("Message.Repair").replace("%cost%",costFormated).replace("&","§"));
        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound.Success")), 0.5f, 0.5f);
    }
}
