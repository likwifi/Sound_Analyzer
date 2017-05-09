package ui.obj;

/**
 * Created by Minas on 5/7/2017.
 */
public class Phoneme {
    public final int start;
    public final int end;
    public final String name;

    public Phoneme(int start, int end, String name) {
        this.start = start;
        this.end = end;
        this.name = name;
    }
}
