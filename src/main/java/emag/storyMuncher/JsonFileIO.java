package emag.storyMuncher;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

class JsonFileIO {
    private static final String filepath="config.json";

    Boolean checkConfigFile() {
        File f = new File(filepath);

        return f.exists() && !f.isDirectory();
    }

    Config ReadObjectFromFile() {

        Config config = new Config();

        try {
            JsonReader reader = new JsonReader(new FileReader(filepath));

            Gson gson = new Gson();
            config = gson.fromJson(reader, Config.class);

            System.out.println("The Object has been read from the file");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return config;
    }

    void WriteObjectToFile(Config config) {

        Gson jsonObject = new Gson();

        try {
            FileWriter file = new FileWriter(filepath);
            file.write(jsonObject.toJson(config));
            file.close();
            System.out.println("The Object  was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
