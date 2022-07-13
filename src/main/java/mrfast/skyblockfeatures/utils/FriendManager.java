// package mrfast.skyblockfeatures.utils;

// import java.io.File;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.Writer;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.internal.LinkedTreeMap;
// import com.google.gson.reflect.TypeToken;
// import com.mojang.realmsclient.gui.ChatFormatting;

// import org.lwjgl.input.Mouse;
// import org.lwjgl.opengl.GL11;

// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.GlStateManager;
// import net.minecraft.entity.Entity;
// import net.minecraft.entity.player.EntityPlayer;
// import net.minecraft.util.MovingObjectPosition;
// import net.minecraftforge.client.event.RenderPlayerEvent;
// import net.minecraftforge.client.event.RenderWorldLastEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import net.minecraftforge.fml.common.gameevent.TickEvent;
// import mrfast.skyblockfeatures.skyblockfeatures;


// public class FriendManager
// {
    
//     public void Load()
//     {
//         LoadFriends();
//     }

//     public static FriendManager Get()
//     {
//         return skyblockfeatures.GetFriendManager();
//     }

//     File l_Exists = new File(skyblockfeatures.modDir, "friends.json");
    
//     /// Loads the friends from the JSON
//     public void LoadFriends()
//     {

//         try 
//         {
//             // create Gson instance
//             Gson gson = new Gson();

//             // create a reader
//             FileReader reader = new FileReader(l_Exists);

//             // convert JSON file to map
//             FriendList = gson.fromJson(reader, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType());

//             // close reader
//             reader.close();

//         }
//         catch (Exception ex)
//         {
//             ex.printStackTrace();
//         }
//     }
    
//     public void SaveFriends()
//     {
//         GsonBuilder builder = new GsonBuilder();
        
//         Gson gson = builder.setPrettyPrinting().create();

//         FileWriter writer;
//         try
//         {
//             writer = new FileWriter(l_Exists);
        
//             gson.toJson(FriendList, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType(), writer);
//             writer.close();
//         }
//         catch (IOException e)
//         {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }
    
//     private LinkedTreeMap<String, Friend> FriendList = new LinkedTreeMap<>();
    
//     public boolean IsFriend(Entity p_Entity)
//     {
//         return p_Entity instanceof EntityPlayer && FriendList.containsKey(p_Entity.getName().toLowerCase());
//     }

//     public boolean AddFriend(String p_Name)
//     {
//         if (FriendList.containsKey(p_Name))
//             return false;
        
//         Friend l_Friend = new Friend(p_Name);
        
//         FriendList.put(p_Name, l_Friend);
//         SaveFriends();
//         return true;
//     }

//     public boolean RemoveFriend(String p_Name)
//     {
//         if (!FriendList.containsKey(p_Name))
//             return false;

//         FriendList.remove(p_Name);
//         SaveFriends();
//         return true;
//     }

//     public final LinkedTreeMap<String, Friend> GetFriends()
//     {
//         return FriendList;
//     }

//     public boolean IsFriend(String p_Name)
//     {
//         if (!skyblockfeatures.config.friends)
//             return false;
        
//         return FriendList.containsKey(p_Name.toLowerCase());
//     }

//     public Friend GetFriend(Entity e)
//     {
//         if (!skyblockfeatures.config.friends)
//             return null;
        
//         if (!FriendList.containsKey(e.getName().toLowerCase()))
//             return null;
        
//         return FriendList.get(e.getName().toLowerCase());
//     }


//     public static ChatFormatting red = ChatFormatting.RED;
//     public static ChatFormatting green = ChatFormatting.GREEN;
//     public static ChatFormatting bold = ChatFormatting.BOLD;
//     public static ChatFormatting reset = ChatFormatting.RESET;


//     private boolean clicked = false;

//     Minecraft mc = Minecraft.getMinecraft();
//     Entity player = null;

//     @SubscribeEvent
// 	public void onTick(TickEvent.ClientTickEvent event) {
// 		if (mc.thePlayer == null) {
// 			return;
// 		}

//         System.out.println(FriendList);


//         if (mc.currentScreen != null) {
//             return;
//         }

//         if (!Mouse.isButtonDown(2)) {
//             clicked = false;
//             return;
//         }

