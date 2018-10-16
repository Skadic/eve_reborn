package skadic.eve.commands.impl;

import skadic.commands.*;
import skadic.commands.limiters.PermissionLimiter;
import skadic.commands.util.Utils;

public class CommandPrefix extends Command {
    public CommandPrefix(CommandRegistry registry) {
        super(registry);
        addSubCommand(new CommandSetPrefix(registry, this) ,"seti");
    }

    @Override
    protected boolean execute(CommandContext ctx) {
        if(ctx.getArgs().size() > 0) return false;

        Utils.sendMessage(ctx.getChannel(),ctx.getGuild().getName() + "'s command prefix is: " + registry.getPrefix());
        return true;
    }

    private class CommandSetPrefix extends SubCommand {

        private final CommandRegistry registry;

        public CommandSetPrefix(CommandRegistry registry, Command parentCommand) {
            super(registry, parentCommand, new PermissionLimiter(Permission.ADMIN));
            this.registry = registry;
        }

        @Override
        protected boolean execute(CommandContext ctx) {
            // TODO: 05.03.2018  finish this


            return true;
        }
    }
}
