package mrfast.skyblockfeatures.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventRender3D extends Event {

    public static float partialTicks;

    public EventRender3D(float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
