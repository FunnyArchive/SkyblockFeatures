package mrfast.skyblockfeatures.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiEditSign.class)
public interface AccessorGuiEditSign {
    /** Reference to the sign object. */
    @Accessor("tileSign")
    TileEntitySign getTileSign();
    /** Counts the number of screen updates. */
    @Accessor("updateCounter")
    int getUpdateCounter();
    /** The index of the line that is being edited. */
    @Accessor("editLine")
    int getEditLine();
    /** "Done" button for the GUI. */
    @Accessor("doneBtn")
    GuiButton getDoneBtn();
}
