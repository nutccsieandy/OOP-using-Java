import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event {
    public String id, title, location, time;
    public int capacity;

    public Event(String id, String title, String location, String time, int capacity) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.time = time;
        this.capacity = capacity;
    }

    public String getStatus(int registeredCount) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = LocalDate.parse(this.time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (today.isAfter(eventDate)) {
            return "已結束";
        } else if (registeredCount >= capacity) {
            return "額滿";
        } else {
            return "開放中";
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s @ %s, %s (%d人)", id, title, location, time, capacity);
    }
}
