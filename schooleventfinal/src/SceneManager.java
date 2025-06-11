import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage stage;
    private final UserSession session;
    private final EventManager eventManager;

    public SceneManager(Stage stage, UserSession session, EventManager eventManager) {
        this.stage = stage;
        this.session = session;
        this.eventManager = eventManager;
    }

    public void showLoginScene() {
        Label label = new Label("請輸入帳號與密碼");
        label.getStyleClass().add("label-title");

        TextField username = new TextField();
        username.setPromptText("帳號");
        username.getStyleClass().add("text-field");

        PasswordField password = new PasswordField();
        password.setPromptText("密碼");
        password.getStyleClass().add("text-field");

        Button login = new Button("登入");
        login.getStyleClass().add("primary-button");

        Label msg = new Label();

        login.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText().trim();

            if (user.equals("student01") && pass.equals("1234")) {
                session.login(user, "student");
                new StudentView(stage, session, eventManager, this).show();
            } else if (user.equals("organizer01") && pass.equals("5678")) {
                session.login(user, "organizer");
                new OrganizerView(stage, session, eventManager, this).show();
            } else {
                msg.setText("登入失敗，請確認帳密");
            }
        });

        VBox box = new VBox(12, label, username, password, login, msg);
        box.setPadding(new Insets(30));
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("view-layout");

        Scene scene = new Scene(box, 420, 280);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("登入系統");
        stage.show();
    }
}
