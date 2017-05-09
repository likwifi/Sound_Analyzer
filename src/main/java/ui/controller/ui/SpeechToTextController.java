package ui.controller.ui;

import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.*;
import ui.*;

import java.io.File;

/**
 * Created by Minas on 3/4/2017.
 */
public class SpeechToTextController {
    private Stage stage;

    public SpeechToTextController() {

    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showSelectAudioDialog(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
//        fileChooser.setInitialDirectory(file);
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Wav audio file", "*.wav"));
        File selectedDir = fileChooser.showOpenDialog(this.stage);

        if (selectedDir != null && selectedDir.exists()) {
            ui.Popup.showMessage("ay ke ban", this.stage, false);
            System.out.println(selectedDir.getAbsolutePath());
        }
    }
}
