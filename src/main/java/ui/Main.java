package ui;

import javafx.scene.image.Image;
import org.opencv.core.Core;
import ui.controller.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    static {
        String libName = Core.NATIVE_LIBRARY_NAME;
        if (System.getProperties().getProperty("java.vm.name").indexOf("64") > -1) {
            libName += "x64";
        } else {
            libName += "x86";
        }
        System.loadLibrary(libName);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xml/main.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Sound Analyzer");
        primaryStage.setScene(new Scene(root, 1024, 768));
//        primaryStage.setScene(new Scene(root, 620, 480));
        primaryStage.getIcons().add(new Image(getClass().getResource("/icons/iconic-retina.png").toString()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
