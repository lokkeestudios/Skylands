package com.lokkeestudios.skylands.npcsystem.event;

import com.lokkeestudios.skylands.npcsystem.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NpcInteractListener implements Listener {

    @EventHandler
    public void onNpcManipulate(final @NonNull PlayerArmorStandManipulateEvent event) {
        if (!Npc.entities.containsKey(event.getRightClicked().getEntityId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onNpcRightClick(final @NonNull PlayerInteractEntityEvent event) {
        if (!Npc.entities.containsKey(event.getRightClicked().getEntityId())) return;

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        final @NonNull Player player = event.getPlayer();

        handleNpcInteract(player);
    }

    @EventHandler
    public void onNpcLeftClick(final @NonNull EntityDamageByEntityEvent event) {
        if (!Npc.entities.containsKey(event.getEntity().getEntityId())) return;
        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player player)) return;
        handleNpcInteract(player);
    }

    private void handleNpcInteract(final @NonNull Player player) {
        // TODO: Handle interaction
    }
}
