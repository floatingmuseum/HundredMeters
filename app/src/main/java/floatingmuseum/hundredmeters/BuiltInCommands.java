package floatingmuseum.hundredmeters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/6/26.
 */

public class BuiltInCommands {

    private static BuiltInCommands builtInCommands;

    private static List<String> commands = new ArrayList<>();

    private BuiltInCommands() {
    }

    public static BuiltInCommands getInstance() {
        if (builtInCommands == null) {
            synchronized (BuiltInCommands.class) {
                if (builtInCommands == null) {
                    builtInCommands = new BuiltInCommands();
                }
            }
        }
        return builtInCommands;
    }

    public static boolean isBuiltInCommands(String message) {
        return commands.contains(message);
    }

    public static void excuteCommands(String commands){

    }
}
