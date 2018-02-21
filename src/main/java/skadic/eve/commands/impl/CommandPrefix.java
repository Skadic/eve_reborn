package skadic.eve.commands.impl;

import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.util.Utils;

// TODO: 01.02.2018 Just do this
public class CommandPrefix extends Command {
    public CommandPrefix(CommandRegistry registry) {
        super(registry);
    }

    @Override
    protected boolean execute(CommandContext ctx) {
        if(ctx.getArgs().size() > 0) return false;

        Utils.sendMessage(ctx.getChannel(),ctx.getGuild().getName() + "'s command prefix is: " + registry.getPrefix());
        return true;
    }

    private class CommandSetPrefix extends SubCommand {

        public CommandSetPrefix(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand, new PermissionLimiter(Permission.ADMIN));
        }

        @Override
        protected boolean execute(CommandContext ctx) {

            return true;
        }
    }
}
