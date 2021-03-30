package de.mari_023.fabric.ae2wtlib.mixin;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import de.mari_023.fabric.ae2wtlib.terminal.WirelessCraftAmountContainer;
import de.mari_023.fabric.ae2wtlib.wct.WCTContainer;
import de.mari_023.fabric.ae2wtlib.wpt.WPTContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryActionPacket.class)
public class InvActionPacket {

    @Shadow
    @Final
    private InventoryAction action;

    @Inject(method = "serverPacketData", at = @At(value = "TAIL"), require = 1, allow = 1, remap = false)
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(action == InventoryAction.AUTO_CRAFT) {
            final ServerPlayerEntity sender = (ServerPlayerEntity) player;
            if(sender.currentScreenHandler instanceof WCTContainer || sender.currentScreenHandler instanceof WPTContainer) {
                final AEBaseContainer baseContainer = (AEBaseContainer) sender.currentScreenHandler;
                final ContainerLocator locator = baseContainer.getLocator();
                if(locator != null) {
                    WirelessCraftAmountContainer.open(player, locator);

                    if(sender.currentScreenHandler instanceof WirelessCraftAmountContainer) {
                        final WirelessCraftAmountContainer cca = (WirelessCraftAmountContainer) sender.currentScreenHandler;

                        if(baseContainer.getTargetStack() != null) {
                            cca.getCraftingItem().setStack(baseContainer.getTargetStack().asItemStackRepresentation());
                            // This is the *actual* item that matters, not the display item above
                            cca.setItemToCraft(baseContainer.getTargetStack());
                        }
                        cca.sendContentUpdates();
                    }
                }
            }
        }
    }
}