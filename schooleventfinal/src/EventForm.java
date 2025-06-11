import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventForm {
    private final Stage stage;
    private final EventManager eventManager;
    private final int index;
    private final SceneManager sceneManager;

    public EventForm(Stage stage, EventManager eventManager, int index, SceneManager sceneManager) {
        this.stage = stage;
        this.eventManager = eventManager;
        this.index = index;
        this.sceneManager = sceneManager;
    }

    public void show() {
        Label titleLabel = new Label(index >= 0 ? "編輯活動" : "新增活動");
        titleLabel.getStyleClass().add("label-title");

        Label idLabel = new Label("活動 ID:");
        TextField idField = new TextField();
        idField.setDisable(true);

        Label titleLbl = new Label("活動名稱:");
        TextField title = new TextField();
        title.getStyleClass().add("text-field");

        Label locationLbl = new Label("活動地點:");
        TextField location = new TextField();
        location.getStyleClass().add("text-field");

        Label timeLbl = new Label("活動日期:");
        DatePicker datePicker = new DatePicker();

        Label capacityLbl = new Label("人數上限:");
        TextField capacity = new TextField();
        capacity.getStyleClass().add("text-field");

        if (index >= 0) {
            Event ev = eventManager.getEvents().get(index);
            idField.setText(ev.id);
            title.setText(ev.title);
            location.setText(ev.location);
            datePicker.getEditor().setText(ev.time);
            capacity.setText(String.valueOf(ev.capacity));
        } else {
            idField.setText(generateNextEventID());
        }

        Button save = new Button("儲存");
        save.getStyleClass().add("primary-button");

        save.setOnAction(e -> {
            if (title.getText().isEmpty() || location.getText().isEmpty() ||
                    datePicker.getValue() == null || capacity.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "請填寫所有欄位！");
                return;
            }

            int cap;
            try {
                cap = Integer.parseInt(capacity.getText());
                if (cap <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "人數上限必須是正整數！");
                return;
            }

            String id = idField.getText();
            String dateText = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Event ev = new Event(id, title.getText(), location.getText(), dateText, cap);

            if (index >= 0) {
                eventManager.updateEvent(index, ev);
                showAlert(Alert.AlertType.INFORMATION, "活動已成功修改！");
            } else {
                eventManager.addEvent(ev);
                showAlert(Alert.AlertType.INFORMATION, "活動已成功建立！");
                try (FileWriter writer = new FileWriter("out.txt", true)) {
                    writer.write(String.format("新增活動: %s, 地點: %s, 時間: %s, 容量: %d\n",
                            ev.title, ev.location, ev.time, ev.capacity));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            new OrganizerView(stage, null, eventManager, sceneManager).show();
        });

        VBox layout = new VBox(10,
                titleLabel,
                idLabel, idField,
                titleLbl, title,
                locationLbl, location,
                timeLbl, datePicker,
                capacityLbl, capacity,
                save
        );
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.getStyleClass().add("form-layout");

        Scene scene = new Scene(layout, 450, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }

    private String generateNextEventID() {
        List<Event> events = eventManager.getEvents();
        int maxNum = 0;
        for (Event ev : events) {
            if (ev.id.matches("E\\d+")) {
                int num = Integer.parseInt(ev.id.substring(1));
                if (num > maxNum) maxNum = num;
            }
        }
        return String.format("E%03d", maxNum + 1);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
