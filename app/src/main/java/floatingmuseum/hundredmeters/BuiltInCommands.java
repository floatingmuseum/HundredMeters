package floatingmuseum.hundredmeters;


import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import floatingmuseum.hundredmeters.utils.NicknameUtil;
import floatingmuseum.hundredmeters.utils.ResUtil;

/**
 * Created by Floatingmuseum on 2017/6/26.
 */

public class BuiltInCommands {

    private static BuiltInCommands builtInCommands;
    private static List<String> commands = new ArrayList<>();
    private static Map<String, String> commandsMeaning = new HashMap<>();

    private static final String COMMAND_HELP = "--help";
    private static final String COMMAND_AROUND = "--around";
    private static final String COMMAND_CONNECT = "--connect";
    private static final String COMMAND_RENAME = "--rename";

    private BuiltInCommands() {
        /**
         * <>中括号内字符为可替换字符
         */
        commands.add(COMMAND_HELP);
        commandsMeaning.put(COMMAND_HELP, ResUtil.getString(R.string.command_help));
        commands.add(COMMAND_AROUND);
        commandsMeaning.put(COMMAND_AROUND, ResUtil.getString(R.string.command_around));
        commands.add(COMMAND_CONNECT);
        commandsMeaning.put(COMMAND_CONNECT, ResUtil.getString(R.string.command_connect));
        commands.add(COMMAND_RENAME);
        commandsMeaning.put(COMMAND_RENAME, ResUtil.getString(R.string.command_rename));
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

    public boolean isBuiltInCommands(String message) {
        String[] newMessage = message.split(" ");
        Logger.d("BuiltInCommands...是否内部命令:" + message + "..." + newMessage[0]);
        boolean isCommand = commands.contains(newMessage[0]);
        if (isCommand) {
            MessageManager.getInstance().sendNewMessage(message);
            executeCommand(newMessage);
        }
        return isCommand;
    }

    private void executeCommand(String[] command) {
        if (COMMAND_HELP.equals(command[0])) {
            listAllCommands();
        } else if (COMMAND_AROUND.equals(command[0])) {
            ConnectionService.sendCommand(ConnectionService.ACTION_LIST_AROUND_USER, null, null);
        } else if (COMMAND_CONNECT.equals(command[0])) {
            ConnectionService.sendCommand(ConnectionService.ACTION_CONNECT_TO, ConnectionService.EXTRA_CONNECT_NICKNAME, command[1]);
        } else if (COMMAND_RENAME.equals(command[0])) {
            if (command[1].length() < 1 || command[1].length() > 10) {
                BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.alert_nickname_length));
            } else if ("Bot-Y".equals(command[1])) {
                BotY.getInstance().sendNewMessage(ResUtil.getString(R.string.alert_nickname_same_as_100m));
            } else {
                NicknameUtil.saveNewName(command[1]);
            }
        }
    }

    private void listAllCommands() {
        String message = ResUtil.getString(R.string.all_inside_commands);
        for (String command : commands) {
            message += "\n          " + command +
                    "\n          " + commandsMeaning.get(command);
        }
        BotY.getInstance().sendNewMessage(message);
    }
}
