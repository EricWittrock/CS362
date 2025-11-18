import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DataCache {
    private static final String eventFileName = "./Events.txt";
    private static final String venueFileName = "./Venues.txt";
    private static final String scriptFileName = "./Scripts.txt";
    private static final String streamingCompanyFileName = "./StreamingCompanies.txt";
    private static final String wrestlerFileName = "./Wrestlers.txt";
    private static final String wrestlerScheduleFileName = "./WrestlerSchedules.txt";
    private static final String sectionFileName = "./Sections.txt";

    private static Map<Integer, Event> eventCache = new HashMap<>();
    private static Map<Integer, Venue> venueCache = new HashMap<>();
    private static Map<Integer, Script> scriptCache = new HashMap<>();
    private static Map<Integer, StreamingCompany> streamingCompanyCache = new HashMap<>();
    private static Map<Integer, Wrestler> wrestlerCache = new HashMap<>();
    private static Map<Integer, WrestlerSchedule> wrestlerScheduleCache = new HashMap<>();
    private static Map<Integer, Section> sectionCache = new HashMap<>();

    private static Database db = new Database();

    public static void loadAll() {
        createIfMissingFiles();
        loadSectionData();
        loadVenueData();
        loadEventData();
        loadScriptData();
        loadWrestlerData();
        loadWrestlerScheduleData();
        loadStreamingCompanyData();
    }

    public static void saveAll() {
        saveEventData();
        saveVenueData();
        saveScriptData();
        saveWrestlerData();
        saveWrestlerScheduleData();
        saveStreamingCompanyData();
    }

    public static Event getEventById(int eventId) {
        return eventCache.get(eventId);
    }

    public static Venue getVenueById(int venueId) {
        return venueCache.get(venueId);
    }

    public static Script getScriptById(int scriptId) {
        return scriptCache.get(scriptId);
    }

    public static Wrestler getWrestlerById(int wrestlerId) {
        return wrestlerCache.get(wrestlerId);
    }
    
    public static StreamingCompany getStreamingCompanyById(int streamingCompanyId) {
        return streamingCompanyCache.get(streamingCompanyId);
    }

    public static List<Event> getAllEvents() {
        return new ArrayList<>(eventCache.values());
    }

    public static void addEvent(Event event) {
        eventCache.put(event.getId(), event);
    }

    public static void addVenue(Venue venue) {
        venueCache.put(venue.getId(), venue);
    }

    public static void addScript(Script script) {
        scriptCache.put(script.getId(), script);
    }

    public static void addSection(Section section) {
        sectionCache.put(section.getId(), section);
    }

    public static void addStreamingCompany(StreamingCompany company) {
        streamingCompanyCache.put(company.getId(), company);
    }

    public static List<StreamingCompany> getAllStreamingCompanies() {
        return new ArrayList<>(streamingCompanyCache.values());
    }

    public static List<Wrestler> getAllWrestlers() {
        return new ArrayList<>(wrestlerCache.values());
    }

    public static List<WrestlerSchedule> getAllWrestlerSchedules() {
        return new ArrayList<>(wrestlerScheduleCache.values());
    }

    public static void addWrestler(Wrestler wrestler) {
        wrestlerCache.put(wrestler.getId(), wrestler);
    }

    public static void addWrestlerSchedule(WrestlerSchedule schedule) {
        wrestlerScheduleCache.put(schedule.getId(), schedule);
    }
    

    private static void loadEventData() {
        List<Event> events = db.getAll(eventFileName, Event::new);
        for (Event event : events) {
            eventCache.put(event.getId(), event);
        }
    }

    private static void saveEventData() {
        for (Event event : eventCache.values()) {
            db.add(event, eventFileName);
        }
    }

    private static void loadScriptData() {
        List<Script> scripts = db.getAll(scriptFileName, Script::new);
        for (Script script : scripts) {
            scriptCache.put(script.getId(), script);
        }
    }

    private static void loadSectionData() {
        List<Section> sections = db.getAll(sectionFileName, Section::new);
        for (Section section : sections) {
            sectionCache.put(section.getId(), section);
        }
    }

    private static void saveScriptData() {
        for (Script script : scriptCache.values()) {
            db.add(script, scriptFileName);
        }
    }

    private static void loadVenueData() {
        List<Venue> venues = db.getAll(venueFileName, Venue::new);
        for (Venue venue : venues) {
            venueCache.put(venue.getId(), venue);
        }
    }

    private static void saveVenueData() {
        for (Venue venue : venueCache.values()) {
            db.add(venue, venueFileName);
        }
    }

    private static void loadWrestlerData() {
        List<Wrestler> wrestlers = db.getAll(wrestlerFileName, Wrestler::new);
        for (Wrestler wrestler : wrestlers) {
            wrestlerCache.put(wrestler.getId(), wrestler);
        }
    }

    private static void saveWrestlerData() {
        for (Wrestler wrestler : wrestlerCache.values()) {
            db.add(wrestler, wrestlerFileName);
        }
    }

    private static void loadWrestlerScheduleData() {
        List<WrestlerSchedule> schedules = db.getAll(wrestlerScheduleFileName, WrestlerSchedule::new);
        for (WrestlerSchedule schedule : schedules) {
            wrestlerScheduleCache.put(schedule.getId(), schedule);
        }
    }

    private static void saveWrestlerScheduleData() {
        for (WrestlerSchedule schedule : wrestlerScheduleCache.values()) {
            db.add(schedule, wrestlerScheduleFileName);
        }
    }

    private static void loadStreamingCompanyData() {
        List<StreamingCompany> companies = db.getAll(streamingCompanyFileName, StreamingCompany::new);
        for (StreamingCompany company : companies) {
            streamingCompanyCache.put(company.getId(), company);
        }
    }

    private static void saveStreamingCompanyData() {
        for (StreamingCompany company : streamingCompanyCache.values()) {
            db.add(company, streamingCompanyFileName);
        }
    }

    private static void createIfMissingFiles(){
        db.ensureFileExists(eventFileName);
        db.ensureFileExists(venueFileName);
        db.ensureFileExists(scriptFileName);
        db.ensureFileExists(sectionFileName);
        db.ensureFileExists(wrestlerFileName);
        db.ensureFileExists(wrestlerScheduleFileName);
        db.ensureFileExists(streamingCompanyFileName);
    }
}