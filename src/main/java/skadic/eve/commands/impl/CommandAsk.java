package skadic.eve.commands.impl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.json.JSONException;
import org.json.JSONObject;
import skadic.commands.Command;
import skadic.commands.CommandContext;
import skadic.commands.CommandRegistry;
import skadic.commands.Help;
import skadic.commands.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Help(syntax = "[Question]", description = "Uses the Wolfram Alpha API to give you answers to the meaning of life")
public class CommandAsk extends Command {

    private ExecutorService service;
    private static final String APP_ID = "6L38XA-LV63JVPW8A";
    private static final String BASE_URL = "http://api.wolframalpha.com/v1/result?appid=" + APP_ID + "&i=";
    private static final String ADV_URL = "http://api.wolframalpha.com/v2/query?appid=" + APP_ID + "&input=";

    public CommandAsk(CommandRegistry registry) {
        super(registry);
        service = Executors.newFixedThreadPool(10);
    }

    @Override
    protected boolean executeCommand(CommandContext ctx) {
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) return false;
        String parsedArgs = parse(args);
        String query = ADV_URL + parsedArgs + "&format=plaintext&output=json";
        String result = executePost(query);
        //includepodid=Definition:WordData&includepodid=Result
        try {
            if(result != null) {
                JSONObject json = new JSONObject(executePost(query));
                json = json.getJSONObject("queryresult");

                String text = json
                        .getJSONArray("pods")
                        .getJSONObject(0)
                        .getJSONArray("subpods")
                        .getJSONObject(0)
                        .getString("plaintext");

                text.replace("\\n", "\n");
                Utils.sendMessage(ctx.getChannel(), text);
                return true;
            } else
                Utils.sendMessage(ctx.getChannel(), ":thinking:");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.sendMessage(ctx.getChannel(), ":thinking:");
            return true;
        }
    }

    private static String parse(Collection<String> tokens){
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            try {
                sb.append(URLEncoder.encode(token, "UTF-8")).append("+");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String executePost(String targetURL) {
        HttpResponse<String> response = null;
        try {
            GetRequest request = Unirest.get(targetURL);
            response = request.asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response == null ? null : response.getBody();
    }
}
