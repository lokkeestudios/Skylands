package com.lokkeestudios.skylands.npcsystem.event;

import com.lokkeestudios.skylands.npcsystem.NpcManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NpcInteractListener implements Listener {

    /**
     * The main {@link NpcManager} instance.
     */
    private final @NonNull NpcManager npcManager;

    /**
     * Constructs the {@link NpcInteractListener}.
     *
     * @param npcManager the main {@link NpcManager} instance
     */
    public NpcInteractListener(
            final @NonNull NpcManager npcManager
    ) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onNpcManipulate(final @NonNull PlayerArmorStandManipulateEvent event) {
        if (!npcManager.isEntityNpc(event.getRightClicked())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onNpcRightClick(final @NonNull PlayerInteractEntityEvent event) {
        final @NonNull Entity entity = event.getRightClicked();

        if (!(entity instanceof LivingEntity)) return;
        if (!npcManager.isEntityNpc((LivingEntity) entity)) return;

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        final @NonNull Player player = event.getPlayer();

        handleNpcInteract(player);
    }

    @EventHandler
    public void onNpcLeftClick(final @NonNull EntityDamageByEntityEvent event) {
        final @NonNull Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity)) return;
        if (!npcManager.isEntityNpc((LivingEntity) entity)) return;
        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player player)) return;
        handleNpcInteract(player);
    }

    private void handleNpcInteract(final @NonNull Player player) {
        // TODO: Handle interaction
    }
}
