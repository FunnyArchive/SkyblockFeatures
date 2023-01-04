package mrfast.skyblockfeatures.features.impl.misc;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.events.CheckRenderEntityEvent;
import mrfast.skyblockfeatures.events.GuiContainerEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.utils.APIUtil;
import mrfast.skyblockfeatures.utils.NumberUtil;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class MiscFeatures {
    public static HashMap<Entity,EntityVillager> tracker = new HashMap<Entity,EntityVillager>();

    @SubscribeEvent
    public void onWorldChanges(WorldEvent.Load event) {
        try {
            tracker.clear();
        } catch(Exception e) {

        }
    }
    // Jerry Talk
    // String[] jerryMessages = new String[]{"Jerry","hrrrmm","huuugh","hrngh","hurngh"};
    // @SubscribeEvent
    // public void onChat(ClientChatReceivedEvent event) {
    //     if(!skyblockfeatures.config.jerryMode) return;
    //     String raw = event.message.getFormattedText();
    //     if(raw.contains(":") && Utils.inSkyblock && !raw.contains("{\"server")) {
    //         String message = raw.replace(raw.split(":")[0],"");
    //         String[] arr = message.split(" ");
    //         String newMessage = ": ";
    //         for(String msg:arr) {
    //             newMessage+=jerryMessages[new Random().nextInt(jerryMessages.length)]+" ";
    //         }
    //         event.message = new ChatComponentText(raw.replace(message, newMessage));
    //     }
    // }

    @SubscribeEvent
    public void onCheckRender(CheckRenderEntityEvent event) {
        if (!Utils.inSkyblock) return;
    
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
    HashMap<EntityTNTPrimed,Double> tntExistTimes = new HashMap<>();
    int tick = 0;
    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if(Utils.GetMC().theWorld==null) return;
        tick++;
        for(Entity entity:Utils.GetMC().theWorld.loadedEntityList) {
            if(entity instanceof EntityTNTPrimed && !tntExistTimes.containsKey(entity)) {
                tntExistTimes.put((EntityTNTPrimed) entity, 4.7d);
            }
        }
        if(tick==4) {
            tick = 0;
            for(EntityTNTPrimed tnt:tntExistTimes.keySet()) {
                if(tntExistTimes.get(tnt)==0.1) continue;
                tntExistTimes.put(tnt,Math.floor((tntExistTimes.get(tnt)-0.1)*10)/10);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for(EntityTNTPrimed tnt:tntExistTimes.keySet()) {
            if(tnt.isDead) {
                tntExistTimes.remove(tnt);
                return;
            }
            if(tntExistTimes.get(tnt)==0) {
                return;
            }
            RenderUtil.draw3DStringWithShadow(tnt.getPositionVector().addVector(0, 1.5, 0), ChatFormatting.GREEN+""+tntExistTimes.get(tnt).toString(), 0xFFFFFF, event.partialTicks);
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
    boolean gotNetworth = false;
    boolean tryingNetworth = false;
    Double networth = 0d;
    Double senitherWeight = 0d;
    Double averageSkill = 0d;
    String discord = "";
    boolean apiOff = false;
    @SubscribeEvent
    public void onGuiClose(GuiContainerEvent.CloseWindowEvent event) {
        gotNetworth = false;
        tryingNetworth = false;
        networth = 0d;
        senitherWeight = 0d;
        averageSkill = 0d;
        discord = "";
        apiOff = false;
    }

    @SubscribeEvent
    public void onDrawContainerTitle(GuiContainerEvent.TitleDrawnEvent.Post event) {
        if (event.gui !=null && event.gui instanceof GuiChest && skyblockfeatures.config.extraProfileInfo) {
            GuiChest gui = (GuiChest) event.gui;
            ContainerChest chest = (ContainerChest) gui.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            String chestName = inv.getDisplayName().getUnformattedText().trim();
            List<String> lines = new ArrayList<>();
            if(!chestName.contains("Profile") || chestName.contains("Management")) return;

            if(!gotNetworth && !tryingNetworth) {
                tryingNetworth = true;
                new Thread(() -> {
                    String key = skyblockfeatures.config.apiKey;
                    if (key.equals("")) return;
                    
                    // Get UUID for Hypixel API requests
                    String username = chestName.substring(0, chestName.indexOf("'"));
                    String uuid = APIUtil.getUUID(username);
                    // Find stats of latest profile
                    String latestProfile = APIUtil.getLatestProfileID(uuid, key);
                    if (latestProfile == null) {
                        apiOff = true;
                        return;
                    };
                    
                    String profileURL = "https://sky.shiiyu.moe/api/v2/profile/"+username;
                    JsonObject profileResponse = APIUtil.getJSONResponse(profileURL);
                    try {
                        profileResponse = profileResponse.get("profiles").getAsJsonObject();
                        networth = profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("networth").getAsJsonObject().get("networth").getAsDouble();
                        senitherWeight = profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("weight").getAsJsonObject().get("senither").getAsJsonObject().get("overall").getAsDouble();
                        averageSkill = Math.floor(profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("average_level").getAsDouble());
                        JsonObject social = profileResponse.get(latestProfile).getAsJsonObject().get("data").getAsJsonObject().get("social").getAsJsonObject();
                        if(social.has("DISCORD")) discord=social.get("DISCORD").getAsString();
                        else discord="None";
                        gotNetworth = true;
                    } catch (Exception e) {
                        apiOff = true;
                    }
                }).start();
            }

            if(gotNetworth) {
                lines.add(ChatFormatting.WHITE+"Networth: "+ChatFormatting.GOLD+NumberUtil.nf.format(Math.floor(networth)));
                lines.add(ChatFormatting.WHITE+"Skill Avg: "+ChatFormatting.GOLD+averageSkill);
                lines.add(ChatFormatting.WHITE+"Senither Weight: "+ChatFormatting.GOLD+NumberUtil.nf.format(senitherWeight));
                lines.add(ChatFormatting.WHITE+"Discord: "+ChatFormatting.BLUE+discord);
            } else if(!apiOff) {
                lines.add(ChatFormatting.WHITE+"Networth: "+ChatFormatting.RED+"Loading.");
                lines.add(ChatFormatting.WHITE+"Skill Avg: "+ChatFormatting.RED+"Loading..");
                lines.add(ChatFormatting.WHITE+"Senither Weight: "+ChatFormatting.RED+"Loading..");
                lines.add(ChatFormatting.WHITE+"Discord: "+ChatFormatting.RED+"Loading...");
            } else if(skyblockfeatures.config.apiKey.equals("")){
                lines.add(ChatFormatting.RED+"API key not set.");
            } else {
                lines.add(ChatFormatting.RED+"Player has API Disabled.");
            }
            Utils.drawGraySquareWithBorder(180, 0, 150, (int) ((3+lines.size())*Utils.GetMC().fontRendererObj.FONT_HEIGHT)-8,3);

            for(int i=0;i<lines.size();i++) {
                Utils.GetMC().fontRendererObj.drawStringWithShadow(lines.get(i), 190, i*(Utils.GetMC().fontRendererObj.FONT_HEIGHT+1)+10, -1);
            }
        }
    }
}
