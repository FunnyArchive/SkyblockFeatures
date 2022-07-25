package mrfast.skyblockfeatures.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import com.google.common.eventbus.Subscribe;

import mrfast.skyblockfeatures.events.SecondPassedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapeUtils {
    static ArrayList<String> final_name_list = get_names();

    @SubscribeEvent
    public void worldLoadEvent(WorldEvent.Load event) {
        final_name_list = get_names();
    }

    public static ArrayList<String> get_names() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/MrFast-js/skyblockFeatures-Capes/main/capes.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final ArrayList<String> name_list = new ArrayList<>();

            String s;

            while ((s = reader.readLine()) != null) {
                name_list.add(s);
            }

            return name_list;
        } catch (Exception ignored){
            return null;
        }
    }

    public static boolean is_name_valid(String name) {
        for (String u : Objects.requireNonNull(final_name_list)) {
            if (u.contains(name.toString())) {
                return true;
            }
        }
        return false;
    }

}
