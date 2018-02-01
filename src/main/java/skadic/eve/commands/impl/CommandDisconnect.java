package skadic.eve.commands.impl;

import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.eve.Eve;

import static skadic.commands.util.Utils.sendMessage;

@Help(description = "Disconnects the bot from the server")
public class CommandDisconnect extends Command {


    public CommandDisconnect(CommandRegistry registry) {
        super(registry, new PermissionLimiter(Permission.ADMIN));
    }

    @Override
    protected boolean executeCommand(CommandContext ctx) {
        sendMessage(ctx.getChannel(), "Disconnecting...");
        Eve.getInstance().disconnect();
        System.exit(0);
        return true;
    }

}
