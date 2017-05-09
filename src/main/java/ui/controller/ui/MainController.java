package ui.controller.ui;

import audio.AudioWaveformReader;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ui.Popup;
import ui.controller.ImageController;
import ui.controller.SoundController;
import ui.obj.PhonemeList;
import ui.utils.Picker;
import ui.utils.UIUtils;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainController {
    @FXML
    public AnchorPane spectrogramPane;
    @FXML
    public Tab spectrogramTab;
    @FXML
    private TreeView treeView;
    @FXML
    private ToolBar audioToolbar;
    @FXML
    public ToolBar frequencyToolbar;

    public Stage stage;

    public AnchorPane topPane;
    public AnchorPane bottomPane;
    public TextArea phonemesDictionary;

    private File rootDir;
    public File selectedPath;

//    private String phonemesDir = "C:\\Users\\Minas\\Documents\\Tez\\PDA\\PDAm\\11k\\001";
//    private String phonemesDir = "C:\\Users\\Public\\Pictures\\Sample Pictures";
    private String phonemesDir = "C:\\Users\\Minas\\Documents\\Tez\\Sndfile";
    private String dictionaryDir = "C:\\Users\\Minas\\Documents\\Tez\\TIMIT CORPUS\\TIMIT\\DOC\\TIMITDIC.txt";
    private SoundController soundController = new SoundController(this);
    private ImageController imageController = new ImageController(null, this);
    public float Fps;

    public MainController() {
    }

    private void fillTree(File rootDir) {
        File[] files = rootDir.listFiles();
        TreeItem<String> root = new TreeItem<>(rootDir.getParentFile().getName());
        root.setExpanded(true);
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".wav") || name.endsWith(".png") || name.endsWith(".jpg")) {
                TreeItem<String> item = new TreeItem<String>(name);
                root.getChildren().add(item);
            }

        }
        if (root.getChildren().size() == 0) {
            Popup.showMessage("No image or audio files found", this.stage, true);
        }
        treeView.setRoot(root);
        treeView.setShowRoot(true);
    }

    @FXML
    private void handleTreeItemClick(Event e) throws IOException, UnsupportedAudioFileException {
        TreeItem<?> selectedItem = (TreeItem<?>) treeView.getSelectionModel().getSelectedItems().get(0);
        if (selectedItem == null)
            return;
        File path = new File(this.rootDir.getAbsolutePath() + "\\" + selectedItem.getValue());

        if (this.selectedPath != null && (/*this.selectedPath.equals(path) || */!this.selectedPath.isFile()))
            return;

        this.selectedPath = path;

        // trying to threat path as wav audio
        AudioInputStream selectedAudio =  null;
        try {
            selectedAudio = AudioWaveformReader.getAudio(this.selectedPath);
        } catch (Exception audioReadException) {}
        if (selectedAudio != null) {
            hideFrequencyToolbar();
            showAudioToolbar();
            spectrogramTab.setDisable(false);
            double[] audioData = AudioWaveformReader.readAudio(selectedAudio);
            Fps = selectedAudio.getFormat().getFrameRate();
            // reading audios phonemeFile
            String name = this.selectedPath.getName();
            PhonemeList phn = null;
            if (name.endsWith(".wav")) {
                String absPath = this.selectedPath.getAbsolutePath();
                File phonemeFile = new File(absPath.split(".wav")[0]+".phn");
                if (phonemeFile.exists()) {
                    phn = new PhonemeList(phonemeFile);
                }
            }
            soundController.setActive(audioData, phn);
            return;
        }

        // trying to threat path as an image
        BufferedImage img = null;
        try {
            img = ImageIO.read(this.selectedPath);
        } catch (Exception exception) {}
        if (img != null) {
            hideAudioToolbar();
            showFrequencyToolbar();
            spectrogramTab.setDisable(true);
            imageController.setActive(img);
            return;
        }
        Popup.showMessage("Please select image or .wav file", this.stage, true);
    }

    private void showAudioToolbar() {
        UIUtils.show(this.audioToolbar);
    }
    private void hideAudioToolbar() {
        UIUtils.hide(this.audioToolbar);
    }
    private void hideFrequencyToolbar() {
        UIUtils.hide(this.frequencyToolbar);
    }
    private void showFrequencyToolbar() {
        UIUtils.show(this.frequencyToolbar);
    }

    public void setFrequencyThreshold(ActionEvent actionEvent) {
        imageController.showSetThresholdDialog();
    }

    private File openDirectory() {
        File file = new File(phonemesDir);
        boolean awt = false;
        File selectedDir = null;
        if (awt) {
            Picker p = new Picker(file);
            selectedDir = p.getSelectedDir();
        } else {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            if (file.exists()) {
                directoryChooser.setInitialDirectory(file);
            }
            selectedDir = directoryChooser.showDialog(this.stage);
        }
        return selectedDir;
    }
    @FXML
    private void openFileHandler() {
        File selectedDir = openDirectory();
        if (selectedDir != null && selectedDir.exists()) {
            fillTree(selectedDir);
            this.rootDir = selectedDir;
        }
    }


    private boolean showEmptyTree = true;
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // initializing tree;
        if (showEmptyTree) {
            // show empty text
        } else {
            treeView.setShowRoot(true);
            File rootDir = new File(phonemesDir);
            if (rootDir.exists()) {
                fillTree(rootDir);
                this.rootDir = rootDir;
            }
        }

        // initialing dictionary file
        /*try {
            fillDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        hideAudioToolbar();
        hideFrequencyToolbar();
    }

    private void fillDictionary()  throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.dictionaryDir));
        StringBuilder sb = new StringBuilder();
        try {
            String line = br.readLine();
            while (line!=null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        phonemesDictionary.setText(sb.toString());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleAbout(ActionEvent actionEvent) {
        Popup.showMessage("Author: Minas Aslanyan\nMail: minas.aslanyan@gmail.com", this.stage, true);
    }

    public void handleExit(ActionEvent actionEvent) {
        this.stage.close();
    }

    public void playAudio(ActionEvent actionEvent) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        soundController.playAudio(actionEvent);
    }
    public void recognizeSpeech(ActionEvent actionEvent) throws IOException {
        soundController.recognizeSpeech(actionEvent);
    }

    public void spectrogramTabSelected(Event event) {
        if(((Tab) event.getTarget()).isSelected() && soundController.redrawSpectrogram) {
            soundController.plotSpectrogram();
        } else if (soundController.redrawFFT) {
            soundController.plotFFT();
        }
    }
}
