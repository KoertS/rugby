package rugbynl.rugbynl;

import java.util.List;

/**
 * Created by ASE on 6-9-17.
 */
public class ScheduleGetter {
    private List<Match> matches;
    private String team;
    String url;

    public List<Match> getSchedule(String url, String team){
        this.team = team;
        this.url = url;
       // new getScheduleTask();
        return matches;
    }


}