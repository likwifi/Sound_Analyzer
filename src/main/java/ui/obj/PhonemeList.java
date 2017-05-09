package ui.obj;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minas on 5/7/2017.
 */
public class PhonemeList {
    private List<Phoneme> phonemes;

    public PhonemeList(File phonemeFile) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(phonemeFile));
        phonemes = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            String[] split = line.split(" ");
            phonemes.add(new Phoneme(Integer.valueOf(split[0]),Integer.valueOf(split[1]), split[2]));
        }
    }

    public List<Phoneme> getPhonemesList() {
        return this.phonemes;
    }
}
