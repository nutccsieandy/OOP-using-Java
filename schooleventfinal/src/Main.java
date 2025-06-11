import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        UserSession session = new UserSession();
        EventManager eventManager = new EventManager();
        SceneManager sceneManager = new SceneManager(primaryStage, session, eventManager);
        sceneManager.showLoginScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
