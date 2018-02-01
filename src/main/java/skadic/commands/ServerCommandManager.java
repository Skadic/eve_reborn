package skadic.commands;

import skadic.commands.listeners.CommandListener;
import skadic.commands.listeners.ServerRegisterListener;
import sx.blah.discord.api.IDiscordClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ServerCommandManager{
    protected final Map<Long, CommandRegistry> serverMap;
    private final Map<Permission, String> defaultPrefixes;

    public ServerCommandManager(IDiscordClient client, Map<Permission, String> defaultPrefixes) {
        this.defaultPrefixes = defaultPrefixes;
        serverMap = new HashMap<>();
        client.getDispatcher().registerListener(new ServerRegisterListener(this));
        client.getDispatcher().registerListener(new CommandListener(this));
    }

    public final CommandRegistry getRegistryForServer(long serverID){
        if(!serverMap.containsKey(serverID))
            registerServer(serverID);
        return serverMap.get(serverID);
    }

    public final void registerServer(long serverID){
        if(serverMap.containsKey(serverID)) throw new IllegalArgumentException("Server with ID " + serverID + " cannot be registered twice!");

        serverMap.put(serverID, new CommandRegistry(defaultPrefixes));
        registerCommands(serverMap.get(serverID));
    }

    public final void registerCommands(){
        for (CommandRegistry registry : serverMap.values())
            registerCommands(registry);
    }

    public abstract void registerCommands(CommandRegistry registry);

    public final Collection<CommandRegistry> getRegistries(){
        return serverMap.values();
    }

    public final Set<Long> getServers(){
        return serverMap.keySet();
    }

    public final Map<Long, CommandRegistry> getServerMap() {
        return serverMap;
    }
}
