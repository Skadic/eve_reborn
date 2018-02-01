package skadic.commands.limiters;

import org.apache.commons.lang3.ArrayUtils;
import skadic.commands.CommandContext;
import sx.blah.discord.handle.obj.IIDLinkedObject;

import java.util.Arrays;

public abstract class IDLimiter implements ILimiter {

    private long[] ids;

    public IDLimiter(IIDLinkedObject... objects) {
        ids = Arrays.stream(objects).mapToLong(IIDLinkedObject::getLongID).toArray();
    }

    public IDLimiter(long... ids) {
        this.ids = ids;
    }

    @Override
    public boolean check(CommandContext ctx) {
        return ArrayUtils.contains(ids, getID(ctx));
    }

    public abstract long getID(CommandContext ctx);
}
