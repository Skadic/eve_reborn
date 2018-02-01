package skadic.eve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import skadic.eve.commands.EveCommandManager;
import skadic.eve.listeners.MessageCounter;
import skadic.eve.listeners.ReadyListener;
import skadic.eve.main.EveLog;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;

@SpringBootApplication
public class Eve implements CommandLineRunner {

    @Autowired
    JdbcTemplate template;

    private static Eve instance;
    private static IDiscordClient client;

    @Override
    public void run(String... args) throws Exception {
        instance = this;

        client = createClient(args[0], true);

        manager = new EveCommandManager();

        messageCounter = new MessageCounter();
        manager.registerCommands();
        registerListeners();
    }

    private EveCommandManager manager;

    private MessageCounter messageCounter;

    private IDiscordClient createClient(String token, boolean login) {// Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            EveLog.error("Error during client building. Token may be invalid");
            return null;
        }
    }

    private void registerListeners(){
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new ReadyListener());
        dispatcher.registerListener((IListener<ReadyEvent>) (ReadyEvent event) -> client.getDispatcher().registerListener(messageCounter));
    }

    public static Eve getInstance() {
        return instance;
    }

    public static IDiscordClient getClient() {
        return client;
    }

    public void disconnect() {
        client.logout();
    }

    public MessageCounter getMessageCounter() {
        return messageCounter;
    }

    public EveCommandManager getManager() {
        return manager;
    }

    public static JdbcTemplate getTemplate() {
        return instance.template;
    }
}
