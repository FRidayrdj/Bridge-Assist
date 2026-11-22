package com.example.bridgeassist;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

@Mod(modid = BridgeAssistMod.MODID, version = "1.0", acceptedMinecraftVersions = "[1.8.9]")
public class BridgeAssistMod {
    public static final String MODID = "bridgeassist";

    private final Minecraft mc = Minecraft.getMinecraft();
    private KeyBinding toggleKey;
    private boolean enabled = false;
    private int placeCooldown = 0;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        toggleKey = new KeyBinding("Toggle Bridge Assist", Keyboard.KEY_K, "BridgeAssist");
        ClientRegistry.registerKeyBinding(toggleKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent e) {
        if (toggleKey.isPressed()) {
            enabled = !enabled;
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText("[BridgeAssist] " + (enabled ? "ON" : "OFF")));
            }
        }
    }

    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        EntityPlayerSP p = mc.thePlayer;
        if (!enabled || p == null || mc.theWorld == null || mc.currentScreen != null) return;

        int sneakCode = mc.gameSettings.keyBindSneak.getKeyCode();
        boolean physicalSneak = Keyboard.isKeyDown(sneakCode);

        
        double aheadX = p.posX + p.motionX * 3.0;
        double aheadZ = p.posZ + p.motionZ * 3.0;
        BlockPos below = new BlockPos(aheadX, p.posY - 1.0, aheadZ);
        boolean airBelow = mc.theWorld.getBlockState(below).getBlock().getMaterial() == Material.air;

        boolean atEdge = p.onGround && airBelow;
        KeyBinding.setKeyBindState(sneakCode, physicalSneak || atEdge);

        if (placeCooldown > 0) placeCooldown--;
        if (atEdge && placeCooldown == 0
                && p.getHeldItem() != null && p.getHeldItem().getItem() instanceof ItemBlock
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            placeCooldown = 2;
        }
    }
}