//         if (!clicked) {

//             clicked = true;

//             final MovingObjectPosition result = mc.objectMouseOver;

//             if (result == null || result.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
//                 return;
//             }

//             if (!(result.entityHit instanceof EntityPlayer)) return;

//             player = result.entityHit;

//             if(player == null) return;
            
//             if (FriendManager.Get().IsFriend(player.getName())) {
//                 FriendManager.Get().RemoveFriend(player.getName());
//                 Utils.SendMessage("Player " + red + bold + player.getName() + reset + " is no longer your friend :(");
//             } else {
//                 FriendManager.Get().AddFriend(player.getName());
//                 Utils.SendMessage("Player " + green + bold + player.getName() + reset + " is now your friend :D");
//             }
//         }
//     }


//     @SubscribeEvent
//     public void preRender(RenderPlayerEvent.Pre event) {
//         if (skyblockfeatures.config.friends && FriendManager.Get().IsFriend(event.entityPlayer.getName())) {
//             GL11.glEnable((int)32823);
//             GlStateManager.enablePolygonOffset();
//             GlStateManager.doPolygonOffset((float)1.0f, (float)-1000000.0f);
//         }
//     }

//     @SubscribeEvent
//     public void postRender(RenderPlayerEvent.Post event) {
//         if (skyblockfeatures.config.friends) {
//             GL11.glDisable((int)32823);
//             GlStateManager.doPolygonOffset((float)1.0f, (float)1000000.0f);
//             GlStateManager.disablePolygonOffset();
//         }
//     }

//     @SubscribeEvent
//     public void onRender3D(RenderWorldLastEvent event) {
//         for(EntityPlayer player : Utils.GetMC().theWorld.playerEntities) {
//             if(FriendManager.Get().IsFriend(player.getName())) {
//                 double x = interpolate(player.lastTickPosX, player.posX, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosX;
//                 double y = interpolate(player.lastTickPosY, player.posY, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosY;
//                 double z = interpolate(player.lastTickPosZ, player.posZ, event.partialTicks) - Utils.GetMC().getRenderManager().viewerPosZ;
//                 renderNameTag(player, x, y, z, event.partialTicks);
//             }
//         }
//     }
//     private double interpolate(double previous, double current, float delta) {
//         return (previous + (current - previous) * delta);
//     }

//     private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
//         if(!player.equals(Utils.GetMC().thePlayer)) {

//         ////////////////////////////////////////////////
//         // NAME TAGS
//         ////////////////////////////////////////////////

//         float f = 1.6F;
// 		float f1 = 0.016666668F * f;

//         Entity renderViewEntity = mc.getRenderViewEntity();

//         double distanceScale = Math.max(1, renderViewEntity.getPositionVector().distanceTo(player.getPositionVector()) / 10F);

//         Minecraft mc = Minecraft.getMinecraft();
//         int iconSize = 25;

//         // eee

//         if (player.isSneaking()) {
//             y -= 0.65F;
//         }

//         y += player.height / 2F + 0.25F;

//         GlStateManager.pushMatrix();
//         GlStateManager.translate(x, y, z);
//         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
//         GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
//         GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
//         GlStateManager.scale(-f1, -f1, f1);

//         GlStateManager.scale(distanceScale, distanceScale, distanceScale);

//         GlStateManager.disableLighting();
//         GlStateManager.depthMask(false);
//         GlStateManager.disableDepth();
//         GlStateManager.enableBlend();
//         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//         GlStateManager.enableTexture2D();
//         GlStateManager.color(1, 1, 1, 1);
//         GlStateManager.enableAlpha();

//         // Utils.GetMC().fontRendererObj.drawStringWithShadow(a, -width, -(Utils.GetMC().fontRendererObj.FONT_HEIGHT - 1), 0x7FFF00);
//         mc.fontRendererObj.drawString(player.getName(), -mc.fontRendererObj.getStringWidth(player.getName()) / 2F, iconSize / 2F + 13, -1, true);

//         GlStateManager.enableDepth();
//         GlStateManager.depthMask(true);
//         GlStateManager.enableLighting();
//         GlStateManager.disableBlend();
//         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//         GlStateManager.popMatrix();
//         }
//     }
// }
