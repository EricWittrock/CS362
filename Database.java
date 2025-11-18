import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

public class Database {
    public void ensureFileExists(String filename) {
        File file = new File(filename);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to create database file: " + e.getMessage());
        }
    }

    public void add(DatabaseObject obj, String filename) {
        ensureFileExists(filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(obj.serialize());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends DatabaseObject> ArrayList<T> getAll(String filename, Supplier<T> factory) {
        ArrayList<T> objects = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines
                T obj = factory.get();  // create new instance
                obj.deserialize(line);
                objects.add(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objects;
    }
}