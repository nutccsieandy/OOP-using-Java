import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OrganizerView {
    private final Stage stage;
    private final UserSession session;
    private final EventManager eventManager;
    private final SceneManager sceneManager;

    public OrganizerView(Stage stage, UserSession session, EventManager eventManager, SceneManager sceneManager) {
        this.stage = stage;
        this.session = session;
        this.eventManager = eventManager;
        this.sceneManager = sceneManager;
    }

    public void show() {
        ListView<String> list = new ListView<>();
        list.setPrefHeight(200); // 美化大小
        updateList(list);

        Button create = new Button("建立活動");
        Button edit = new Button("編輯活動");
        Button delete = new Button("刪除活動");
        Button viewReg = new Button("查看報名名單");
        Button exportCSV = new Button("匯出名單");
        Button logout = new Button("登出");

        create.getStyleClass().add("primary-button");
        edit.getStyleClass().add("primary-button");
        delete.getStyleClass().add("danger-button");
        viewReg.getStyleClass().add("primary-button");
        exportCSV.getStyleClass().add("primary-button");
        logout.getStyleClass().add("danger-button");

        create.setOnAction(e -> new EventForm(stage, eventManager, -1, sceneManager).show());

        edit.setOnAction(e -> {
            int idx = list.getSelectionModel().getSelectedIndex();
            if (idx >= 0) new EventForm(stage, eventManager, idx, sceneManager).show();
        });

        delete.setOnAction(e -> {
            int idx = list.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                eventManager.deleteEvent(idx);
                updateList(list); // 更新畫面
            }
        });

        viewReg.setOnAction(e -> {
            int idx = list.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                Event ev = eventManager.getEvents().get(idx);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("報名名單");
                alert.setHeaderText("活動：" + ev.title);
                StringBuilder sb = new StringBuilder();
                for (String user : eventManager.getRegistrants(ev.id)) {
                    sb.append(user).append("\n");
                }
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
        });

        exportCSV.setOnAction(e -> {
            int idx = list.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                Event ev = eventManager.getEvents().get(idx);
                List<String> users = eventManager.getRegistrants(ev.id);
                try (FileWriter writer = new FileWriter("registration_" + ev.id + ".csv")) {
                    writer.write("EventID,Title,User\n");
                    for (String user : users) {
                        writer.write(ev.id + "," + ev.title + "," + user + "\n");
                    }
                    showAlert("已匯出至 registration_" + ev.id + ".csv");
                } catch (IOException ex) {
                    showAlert("匯出失敗！");
                    ex.printStackTrace();
                }
            }
        });

        logout.setOnAction(e -> sceneManager.showLoginScene());

        VBox layout = new VBox(10, new Label("目前所有活動"), list, create, edit, delete, viewReg, exportCSV, logout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("view-layout");

        Scene scene = new Scene(layout, 550, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }

    private void updateList(ListView<String> list) {
        list.getItems().clear();
        for (Event ev : eventManager.getEvents()) {
            int reg = eventManager.getRegistrants(ev.id).size();
            String status = ev.getStatus(reg);
            list.getItems().add(ev.toString() + " [狀態: " + status + "]");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
