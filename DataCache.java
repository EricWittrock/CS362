import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DataCache {
    private static final String eventFileName = "./Events.txt";
    private static final String venueFileName = "./Venues.txt";
    private static final String concessionFileName = "./Concession.txt";
    private static final String concessionOrderFileName = "./ConcessionOrder.txt";
    private static final String merchandiseFileName = "./Merchandise.txt";
    private static final String scriptFileName = "./Scripts.txt";
    private static final String scriptActionFileName = "./ScriptActions.txt";
    private static final String streamingCompanyFileName = "./StreamingCompanies.txt";
    private static final String wrestlerFileName = "./Wrestlers.txt";
    private static final String wrestlerScheduleFileName = "./WrestlerSchedules.txt";
    private static final String wrestlerInsuranceFileName = "./WrestlerInsurance.txt";
    private static final String sectionFileName = "./Sections.txt";

    private static Map<Integer, Event> eventCache = new HashMap<>();
    private static Map<Integer, Venue> venueCache = new HashMap<>();
    private static Map<Integer, Concession> concessionCache = new HashMap<>();
    private static Map<Integer, ConcessionOrder> concessionOrderCache = new HashMap<>();
    private static Map<Integer, Merchandise> merchandiseCache = new HashMap<>();
    private static Map<Integer, Script> scriptCache = new HashMap<>();
    private static List<ScriptAction> scriptActionsCache = new ArrayList<>();
    private static Map<Integer, StreamingCompany> streamingCompanyCache = new HashMap<>();
    private static Map<Integer, Wrestler> wrestlerCache = new HashMap<>();
    private static Map<Integer, WrestlerSchedule> wrestlerScheduleCache = new HashMap<>();
    private static List<WrestlerInsurance> wrestlerInsuranceCache = new ArrayList<>();
    private static Map<Integer, Section> sectionCache = new HashMap<>();

    private static Database db = new Database();

    public static void loadAll() {
        createIfMissingFiles();
        loadSectionData();
        loadVenueData();
        loadEventData();
        loadConcessionData();
        loadConcessionOrderData();
        loadMerchandiseData();
        loadScriptData();
        loadScriptActionData();
        loadWrestlerData();
        loadWrestlerScheduleData();
        loadWrestlerInsuranceData();
        loadStreamingCompanyData();
    }

    public static void saveAll() {
        saveEventData();
        saveVenueData();
        saveConcessionData();
        saveConcessionOrderData();
        saveMerchandiseData();
        saveScriptData();
        saveScriptActionData();
        saveWrestlerData();
        saveWrestlerScheduleData();
        saveWrestlerInsuranceData();
        saveStreamingCompanyData();
    }

    public static Event getEventById(int eventId) {
        return eventCache.get(eventId);
    }

    public static Venue getVenueById(int venueId) {
        return venueCache.get(venueId);
    }

    public static Concession getConcessionById(int id) {
        return concessionCache.get(id);
    }

    public static List<Concession> getConcessionByVenue(Venue venue) {
        int v_id = venue.getId();
        List<Concession> results = new ArrayList<>();
        for (Concession concession : concessionCache.values()) {
            if (concession.getVenueId() == v_id) {
                results.add(concession);
            }
        }
        return results;
    }

    public static ConcessionOrder getConcessionOrderById(int id) {
        return concessionOrderCache.get(id);
    }

    public static List<ConcessionOrder> getConcessionOrderByName(String name) {
        List<ConcessionOrder> results = new ArrayList<>();
        for (ConcessionOrder order : concessionOrderCache.values()) {
            if (order.getOrderer() == name) {
                results.add(order);
            }
        }
        return results;
    }

    public static List<ConcessionOrder> getConcessionOrderBySupplier(String name) {
        List<ConcessionOrder> results = new ArrayList<>();
        for (ConcessionOrder order : concessionOrderCache.values()) {
            if (order.getSupplier() == name) {
                results.add(order);
            }
        }
        return results;
    }

    public static Script getScriptById(int scriptId) {
        for (Script script : scriptCache.values()) {
            if (script.getId() == scriptId) {
                return script;
            }
        }
        return null;
    }

    public static ScriptAction getScriptActionById(int actionId) {
        for (ScriptAction action : scriptActionsCache) {
            if (action.getActionId() == actionId) {
                return action;
            }
        }
        return null;
    }

    public static List<ScriptAction> getActionsForScript(int scriptId) {
        List<ScriptAction> result = new ArrayList<>();
        for (ScriptAction action : scriptActionsCache) {
            if (action.getScriptId() == scriptId) {
                result.add(action);
            }
        }
        
        result.sort(Comparator.comparingInt(ScriptAction::getSequenceOrder));
        return result;
    }

    public static Wrestler getWrestlerById(int wrestlerId) {
        return wrestlerCache.get(wrestlerId);
    }

    public static WrestlerInsurance getWrestlerInsuranceById(int insuranceId) {
        for (WrestlerInsurance insurance : wrestlerInsuranceCache) {
            if (insurance.getInsuranceId() == insuranceId) {
                return insurance;
            }
        }
        return null;
    }

    public static WrestlerInsurance getInsuranceByWrestlerId(int wrestlerId) {
        for (WrestlerInsurance insurance : wrestlerInsuranceCache) {
            if (insurance.getWrestlerId() == wrestlerId) {
                return insurance;
            }
        }
        return null;
    }
    
    public static StreamingCompany getStreamingCompanyById(int streamingCompanyId) {
        return streamingCompanyCache.get(streamingCompanyId);
    }

    public static Merchandise getMerchandiseById(int id)
    {
        return merchandiseCache.get(id);
    }

    public static List<Event> getAllEvents() {
        return new ArrayList<>(eventCache.values());
    }

    public static void addEvent(Event event) {
        eventCache.put(event.getId(), event);
    }

    public static List<Venue> getAllVenues() {
        return new ArrayList<>(venueCache.values());
    }

    public static void addVenue(Venue venue) {
        venueCache.put(venue.getId(), venue);
    }

    public static void addMerchandise(Merchandise merchandise)
    {
        merchandiseCache.put(merchandise.getId(), merchandise);
    }

    public static void addConcession(Concession concession) {
        concessionCache.put(concession.getId(), concession);
    }

    public static void addConcessionOrder(ConcessionOrder order) {
        concessionOrderCache.put(order.getId(), order);
    }

    public static void addScript(Script script) {
        scriptCache.put(script.getId(), script);
    }

    public static void addScriptAction(ScriptAction action) {
        scriptActionsCache.add(action);
    }

    public static void addSection(Section section) {
        sectionCache.put(section.getId(), section);
    }

    public static void addStreamingCompany(StreamingCompany company) {
        streamingCompanyCache.put(company.getId(), company);
    }

    public static void addWrestlerInsurance(WrestlerInsurance insurance) {
        wrestlerInsuranceCache.add(insurance);
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

    public static List<WrestlerInsurance> getAllWrestlerInsurances() {
        return new ArrayList<>(wrestlerInsuranceCache);
    }

    public static List<ScriptAction> getAllScriptActions() {
        return new ArrayList<>(scriptActionsCache);
    }

    public static List<Script> getAllScripts() {
        return new ArrayList<>(scriptCache.values());
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

    private static void loadConcessionData() {
        List<Concession> concessions = db.getAll(concessionFileName, Concession::new);
        for (Concession concession : concessions) {
            concessionCache.put(concession.getId(), concession);
        }
    }

    private static void saveConcessionData() {
        for (Concession concession : concessionCache.values()) {
            db.add(concession, concessionFileName);
        }
    }

    private static void loadConcessionOrderData() {
        List<ConcessionOrder> orders = db.getAll(concessionOrderFileName, ConcessionOrder::new);
        for (ConcessionOrder order : orders) {
            concessionOrderCache.put(order.getId(), order);
        }
    }

    private static void saveConcessionOrderData() {
        for (ConcessionOrder order : concessionOrderCache.values()) {
            db.add(order, concessionOrderFileName);
        }
    }

    private static void loadScriptData() {
        List<Script> scripts = db.getAll(scriptFileName, Script::new);
        for (Script script : scripts) {
            scriptCache.put(script.getId(), script);
        }
    }

    private static void loadScriptActionData() {
        scriptActionsCache = db.getAll(scriptActionFileName, ScriptAction::new);
    }

    private static void loadSectionData() {
        List<Section> sections = db.getAll(sectionFileName, Section::new);
        for (Section section : sections) {
            sectionCache.put(section.getId(), section);
        }
    }

    private static void loadWrestlerInsuranceData() {
        wrestlerInsuranceCache = db.getAll(wrestlerInsuranceFileName, WrestlerInsurance::new);
    }

    private static void saveWrestlerInsuranceData() {
        for (WrestlerInsurance insurance : wrestlerInsuranceCache) {
            db.add(insurance, wrestlerInsuranceFileName);
        }
    }

    private static void saveScriptData() {
        for (Script script : scriptCache.values()) {
            db.add(script, scriptFileName);
        }
    }

    private static void saveScriptActionData() {
        for (ScriptAction action : scriptActionsCache) {
            db.add(action, scriptActionFileName);
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

    private static void loadMerchandiseData() {
        List<Merchandise> merchandise = db.getAll(merchandiseFileName, Merchandise::new);
        for (Merchandise merch : merchandise) {
            merchandiseCache.put(merch.getId(), merch);
        }
    }

    private static void createIfMissingFiles(){
        db.ensureFileExists(concessionFileName);
        db.ensureFileExists(concessionOrderFileName);
        db.ensureFileExists(eventFileName);
        db.ensureFileExists(venueFileName);
        db.ensureFileExists(scriptFileName);
        db.ensureFileExists(scriptActionFileName);
        db.ensureFileExists(sectionFileName);
        db.ensureFileExists(wrestlerFileName);
        db.ensureFileExists(wrestlerScheduleFileName);
        db.ensureFileExists(wrestlerInsuranceFileName);
        db.ensureFileExists(streamingCompanyFileName);
        db.ensureFileExists(merchandiseFileName);
    }
}