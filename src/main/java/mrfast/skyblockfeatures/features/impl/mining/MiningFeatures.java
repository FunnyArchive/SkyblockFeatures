package mrfast.skyblockfeatures.features.impl.mining;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.DataFetcher;
import mrfast.skyblockfeatures.events.BlockChangeEvent;
import mrfast.skyblockfeatures.events.PacketEvent;
import mrfast.skyblockfeatures.features.impl.misc.MiscFeatures;
import mrfast.skyblockfeatures.utils.RenderUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.ScoreboardUtil;
import mrfast.skyblockfeatures.utils.StringUtils;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiningFeatures {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static BlockPos puzzlerSolution = null;

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (!Utils.inSkyblock || event.type == 2) return;

        String unformatted = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if(skyblockfeatures.config.treasureChestSolver && unformatted.contains("uncovered a treasure chest!")) {
            treasureChest = null;
            particles.clear();
            progress = 0;
        }
        if (skyblockfeatures.config.puzzlerSolver && unformatted.startsWith("[NPC] Puzzler:")) {
            if (unformatted.contains("Nice")) {
                puzzlerSolution = null;
                return;
            }
            if (unformatted.contains("Wrong") || unformatted.contains("Come") || (!unformatted.contains("▶") && !unformatted.contains("▲") && !unformatted.contains("◀") && !unformatted.contains("▼"))) return;
            if (ScoreboardUtil.getSidebarLines().stream().anyMatch(line -> ScoreboardUtil.cleanSB(line).contains("Dwarven Mines"))) {
                puzzlerSolution = new BlockPos(181, 195, 135);
                Matcher matcher = Pattern.compile("([▶▲◀▼]+)").matcher(unformatted);
                if (matcher.find()) {
                    String sequence = matcher.group(1).trim();
                    for (char c : sequence.toCharArray()) {
                        switch (String.valueOf(c)) {
                            case "▲":
                                puzzlerSolution = puzzlerSolution.south();
                                break;
                            case "▶":
                                puzzlerSolution = puzzlerSolution.west();
                                break;
                            case "◀":
                                puzzlerSolution = puzzlerSolution.east();
                                break;
                            case "▼":
                                puzzlerSolution = puzzlerSolution.north();
                                break;
                            default:
                                System.out.println("Invalid Puzzler character: " + c);
                        }
                    }
                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Mine the block highlighted in " + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "RED" + EnumChatFormatting.GREEN + "!"));
                }
            }
        }
    }
    @SubscribeEvent
    public void onPlayerInteractEvent(BlockChangeEvent event) {
        if(Utils.inSkyblock && skyblockfeatures.config.highlightEnderNodes) {
            BlockPos p1 = Utils.GetMC().thePlayer.getPosition();
            BlockPos p2 = event.pos;
            if(p1.distanceSq(p2.getX(), p2.getY(), p2.getZ())<25) enderParticles.clear();
        }
    }
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Utils.inSkyblock) return;
        if (skyblockfeatures.config.puzzlerSolver && puzzlerSolution != null) {
            double x = puzzlerSolution.getX() ;
            double y = puzzlerSolution.getY() ;
            double z = puzzlerSolution.getZ() ;
            RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x, y, z, x + 1, y + 1.01, z + 1), new Color(255, 0, 0, 200), event.partialTicks);
        }
        if(skyblockfeatures.config.highlightEnderNodes && skyblockfeatures.locationString.contains("The End")) {
            try {
                GlStateManager.disableDepth();
                List<Vec3> drawnPositions = new ArrayList<Vec3>();

                for(Vec3 packet:enderParticles) {
                    boolean dupe =false;
                    double x = packet.xCoord;
                    double y = packet.yCoord;
                    double z = packet.zCoord;
                    
                    if(!drawnPositions.contains(packet)) {
                        for(Vec3 packet2:drawnPositions) {
                            if(packet.distanceTo(packet2)<1.5) {
                                dupe=true;
                            }
                        }
                        if(dupe) continue;
                    }

                    if((x-Math.floor(x))==0.25) {
                        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x-0.25, y-0.5, z-0.5, Math.floor(x)-1, Math.floor(y)+1, Math.floor(z)+1), Color.magenta, event.partialTicks);
                        drawnPositions.add(packet);
                    }
                    if((y-Math.floor(y))==0.25) {
                        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x-0.5, y-0.25, z-0.5, Math.floor(x)+1, Math.floor(y)-1, Math.floor(z)+1), Color.magenta, event.partialTicks);
                        drawnPositions.add(packet);
                    }
                    if((z-Math.floor(z))==0.25) {
                        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x-0.5, y-0.5, z-0.25, Math.floor(x)+1, Math.floor(y)+1, Math.floor(z)-1), Color.magenta, event.partialTicks);
                        drawnPositions.add(packet);
                    }
                    if((x-Math.floor(x))==0.75) {
                        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x+1.25, y-0.5, z-0.5, Math.floor(x)+1, Math.floor(y)+1, Math.floor(z)+1), Color.magenta, event.partialTicks);
                        drawnPositions.add(packet);
                    }
                    if((z-Math.floor(z))==0.75) {
                        RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(x-0.5, y-0.5, z+1.25, Math.floor(x)+1, Math.floor(y)+1, Math.floor(z)+1), Color.magenta, event.partialTicks);
                        drawnPositions.add(packet);
                    }
                }
                GlStateManager.enableDepth();
            } catch (Exception e) {}
        }
        
        try {
            if(!skyblockfeatures.config.treasureChestSolver || !SBInfo.getInstance().getLocation().equals("crystal_hollows")) return;
            Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(treasureChest)).getBlock();
            if(treasureChest != null) {
                Vec3 stringPos = new Vec3(treasureChest.getX()+0.5, treasureChest.getY()+1.25, treasureChest.getZ()+0.5);
                RenderUtil.draw3DString(stringPos, ChatFormatting.AQUA+""+progress+" / 5", 0xFFFFFF, event.partialTicks);
                RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(treasureChest, treasureChest.add(1, 1, 1)), Color.green, event.partialTicks);
            }
            for(Vec3 packet:particles) {
                RenderUtil.drawOutlinedFilledBoundingBox(new AxisAlignedBB(packet.xCoord-0.05, packet.yCoord-0.05, packet.zCoord-0.05, packet.xCoord+0.1, packet.yCoord+0.1, packet.zCoord+0.1), Color.red, event.partialTicks);
                if(block != null && block == Blocks.air) {
                    particles.remove(packet);
                }
            }
            if(block != null && block == Blocks.air) {
                treasureChest = null;
                progress = 0;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        try {
            puzzlerSolution = null;
            treasureChest = null;
            particles.clear();
            enderParticles.clear();
            progress = 0;
        } catch(Exception e) {

        }
    }
    BlockPos treasureChest = null;
    List<Vec3> particles = new ArrayList<Vec3>();
    List<Vec3> enderParticles = new ArrayList<Vec3>();
    int progress = 0;

    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent event) {
        if(event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            Vec3 pos = new Vec3(packet.getXCoordinate(),packet.getYCoordinate(),packet.getZCoordinate());
            if(type == EnumParticleTypes.PORTAL && !enderParticles.contains(pos)) {
                enderParticles.add(pos);
            }
        }
        if(event.packet instanceof S2APacketParticles && skyblockfeatures.config.treasureChestSolver) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            EnumParticleTypes type = packet.getParticleType();
            Vec3 pos = new Vec3(packet.getXCoordinate(),packet.getYCoordinate(),packet.getZCoordinate());
            boolean dupe = false;
            for(Vec3 part:particles) {
                if(pos.distanceTo(part)<0.1) {
                    dupe = true;
                }
                if(part.distanceTo(pos) > 0.0) {
                    particles.clear();
                    break;
                }
            }
            for(Vec3 particle:particles) {
                if(pos.distanceTo(particle)<0.1) {
                    
                }
            }
            
            if(!dupe && type == EnumParticleTypes.CRIT && !particles.contains(pos) && mc.thePlayer.getDistance(pos.xCoord, pos.yCoord, pos.zCoord)<5) {
                if(treasureChest == null) {
                    particles.add(pos);
                    for(TileEntity entity: mc.theWorld.loadedTileEntityList) {
                        if(entity.getPos().distanceSq(pos.xCoord, pos.yCoord, pos.zCoord) < 2 && entity instanceof TileEntityChest) {
                            treasureChest = entity.getPos();
                        }
                    }
                } else {
                    if(treasureChest.distanceSq(pos.xCoord, pos.yCoord, pos.zCoord) < 2) particles.add(pos);
                }
            }
        }
        if(event.packet instanceof S29PacketSoundEffect && skyblockfeatures.config.treasureChestSolver) {
            S29PacketSoundEffect packet = (S29PacketSoundEffect) event.packet;
            // For some reason "random.orb" isnt equal to "random.orb"
            if(packet.getSoundName().contains("orb")) {
                if(packet.getVolume() == 1 && packet.getPitch() == 1) {
                    progress++;
                }
            }
            if(packet.getSoundName().contains("villager") && packet.getSoundName().contains("no")) {
                progress=0;
            }
        }
    }
}
