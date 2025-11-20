import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.function.Predicate;


class DataCache {
    private static Database db = new Database();

    // maps table name/class name to (id -> serialized line) map
    private static HashMap<String, HashMap<Integer, String>> DBCache = new HashMap<>();
    private static HashMap<Integer, DatabaseObject> changedObjects = new HashMap<>();


    public static <T extends DatabaseObject> T getById(int id, Supplier<T> factory) {
        T obj = factory.get();
        int result = populateDBObject(obj, id);
        if (result != 0) {
            return null;
        }
        return obj;
    }

    public static <T extends DatabaseObject> T getByFilter(Predicate<T> filter, Supplier<T> factory) {
        T obj = getAll(factory).stream()
            .filter(filter)
            .findFirst()
            .orElse(null);
        return obj;
    }

    public static <T extends DatabaseObject> List<T> getAllByFilter(Predicate<T> filter, Supplier<T> factory) {
        List<T> list = getAll(factory).stream()
            .filter(filter)
            .collect(Collectors.toList());
        return list;
    }

    private static int populateDBObject(DatabaseObject obj, int id) {
        String className = obj.getClass().getSimpleName().split("\\$")[0];

        if ( getTablePath(className) == null ) {
            System.out.println("Invalid class name: " + className);
            return 1;
        }

        if (changedObjects.containsKey(id)) {
            DatabaseObject existingObject = changedObjects.get(id);
            obj.deserialize(existingObject.serialize());
        }

        if (!DBCache.containsKey(className)) {
            DBCache.put(className, new HashMap<Integer, String>());
        }

        HashMap<Integer, String> map = DBCache.get(className);

        if (map.size() == 0) {
            loadAllDBStrings(className);
        }

        if(!map.containsKey(id)) {
            System.out.println("ID " + id + " not found in " + className + " table.");
            return 1; // not found
        }

        String dbLine = map.get(id);
        obj.deserialize(dbLine);
        changedObjects.put(id, obj);
        return 0;        
    }

    public static void addObject(DatabaseObject obj) {
        changedObjects.put(obj.getId(), obj);
    }

    private static void loadAllDBStrings(String className) {
        String tablePath = getTablePath(className);
        if (tablePath == null) {
            System.out.println("Invalid class name: " + className);
            return;
        }
        ArrayList<String> lines = db.getAllLines(tablePath);

        if (!DBCache.containsKey(className)) {
            DBCache.put(className, new HashMap<Integer, String>());
        }
        HashMap<Integer, String> map = DBCache.get(className);
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            int id = Integer.parseInt(parts[0]);
            map.put(id, line);
        }
    }

    public static <T extends DatabaseObject> ArrayList<T> getAll(Supplier<T> factory) {
        applyChangesToDBCache();
        T obj2 = factory.get();
        String className = factory.get().getClass().getSimpleName();
        String tablePath = getTablePath(className);
        if (tablePath == null) {
            System.out.println("getAll Invalid class name: " + className);
            return new ArrayList<T>();
        }

        loadAllDBStrings(className);
        ArrayList<T> objects = new ArrayList<>();
        HashMap<Integer, String> map = DBCache.get(className);
        for (String line : map.values()) {
            T obj = factory.get();
            if (obj == null || line.strip().isEmpty()) continue;
            obj.deserialize(line);
            if (obj == null ) continue;
            objects.add(obj);
        }
 
        return objects;
    }

    private static void applyChangesToDBCache() {
        for (DatabaseObject obj : changedObjects.values()) {
            String className = obj.getClass().getSimpleName().split("\\$")[0];
            String tablePath = getTablePath(className);
            if (tablePath == null) {
                System.out.println("Invalid class name: " + className);
                continue;
            }
            if (!DBCache.containsKey(className)) {
                DBCache.put(className, new HashMap<Integer, String>());
            }
            HashMap<Integer, String> map = DBCache.get(className);
            map.put(obj.getId(), obj.serialize());
        }
    }

    public static void saveAll() {
        applyChangesToDBCache();

        for (String className : DBCache.keySet()) {
            String tablePath = getTablePath(className);
            if (tablePath == null) {
                System.out.println("Invalid class name: " + className);
                continue;
            }
            HashMap<Integer, String> map = DBCache.get(className);
            ArrayList<String> lines = new ArrayList<>(map.values());
            db.overwriteAllLines(lines, tablePath);
        }
    }

    public static String getTablePath(String className) {
        switch(className) {
            case "Event": return "./Data/Events.txt";
            case "Venue": return "./Data/Venues.txt";
            case "Concession": return "./Data/Concessions.txt";
            case "ConcessionOrder": return "./Data/ConcessionOrders.txt";
            case "Merchandise": return "./Data/Merchandise.txt";
            case "MerchandiseOrder": return "./Data/MerchandiseOrders.txt";
            case "Script": return "./Data/Scripts.txt";
            case "ScriptAction": return "./Data/ScriptActions.txt";
            case "StreamingCompany": return "./Data/StreamingCompanies.txt";
            case "Wrestler": return "./Data/Wrestlers.txt";
            case "WrestlerSchedule": return "./Data/WrestlerSchedules.txt";
            case "WrestlerInsurance": return "./Data/WrestlerInsurances.txt";
            case "Section": return "./Data/Sections.txt";
            case "Budget": return "./Data/Budget.txt";
            case "Advertisement": return "./Data/Advertisement.txt";
            case "MediaContract": return "./Data/MediaContract.txt";
            default: return null;
        }
    }
}