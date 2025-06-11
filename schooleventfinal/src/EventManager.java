import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private final List<Event> events = new ArrayList<>();
    private final Map<String, List<String>> registrations = new HashMap<>();

    // 獲取所有活動
    public List<Event> getEvents() {
        return events;
    }

    // 新增活動
    public void addEvent(Event e) {
        events.add(e);
    }

    // 編輯活動
    public void updateEvent(int idx, Event updated) {
        events.set(idx, updated);
    }

    // 刪除活動並移除所有報名紀錄
    public void deleteEvent(int idx) {
        Event removed = events.remove(idx);
        for (List<String> list : registrations.values()) {
            list.remove(removed.id);
        }
    }

    // 報名活動
    public void register(String user, String eventId) {
        registrations.putIfAbsent(user, new ArrayList<>());
        List<String> list = registrations.get(user);
        if (!list.contains(eventId)) list.add(eventId);
    }

    // 取消報名
    public void cancel(String user, String eventId) {
        if (registrations.containsKey(user)) {
            registrations.get(user).remove(eventId);
        }
    }

    // 查詢使用者報名清單
    public List<String> getUserRegistrations(String user) {
        return registrations.getOrDefault(user, new ArrayList<>());
    }

    // 查詢某活動所有報名人員
    public List<String> getRegistrants(String eventId) {
        List<String> result = new ArrayList<>();
        for (String user : registrations.keySet()) {
            if (registrations.get(user).contains(eventId)) {
                result.add(user);
            }
        }
        return result;
    }

    // 🔍 搜尋活動（關鍵字 in title 或日期）
    public List<Event> getFilteredEvents(String keyword) {
        return events.stream()
                .filter(ev -> ev.title.contains(keyword) || ev.time.contains(keyword))
                .collect(Collectors.toList());
    }
}
