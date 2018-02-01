package skadic.commands.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import skadic.commands.Permission;
import skadic.commands.serializers.EnumMapSerializer;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eti22 on 01.01.2017.
 */
@SuppressWarnings("Duplicates")
public class Utils {

    private static final KryoPool pool = new KryoPool.Builder(Kryo::new).softReferences().build();

    /**
     * Writes a map into a file using Kryo.
     * @param map The map that should be written into the file.
     * @param file The file name that the Map should be written into
     */
    public static void serializeMap(Map map, String file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(getFileInWorkingDir("data/maps/" + file));
        Kryo kryo = pool.borrow();
        if(map instanceof EnumMap)kryo.register(EnumMap.class, new EnumMapSerializer());
        Output output = new Output(fos);
        kryo.writeObject(output, map);
        output.close();
        pool.release(kryo);
        CommandLog.d("Map serialized to file maps/" + file);
    }

    /**
     * Returns a map that has been read from a file using Kryo.
     * @param file The file name that the map should be read from.
     * @param mapClass The class of the map's type
     * @return The map that has been read from the file.
     */
    public static Map deserializeMap(String file, Class<? extends Map> mapClass){
        try {
            try {
                FileInputStream fis = new FileInputStream(getFileInWorkingDir("data/maps/" + file));
                Kryo kryo = pool.borrow();
                Input input = new Input(fis);

                Map map = kryo.readObject(input, mapClass);
                input.close();
                pool.release(kryo);
                if(map != null){
                    return map;
                } else {
                    if(mapClass.isInstance(new EnumMap<>(Permission.class)))
                        return new EnumMap<>(Permission.class);
                    else
                        return mapClass.newInstance();
                }
            } catch (FileNotFoundException e) {
                if(mapClass.isInstance(new EnumMap<>(Permission.class)))
                    return new EnumMap<>(Permission.class);
                else
                    return mapClass.newInstance();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked cast")
    public static <T extends Enum<T>, K> EnumMap<T, K> deserializeEnumMap(String file, Class<T> enoom){
        try {
            FileInputStream fis = new FileInputStream(getFileInWorkingDir("data/maps/" + file));
            Kryo kryo = pool.borrow();
            kryo.register(EnumMap.class, new EnumMapSerializer());
            Input input = new Input(fis);

            EnumMap<T, K> map = kryo.readObject(input, EnumMap.class);
            input.close();
            pool.release(kryo);
            if(map != null){
                return map;
            } else {
                return (new EnumMap<>(enoom));
            }
        } catch (FileNotFoundException e) {
            return new EnumMap<>(enoom);
        }
    }


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
