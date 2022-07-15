package mrfast.skyblockfeatures.mixins;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import club.sk1er.mods.core.transformers.RendererLivingEntityTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraftforge.client.event.RenderLivingEvent;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.dungeons.Nametags;
import mrfast.skyblockfeatures.features.impl.glowingstuff.PartyGlow;
import mrfast.skyblockfeatures.utils.ColorCode;
import mrfast.skyblockfeatures.utils.DrawUtils;
import mrfast.skyblockfeatures.utils.ItemRarity;
import mrfast.skyblockfeatures.utils.ItemUtil;
import mrfast.skyblockfeatures.utils.SBInfo;
import mrfast.skyblockfeatures.utils.Utils;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {

    @Shadow private WorldClient theWorld;
    @Final @Shadow private RenderManager renderManager;
    @Final @Shadow private Minecraft mc;
    @Shadow private Framebuffer entityOutlineFramebuffer;
    @Shadow private ShaderGroup entityOutlineShader;

    @Shadow protected abstract boolean isRenderEntityOutlines();

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;isRenderEntityOutlines()Z", ordinal = 0))
    private boolean onRenderEntities(RenderGlobal renderGlobal) {
        return false;
    }

    // Remove condition by always returning true
    @Redirect(method = "isRenderEntityOutlines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSpectator()Z", ordinal = 0))
    private boolean isSpectatorDisableCheck(EntityPlayerSP entityPlayerSP) {
        return true;
    }

    // Instead of key down, check if they are in the lobby
    @Redirect(method = "isRenderEntityOutlines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", ordinal = 0))
    private boolean isKeyDownDisableCheck(KeyBinding keyBinding) {
        return true;
    }

    @Inject(method = "renderEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", shift = At.Shift.BEFORE, ordinal = 2, args = {"ldc=entities"}), locals = LocalCapture.CAPTURE_FAILSOFT) // Optifine version
    private void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci, int pass, double d0, double d1, double d2, Entity entity, double d3, double d4, double d5, List<Entity> list, boolean bool0, boolean bool1) {
        displayOutlines(list, d0, d1, d2, camera, partialTicks);
    }

    @Inject(method = "renderEntities", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", shift = At.Shift.BEFORE, ordinal = 2, args = {"ldc=entities"}), locals = LocalCapture.CAPTURE_FAILSOFT) // Non-optifine version
    private void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci, int pass, double d0, double d1, double d2, Entity entity, double d3, double d4, double d5, List<Entity> list) {
        displayOutlines(list, d0, d1, d2, camera, partialTicks);
    }
    
    private static Framebuffer swapBuffer = null;
    private static Framebuffer initSwapBuffer() {
        Framebuffer main = Minecraft.getMinecraft().getFramebuffer();
        Framebuffer framebuffer = new Framebuffer(main.framebufferTextureWidth, main.framebufferTextureHeight, true);
        framebuffer.setFramebufferFilter(GL11.GL_NEAREST);
        framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        return framebuffer;
    }

    private static void updateFramebufferSize()
    {
        if (swapBuffer == null) {
            swapBuffer = initSwapBuffer();
        }
        int width = Minecraft.getMinecraft().displayWidth;
        int height = Minecraft.getMinecraft().displayHeight;
        if (swapBuffer.framebufferWidth != width || swapBuffer.framebufferHeight != height) {
            swapBuffer.createBindFramebuffer(width, height);
        }
        RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
        Framebuffer outlineBuffer = rg.entityOutlineFramebuffer;
        if (outlineBuffer.framebufferWidth != width || outlineBuffer.framebufferHeight != height) {
            outlineBuffer.createBindFramebuffer(width, height);
            rg.entityOutlineShader.createBindFramebuffers(width, height);
        }
    }

    
    private void displayOutlines(List<Entity> entities, double x, double y, double z, ICamera camera, float partialTicks) {
        if (isRenderEntityOutlines()) // Replaced isRenderEntityOutlines call with the conditions themselves
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderGlobal renderGlobal = mc.renderGlobal;

            mc.theWorld.theProfiler.endStartSection("entityOutlines");
            updateFramebufferSize();
            // Clear and bind the outline framebuffer
            renderGlobal.entityOutlineFramebuffer.framebufferClear();
            renderGlobal.entityOutlineFramebuffer.bindFramebuffer(false);

            // Vanilla options
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableFog();
            mc.getRenderManager().setRenderOutlines(true);
            
            DrawUtils.enableOutlineMode();
            GlStateManager.depthFunc(GL11.GL_ALWAYS);
            try {
                for (Entity entity : entities) {
                    boolean flag = (mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping());
                    boolean flag1 = (entity.isInRangeToRender3d(x, y, z) && (entity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) && entity instanceof EntityPlayer && !Utils.isNPC(entity));
                    // Dungeon Player Glowing
                    if ((entity != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0 || flag) && flag1 && Nametags.players.containsKey(entity) && skyblockfeatures.config.glowingPlayers && Utils.inDungeons) {
                        outlineColor(entity, (String)Nametags.players.get(entity));
                        renderManager.renderEntitySimple(entity, partialTicks);
                    }
                    // General Player Glowing
                    if ((entity != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0 || flag) && flag1 && skyblockfeatures.config.playeresp && mc.thePlayer.canEntityBeSeen(entity)) {
                        if(skyblockfeatures.config.HidePlayersNearNPC) {
                            if(!Nametags.isNearNPC(entity) && !Utils.isNPC(entity)) {
                                renderManager.renderEntitySimple(entity, partialTicks);
                            }
                        } else {
                            renderManager.renderEntitySimple(entity, partialTicks);
                        }
                    }
                    // Item Glowing
                    boolean flag2 = (mc.thePlayer.getDistanceToEntity(entity) < 15.0F && entity instanceof EntityItem);
                    if (flag2 && skyblockfeatures.config.glowingItems) {
                        ItemRarity itemRarity = ItemUtil.getRarity(((EntityItem)entity).getEntityItem(), "");
                        outlineColor(itemRarity.getColorCode().getColor());
                        renderManager.renderEntitySimple(entity, partialTicks);
                    } 
                    if(entity instanceof EntityEnderman && mc.thePlayer.canEntityBeSeen(entity) && SBInfo.getInstance().location.contains("Dragons Nest") && skyblockfeatures.config.glowingZealots) {
                        renderManager.renderEntitySimple(entity, partialTicks);
                    }
                    // Make party members glow blue
                    for (String name : PartyGlow.party) {
                        if (entity.getName().contains(name) && !Utils.inDungeons && skyblockfeatures.config.glowingParty && mc.thePlayer.canEntityBeSeen(entity)) {
                            outlineColor(entity, "§1");
                            this.renderManager.renderEntitySimple(entity, partialTicks);
                        }
                    }
                }
            } catch (NullPointerException e) {
                
            }
            GlStateManager.depthFunc(GL11.GL_LEQUAL);

            DrawUtils.disableOutlineMode();

            // Vanilla options
            RenderHelper.enableStandardItemLighting();
            mc.getRenderManager().setRenderOutlines(false);

            // Load the outline shader
            GlStateManager.depthMask(false);
            renderGlobal.entityOutlineShader.loadShaderGroup(partialTicks);
            GlStateManager.depthMask(true);

            // Reset GL/framebuffers for next render layers
            GlStateManager.enableLighting();
            mc.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableFog();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
        }
    }

    private final FloatBuffer BUF_FLOAT_4 = BufferUtils.createFloatBuffer(4);

    private void outlineColor(int color) {
        BUF_FLOAT_4.put(0, (float)(color >> 16 & 255) / 255.0F);
        BUF_FLOAT_4.put(1, (float)(color >> 8 & 255) / 255.0F);
        BUF_FLOAT_4.put(2, (float)(color & 255) / 255.0F);
        BUF_FLOAT_4.put(3, 1);

        GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, BUF_FLOAT_4);
    }

    private void outlineColor(Entity entity, String string) {
        if(!(entity instanceof EntityPlayer)) return;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) ((EntityPlayer) entity).getTeam();
        if (scoreplayerteam != null && scoreplayerteam.getNameTagVisibility() != EnumVisible.NEVER) {
            scoreplayerteam.setNamePrefix(string);
        }
    }

}