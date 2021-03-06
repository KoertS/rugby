package rugbynl.rugbynl;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by ASE on 6-9-17.
 */
public class Match extends ListItem implements Comparable<Match> {
    private String team;
    private Date date = new Date();
    private String homeTeam = "";
    private String awayTeam = "";
    private String time = "";
    private String score = "";
    private String opponent = "";
    private String location = "";
    private Boolean homeGame;

    public Match(String team) {
        this.team = team;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

        public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setOpponent() {
        System.out.println(homeTeam + " = " + team + "    " + homeTeam.contains(team));
        if (homeTeam.toLowerCase().contains(team.toLowerCase())) {
            opponent = awayTeam;
            homeGame = true;
        } else {
            opponent = homeTeam;
            homeGame = false;
        }
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getScore() {
        return score;
    }

    public String getOpponent() {
        return opponent;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public String getHomeOrAway() {
        return homeGame ? "home": "away";
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int compareTo(@NonNull Match other) {
        return date.compareTo(other.getDate());
    }
}
