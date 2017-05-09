package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Minas on 3/4/2017.
 */
public class Popup {
    public static Stage showMessage(String msg, Stage owner, boolean ok) {
        final Stage dialog = new Stage();
        int height = 50;
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);

        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.BASELINE_CENTER);

        // adding msg
        Text msgHolder = new Text(msg);
        msgHolder.setFont(Font.font(12));
        msgHolder.setTextAlignment(TextAlignment.CENTER);
        msgHolder.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        dialogVbox.getChildren().add(msgHolder);

        // adding ok button
        if (ok) {
            Button okBtn = new Button("OK");
            okBtn.setDefaultButton(true);
            okBtn.setOnAction(event -> dialog.close());
            dialogVbox.getChildren().add(okBtn);
            height += 50;
        }

        int width = Math.max(msg.length()*10, 200);
        Scene dialogScene = new Scene(dialogVbox, width, height);
        dialog.setScene(dialogScene);
        dialog.show();
        return dialog;
    }
}
