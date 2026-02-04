package ui.obj;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Minas on 5/7/2017.
 */
public class PhonemeList {
    private final List<Phoneme> phonemes = new ArrayList<>();

    public PhonemeList(File phonemeFile) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(phonemeFile))) {
            String line;
            int lineNumber = 0;
            while ((line = in.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] fields = line.split("\\s+");
                if (fields.length < 3)
                    throw new IOException("Invalid phoneme annotation at line " + lineNumber);
                try {
                    phonemes.add(new Phoneme(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), fields[2]));
                } catch (NumberFormatException exception) {
                    throw new IOException("Invalid phoneme sample index at line " + lineNumber, exception);
                }
            }
        }
    }

    public List<Phoneme> getPhonemesList() {
        return Collections.unmodifiableList(this.phonemes);
    }
}
