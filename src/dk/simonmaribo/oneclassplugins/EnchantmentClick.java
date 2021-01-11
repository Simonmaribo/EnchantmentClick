package dk.simonmaribo.oneclassplugins;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class EnchantmentClick extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
    }

    @EventHandler
    public void onInvCursorClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR){
            if(event.getCursor() != null && event.getCurrentItem() != null) {
                if (event.getCursor().getType() == Material.ENCHANTED_BOOK) {
                    final String typeNameString = event.getCurrentItem().getType().name();
                    if(typeNameString.endsWith("_HELMET")
                        || typeNameString.endsWith("_CHESTPLATE")
                        || typeNameString.endsWith("_LEGGINGS")
                        || typeNameString.endsWith("_BOOTS")
                        || typeNameString.endsWith("_SWORD")
                        || typeNameString.endsWith("_AXE")
                        || typeNameString.endsWith("_PICKAXE")
                        || typeNameString.endsWith("_HOE")
                        || typeNameString.endsWith("_SHOVEL")){



                        ItemStack bookItem = event.getCursor();
                        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)bookItem.getItemMeta();
                        if(bookMeta == null) return;
                        Map<Enchantment, Integer> bookEnchants = bookMeta.getStoredEnchants();

                        ItemStack clickedItem = event.getCurrentItem();
                        ItemMeta clickedMeta = clickedItem.getItemMeta();
                        boolean clickedHasEnchants = false;
                        Map<Enchantment, Integer> clickedEnchants = null;
                        if(clickedMeta.hasEnchants()) {
                            clickedHasEnchants = true;
                            clickedEnchants = clickedMeta.getEnchants();
                        }

                        boolean cancel = false;
                        for(Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()){
                            if (getConfig().getBoolean("use-unsafe-enchantments")) {
                                clickedItem.addUnsafeEnchantment(entry.getKey(), clickedMeta.getEnchantLevel(entry.getKey()) + entry.getValue());
                                cancel = true;
                            } else{
                                if(entry.getKey().canEnchantItem(clickedItem)){
                                    if(clickedHasEnchants){
                                        for(Map.Entry<Enchantment, Integer> entry2 : clickedEnchants.entrySet()){
                                            if(!entry.getKey().conflictsWith(entry2.getKey())){
                                                int maxLevel = entry.getKey().getMaxLevel();
                                                if(clickedMeta.getEnchantLevel(entry.getKey()) + entry.getValue() > maxLevel){
                                                    clickedItem.addEnchantment(entry.getKey(), maxLevel);
                                                } else{
                                                    clickedItem.addEnchantment(entry.getKey(), clickedMeta.getEnchantLevel(entry.getKey()) + entry.getValue());
                                                }
                                                cancel = true;
                                            }
                                        }
                                    }else {
                                        int maxLevel = entry.getKey().getMaxLevel();
                                        if(clickedMeta.getEnchantLevel(entry.getKey()) + entry.getValue() > maxLevel){
                                            clickedItem.addEnchantment(entry.getKey(), maxLevel);
                                        } else{
                                            clickedItem.addEnchantment(entry.getKey(), clickedMeta.getEnchantLevel(entry.getKey()) + entry.getValue());
                                        }
                                        cancel = true;
                                    }
                                }
                            }
                        }
                        if(cancel) {
                            event.getWhoClicked().setItemOnCursor(null);
                            event.setCancelled(cancel);
                        }
                    }
                }
            }
        }
    }
}
