package mrfast.skyblockfeatures.commands;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import mrfast.skyblockfeatures.features.impl.dungeons.solvers.LividFinder;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class FakePlayerCommand extends CommandBase {

	@Override
    public String getCommandName() {
        return "fakeplayer";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/fakeplayer [playerName]";
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

    EntityOtherPlayerMP fakePlayer = null;
    static Minecraft mc = Utils.GetMC();
	@Override
	public void processCommand(ICommandSender arg0, String[] args) throws CommandException {
        if(fakePlayer != null) {
            mc.theWorld.removeEntity(fakePlayer);;
        }
        UUID uuid = UUID.fromString(getUUID(args[0]));
		fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(uuid, args[0]));
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;

        mc.theWorld.addEntityToWorld(-1, fakePlayer);
        LividFinder.livid = fakePlayer;
	}

    public static String getUUID(String name) {
        JsonParser parser = new JsonParser();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if (UUIDJson.isEmpty()) {
                return "invalid name";
            }
            JsonObject UUIDObject = (JsonObject)parser.parse(UUIDJson);
            return reformatUuid(UUIDObject.get("id").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return mc.thePlayer.getUniqueID().toString();
        }
    }
    
    private static String reformatUuid(String uuid) {
        String longUuid = "";
        longUuid = longUuid + uuid.substring(1, 9) + "-";
        longUuid = longUuid + uuid.substring(9, 13) + "-";
        longUuid = longUuid + uuid.substring(13, 17) + "-";
        longUuid = longUuid + uuid.substring(17, 21) + "-";
        longUuid += uuid.substring(21, 33);
        return longUuid;
    }
}
