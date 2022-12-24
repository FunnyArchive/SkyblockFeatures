package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.CheckRenderEntityEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.features.impl.ItemFeatures.HideGlass;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiscFeatures {
    public static HashMap<Entity,EntityVillager> tracker = new HashMap<Entity,EntityVillager>();

    @SubscribeEvent
    public void onload(WorldEvent.Load event) {
        tracker.clear();
    }
    String[] jerryMessages = new String[]{"Jerry","hrrrmm","huuugh","hrngh","hurngh"};
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if(!skyblockfeatures.config.jerryMode) return;
        String raw = event.message.getFormattedText();
        if(raw.contains(":") && Utils.inSkyblock && !raw.contains("{")) {
            String message = raw.split(":")[1];
            String[] arr = message.split(" ");
            String newMessage = "";
            for(String msg:arr) {
                newMessage+=jerryMessages[new Random().nextInt(jerryMessages.length)]+" ";
            }
            event.message = new ChatComponentText(raw.replace(message, newMessage));
        }
    }

    @SubscribeEvent
    public void onCheckRender(CheckRenderEntityEvent event) {
        if (!Utils.inSkyblock) return;
        try {
            if (event.entity instanceof EntityPlayer && skyblockfeatures.config.jerryMode && event.entity!=Utils.GetMC().thePlayer && !event.entity.isInvisible() && !event.entity.isDead) {
                if(tracker.containsKey(event.entity)) {
                    EntityVillager villager = tracker.get(event.entity);
                    villager.setPosition(event.entity.posX, event.entity.posY, event.entity.posZ);
                    villager.rotationYaw = event.entity.rotationYaw;
                    villager.rotationPitch = event.entity.rotationPitch;
                    villager.setRotationYawHead(event.entity.getRotationYawHead()); 
                
                    event.setCanceled(true);
                } else {
                    EntityVillager villager = new EntityVillager(Utils.GetMC().theWorld);
                    Utils.GetMC().theWorld.addEntityToWorld((int) Math.floor(Math.random()*10000000), villager);
                    tracker.put(event.entity, villager);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    
        if (event.entity instanceof EntityItem) {
            EntityItem entity = (EntityItem) event.entity;
            if (skyblockfeatures.config.hideJerryRune) {
                ItemStack item = entity.getEntityItem();
                if(item.getItem() == Items.spawn_egg && Objects.equals(ItemMonsterPlacer.getEntityName(item), "Villager") && item.getDisplayName().equals("Spawn Villager") && entity.lifespan == 6000) {
                    event.setCanceled(true);
                }
            }
        }

        if (event.entity instanceof EntityLightningBolt) {
            if (skyblockfeatures.config.hideLightning) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (!Utils.inSkyblock) return;
        if (event.type == RenderGameOverlayEvent.ElementType.AIR && skyblockfeatures.config.hideAirDisplay && !Utils.inDungeons) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (Utils.inSkyblock && skyblockfeatures.config.noFire && event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE) {
            event.setCanceled(true);
        }
    }
    List<Vec3> particles = new ArrayList<Vec3>();
    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if(event.packet instanceof S2APacketParticles  && skyblockfeatures.config.highlightMushrooms) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            Vec3 pos = new Vec3(Math.floor(packet.getXCoordinate()),Math.floor(packet.getYCoordinate()),Math.floor(packet.getZCoordinate()));
            boolean dupe = false;
            for(Vec3 part:particles) {
                if(part.distanceTo(pos) < 1 || part==pos) {
                    dupe = true;
                }
            }
            
            if(!dupe && type == EnumParticleTypes.SPELL_MOB && SBInfo.getInstance().location.contains("Glowing")) {
                particles.add(pos);
            }
        }
    }
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if(skyblockfeatures.config.jerryMode && !event.itemStack.getDisplayName().contains("Jerry's") && !HideGlass.isEmptyGlassPane(event.itemStack)) {
            String color = event.itemStack.getDisplayName().substring(0, 2);
            event.itemStack.setStackDisplayName(color+"Jerry's "+event.itemStack.getDisplayName());
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            for(ItemStack stack: Utils.GetMC().thePlayer.getInventory()) {
                if(skyblockfeatures.config.jerryMode && !stack.getDisplayName().contains("Jerry's")) {
                    String color = stack.getDisplayName().substring(0, 2);
                    stack.setStackDisplayName(color+"Jerry's "+stack.getDisplayName());
                }
            }
            for(Entity entity:tracker.keySet()) {
                if(entity==null || entity.isDead) {
                    Utils.GetMC().theWorld.removeEntityFromWorld(tracker.get(entity).getEntityId());
                    tracker.remove(entity);
                    continue;
                }
                if(!Utils.isNPC(entity)) {
                    RenderUtil.draw3DStringWithShadow(entity.getPositionVector().add(new Vec3(0,2.6,0)), "Jerry", 0xFFFFFF, event.partialTicks);
                    RenderUtil.draw3DStringWithShadow(entity.getPositionVector().add(new Vec3(0,2.3,0)), ChatFormatting.YELLOW+""+ChatFormatting.BOLD+"CLICK", 0xFFFFFF, event.partialTicks);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        if(SBInfo.getInstance().location.contains("Glowing") && skyblockfeatures.config.highlightMushrooms) {
            try {
                for(Vec3 packet:particles) {
                    Color color = new Color(0x55FF55);
                    highlightBlock(color, Math.floor(packet.xCoord),Math.floor(packet.yCoord), Math.floor(packet.zCoord), event.partialTicks);

                    Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(packet)).getBlock();
                    if(block != null && block == Blocks.air) {
                        particles.remove(packet);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static void highlightBlock(Color c, double d, double d1, double d2, float ticks) {
        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(d + 1.0D, d1 + 1, d2 + 1.0D, d, d1, d2),c,ticks);
    }
}
