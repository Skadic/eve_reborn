package skadic.eve.commands.impl;

import skadic.commands.Command;
import skadic.commands.CommandContext;
import skadic.commands.CommandRegistry;
import skadic.commands.Help;
import skadic.commands.util.Utils;

@Help(description = "Makes me say what you want", syntax = "<text>")
public class CommandSay extends Command {

    public CommandSay(CommandRegistry registry) {
        super(registry);
    }

    @Override
    public boolean execute(CommandContext ctx) {
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
