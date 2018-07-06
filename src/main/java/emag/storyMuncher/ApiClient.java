package emag.storyMuncher;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;

public class ApiClient {

    JSONObject getSprints() {

        JSONObject json =  new JSONObject();

        try {
            URL url = new URL ("https://jira.emag.network/rest/agile/1.0/board/8/sprint?state=active");
            String encoding = Base64.getEncoder().encodeToString(("dashboard.qa:parolaQA1.").getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            InputStream content = connection.getInputStream();

            BufferedReader in   = new BufferedReader (new InputStreamReader(content));
            json =  new JSONObject(in.readLine());

        } catch(Exception e) {
            e.printStackTrace();
        }

        return json;
    }


}
