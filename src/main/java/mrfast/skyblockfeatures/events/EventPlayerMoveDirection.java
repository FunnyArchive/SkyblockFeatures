package mrfast.skyblockfeatures.events;

import net.minecraftforge.fml.common.eventhandler.Event;


public class EventPlayerMoveDirection extends Event {

    private float strafe, forward;
    private Type type;

    public static enum Type {
        PRE(false),
        POST(true),
        RENDER2D(false),
        RENDER3D(false);
    	
    	boolean reverse;
    	
    	Type(boolean reverse) {
    		this.reverse = reverse;
    	}
    }

    public void type(Type type) {
        this.type = type;
    }

    public EventPlayerMoveDirection(Type type, float strafe, float forward) {
        this.strafe = strafe;
        this.forward = forward;
    }

    public float strafe() {
        return strafe;
    }

    public void strafe(float strafe) {
        this.strafe = strafe;
    }

    public float forward() {
        return forward;
    }

    public void forward(float forward) {
        this.forward = forward;
    }

    public Type type() {
        return type;
    }

}