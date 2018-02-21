package skadic.eve.commands;

import skadic.commands.CommandHelp;
import skadic.commands.CommandRegistry;
import skadic.commands.ServerCommandManager;
import skadic.eve.Eve;
import skadic.eve.commands.impl.*;

public class EveCommandManager extends ServerCommandManager{

    private static final String prefix = "'";

    public EveCommandManager() {
        super(Eve.getClient(), prefix);
    }

    @Override
    public void registerCommands(CommandRegistry registry) {
        registry.register(new CommandHelp(registry), "help");
        registry.register(new CommandSay(registry), "say");
        registry.register(new CommandDisconnect(registry), "disconnect", "dc", "shutdown");
        registry.register(new CommandStats(registry), "stats");
        registry.register(new CommandTitle(registry), "title");
        registry.register(new CommandZircons(registry), "zircons", "money", "gems");
        registry.register(new CommandPermission(registry), "permission", "permissions", "p");
        registry.register(new CommandAsk(registry), "ask");
        registry.register(new CommandMath(registry), "math", "calc");
    }
}
