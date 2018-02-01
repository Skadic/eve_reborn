package skadic.commands.limiters;

import skadic.commands.CommandContext;
import sx.blah.discord.handle.obj.IChannel;

public class ChannelLimiter extends IDLimiter {

    public ChannelLimiter(IChannel... channels) {
        super(channels);
    }

    public ChannelLimiter(long... ids) {
        super(ids);
    }

    @Override
    public long getID(CommandContext ctx) {
        return ctx.getChannel().getLongID();
    }
}
