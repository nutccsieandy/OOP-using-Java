import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StudentView {
    private final Stage stage;
    private final UserSession session;
    private final EventManager eventManager;
    private final SceneManager sceneManager;

    public StudentView(Stage stage, UserSession session, EventManager eventManager, SceneManager sceneManager) {
        this.stage = stage;
        this.session = session;
        this.eventManager = eventManager;
        this.sceneManager = sceneManager;
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("view-layout");

        TextField searchField = new TextField();
        searchField.setPromptText("輸入關鍵字或日期篩選");
        searchField.getStyleClass().add("text-field");

        ListView<String> listView = new ListView<>();
        updateList(listView, "");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateList(listView, newVal));

        Button register = new Button("報名活動");
        Button cancel = new Button("取消報名");
        Button view = new Button("查看已報名");
        Button logout = new Button("登出");

        register.getStyleClass().add("primary-button");
        cancel.getStyleClass().add("danger-button");
        view.getStyleClass().add("primary-button");
        logout.getStyleClass().add("danger-button");

        register.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                Event ev = eventManager.getFilteredEvents(searchField.getText()).get(idx);
                int regCount = eventManager.getRegistrants(ev.id).size();
                String status = ev.getStatus(regCount);
                if (status.equals("額滿") || status.equals("已結束")) {
                    showAlert("此活動無法報名！");
                    return;
                }
                eventManager.register(session.getCurrentUser(), ev.id);
                showAlert("報名成功！");
                updateList(listView, searchField.getText());
            }
        });

        cancel.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                Event ev = eventManager.getFilteredEvents(searchField.getText()).get(idx);
                eventManager.cancel(session.getCurrentUser(), ev.id);
                updateList(listView, searchField.getText());
            }
        });

        view.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("已報名活動");
            alert.setHeaderText("您報名的活動如下：");
            StringBuilder sb = new StringBuilder();
            for (Event ev : eventManager.getEvents()) {
                if (eventManager.getUserRegistrations(session.getCurrentUser()).contains(ev.id)) {
                    sb.append(ev.toString()).append("\n");
                }
            }
            alert.setContentText(sb.toString());
            alert.showAndWait();
        });

        logout.setOnAction(e -> sceneManager.showLoginScene());

        root.getChildren().addAll(new Label("活動搜尋與報名"), searchField, listView, register, cancel, view, logout);
        Scene scene = new Scene(root, 550, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }

    private void updateList(ListView<String> list, String keyword) {
        list.getItems().clear();
        for (Event ev : eventManager.getFilteredEvents(keyword)) {
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
