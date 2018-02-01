package skadic.commands.listeners;

import skadic.commands.ServerCommandManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IGuild;

public class ServerRegisterListener implements IListener<GuildCreateEvent>{

    private final ServerCommandManager manager;

    public ServerRegisterListener(ServerCommandManager manager){
        this.manager = manager;
    }

    @Override
    public void handle(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        manager.registerServer(guild.getLongID());
    }
}
