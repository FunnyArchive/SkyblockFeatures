package mrfast.skyblockfeatures.commands;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.features.impl.misc.MiscFeatures;
import mrfast.skyblockfeatures.utils.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;

public class jerryCommand extends CommandBase {

	@Override
    public String getCommandName() {
        return "jerry";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/jerry";
    }

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	@Override
	public void processCommand(ICommandSender arg0, String[] args) throws CommandException {
        skyblockfeatures.config.jerryMode=!skyblockfeatures.config.jerryMode;
        if(!skyblockfeatures.config.jerryMode) {
            for(Entity entity:MiscFeatures.tracker.keySet()) {
                Utils.GetMC().theWorld.removeEntityFromWorld(MiscFeatures.tracker.get(entity).getEntityId());
            }
            MiscFeatures.tracker.clear();
        }
	}
}
