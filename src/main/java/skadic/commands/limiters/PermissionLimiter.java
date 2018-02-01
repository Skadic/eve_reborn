package skadic.commands.limiters;

import skadic.commands.CommandContext;
import skadic.commands.Permission;

public class PermissionLimiter implements ILimiter{

    private final Permission permission;

    public PermissionLimiter(Permission permission){
        this.permission = permission;
    }

    @Override
    public boolean check(CommandContext ctx) {
        return Permission.getPermissionLevel(ctx.getAuthor(), ctx.getGuild()).compare(permission) >= 0;
    }

    public Permission getPermission() {
        return permission;
    }
}
