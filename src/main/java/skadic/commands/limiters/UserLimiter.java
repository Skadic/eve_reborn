package skadic.commands.limiters;

import skadic.commands.CommandContext;
import sx.blah.discord.handle.obj.IUser;

public class UserLimiter extends IDLimiter{


    public UserLimiter(IUser... users) {
        super(users);
    }

    public UserLimiter(long... ids) {
        super(ids);
    }

    @Override
    public long getID(CommandContext ctx) {
        return ctx.getAuthor().getLongID();
    }
}
