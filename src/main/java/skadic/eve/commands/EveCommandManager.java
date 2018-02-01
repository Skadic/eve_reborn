package skadic.eve.commands;

import skadic.commands.CommandHelp;
import skadic.commands.CommandRegistry;
import skadic.commands.Permission;
import skadic.commands.ServerCommandManager;
import skadic.eve.Eve;
import skadic.eve.commands.impl.*;

import java.util.HashMap;
import java.util.Map;

import static skadic.commands.Permission.*;

public class EveCommandManager extends ServerCommandManager{

    private static final Map<Permission, String> defaultPrefixes = new HashMap<>();

    static {
        defaultPrefixes.put(ADMIN, "e!!!!");
        defaultPrefixes.put(HIGH, "e!!!");
        defaultPrefixes.put(MEDIUM, "e!!");
        defaultPrefixes.put(LOW, "e!");
    }

    public EveCommandManager() {
        super(Eve.getClient(), defaultPrefixes);
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
    }
}
