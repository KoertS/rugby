package rugbynl.rugbynl;

/**
 * Created by ASE on 6-9-17.
 */
public class Match {

    private String date;
    private String home;
    private String guest;
    private String time;
    private String score;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {this.time = time;}

    public void setHome(String home) {this.home = home; }

    public void setGuest(String guest) {this.guest = guest;}

    public void setScore(String score) { this.score = score; }
}
