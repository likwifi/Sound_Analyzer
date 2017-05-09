package ui.utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by maslanyan on 05.05.2017.
 */

public class Picker extends Component {
    private File selectedDir = null;

    public Picker(File initialDir) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
//        JFileChooser chooser = new JFileChooser();
        JFileChooser j = new JFileChooser();
        j.setCurrentDirectory(initialDir);
        j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES/*OPEN_DIALOG*//*DIRECTORIES_ONLY*/);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Wav or Image file", "*.jpg", ".jpg", "jpg", "png", "wav");
        j.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return f.isDirectory() ? true : name.endsWith(".wav") || name.endsWith(".jpg") || name.endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "Wav or Image file";
            }
        });
//            j.
        int res = j.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            if (j.getSelectedFile().isDirectory()) {
                this.selectedDir = j.getSelectedFile();
            }
        }
    }

    public File getSelectedDir() {
        return selectedDir;
    }
}
