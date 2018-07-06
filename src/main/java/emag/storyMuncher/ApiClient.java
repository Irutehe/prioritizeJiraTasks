package emag.storyMuncher;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ApiClient {

    public Config config;

    public ApiClient(Config config){
        this.config = config;
    }

    public List<JiraSprint> getSprints(){

        List<JiraSprint> availableSprints = new ArrayList<>();

        JSONObject json;
        json = getJiraSprints(config.getJiraHost(), config.getJiraBoardId(), config.getJiraUser(), config.getJiraPassword());
        JSONArray object;
        try {
            object = json.getJSONArray("values");
            for (int i = 0; i < object.length (); ++i) {

                JSONObject obj = object.getJSONObject(i);
                JiraSprint sprint = new JiraSprint();

                sprint.setId(obj.getInt("id"));
                sprint.setName(obj.getString("name"));
                sprint.setStartDate(new DateTime(obj.getString("startDate")));
                sprint.setEndDate(new DateTime(obj.getString("endDate")));
                sprint.setGoal(obj.getString("goal"));

                int workingDays = getWorkingDaysBetweenTwoDates(sprint.getStartDate().toDate(), sprint.getEndDate().toDate());

                sprint.setRemainingSeconds(transformWorkingDaysToSeconds(workingDays));

                availableSprints.add(sprint);

            }
        } catch (Exception ex){
            System.out.println(ex.getStackTrace());
        }

        return availableSprints;
    }

    JSONObject getJiraSprints(String jiraHost, int boardId, String user, String pass) {

        JSONObject json =  new JSONObject();

        try {
            URL url = new URL (jiraHost + "/rest/agile/1.0/board/" + boardId + "/sprint?state=active");
            String encoding = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());

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

    public static int transformWorkingDaysToSeconds(int workingDays){
        int hoursPerDay = 8;
        return workingDays * hoursPerDay * 3600;
    }

    public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
