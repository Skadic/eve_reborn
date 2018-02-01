package skadic.commands.util;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by eti22 on 01.01.2017.
 */
@SuppressWarnings("Duplicates")
public class Utils {

    public static void sendMessage(IChannel channel, String message){
        try {
            channel.sendMessage(message);
        } catch (RateLimitException e) {
            handleRateLimit(e);
            sendMessage(channel, message);
        } catch (MissingPermissionsException e) {
            CommandLog.w("Missing permission to send Message to channel: " + channel.getName());
        } catch (DiscordException e){
            CommandLog.e("Error sending message", e);
        }
    }

    public static void sendMessage(IChannel channel, EmbedObject embed){
        try {
            channel.sendMessage(embed);
        } catch (RateLimitException e) {
            handleRateLimit(e);
            sendMessage(channel, embed);
        } catch (MissingPermissionsException e) {
            CommandLog.w("Missing permission to send Message to channel: " + channel.getName());
        } catch (DiscordException e){
            CommandLog.e("Error sending message", e);
        }
    }

    public static void sendMessage(IChannel channel, String message, EmbedObject embed){
        try {
            channel.sendMessage(message, embed);
        } catch (RateLimitException e) {
            handleRateLimit(e);
            sendMessage(channel, message, embed);
        } catch (MissingPermissionsException e) {
            CommandLog.w("Missing permission to send Message to channel: " + channel.getName());
        } catch (DiscordException e){
            CommandLog.e("Error sending message", e);
        }
    }

    public static File getFileInWorkingDir(String path){
        return new File(System.getProperty("user.dir") + "/" + path);
    }

    public static File getFileFromResource(String path){
        return getFileInWorkingDir("src/main/resources/" + path);
    }

    public static EmbedObject buildBasicEmbed(String title, String desc, Color color){
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle(title);
        builder.withDescription(desc);
        builder.withColor(color);

        return builder.build();
    }

    public static boolean stringContainsAny(String string, String... tokens){
        for (String token : tokens)
            if(string.contains(token))
                return true;
        return false;
    }

    public static boolean stringStartsWithAny(String string, String... prefixes){
        for (String prefix : prefixes)
            if(string.startsWith(prefix))
                return true;
        return false;
    }

    public static List<IMessage> bulkDelete(IChannel channel){
        try {
            return channel.bulkDelete();
        } catch (RateLimitException e){
            handleRateLimit(e);
            return bulkDelete(channel);
        }
    }

    private static void handleRateLimit(RateLimitException e){
        try {
            Thread.sleep(e.getRetryDelay() + 50L);
        } catch (InterruptedException e1) {
            CommandLog.i("Handling rate limit, sleeping for " + (e.getRetryDelay() + 50L) + "ms");
        }
    }
}
