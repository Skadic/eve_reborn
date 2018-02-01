package skadic.commands.limiters;

import skadic.commands.CommandContext;
import skadic.commands.CommandRegistry;
import skadic.commands.Permission;

public class PermissionLimiter implements ILimiter{

    private final Permission permission;
    private final String prefix;

    public PermissionLimiter(Permission permission, CommandRegistry registry) {
        this.permission = permission;
        this.prefix = registry.getPrefixMap().get(permission);
    }

    @Override
    public boolean check(CommandContext ctx) {
        return Permission.getPermissionLevel(ctx.getAuthor(), ctx.getGuild()).compare(permission) >= 0 && ctx.getPrefixUsed().equals(prefix);
    }

    public Permission getPermission() {
        return permission;
    }
}
