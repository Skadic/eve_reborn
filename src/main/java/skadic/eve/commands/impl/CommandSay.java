package skadic.eve.commands.impl;

import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.SubCommand;
import skadic.commands.util.Utils;

@Help(description = "Makes me say what you want", syntax = "<text>")
public class CommandSay extends Command {

    public CommandSay(CommandRegistry registry) {
        super(registry, new PermissionLimiter(Permission.LOW, registry));
    }

    @Override
    public boolean executeCommand(CommandContext ctx) {
        if(!ctx.getArgs().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            ctx.getArgs().forEach((s) -> sb.append(s).append(" "));

            Utils.sendMessage(ctx.getChannel(), String.format("**%s** said: \"%s\"", ctx.getAuthor().getNicknameForGuild(ctx.getGuild()), sb.toString().trim()));
            return true;
        } else {
            return false;
        }
    }
}
