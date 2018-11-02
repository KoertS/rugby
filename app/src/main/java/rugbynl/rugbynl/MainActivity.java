package rugbynl.rugbynl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private MatchesReaderWriter matchesReaderWriter;
    private List<Match> matches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchesReaderWriter = new MatchesReaderWriter(getApplicationContext());
        rv = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        matches = matchesReaderWriter.loadMatches();
        if(matches.isEmpty()){
            ScheduleDownloader scheduleDownloader = new ScheduleDownloader(matchesReaderWriter, this);
            scheduleDownloader.execute();
        }
        Collections.sort(matches);
        fillTable(matches);
    }

    public void fillTable(List<Match> matches) {
        List<Match> upcomingMatches= new ArrayList<Match>();

        Date prevMatchDate = matches.get(0).getDate();
        Header header = new Header(prevMatchDate);
      //  upcomingMatches.add(header);
        for(int i = 0; i < matches.size(); i++){
            // filter out previous matches
            Match match = matches.get(i);

            // Check if next match in list is on the same day
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            if(!fmt.format(match.getDate()).equals(fmt.format(prevMatchDate))){
                // If match is on an other day than then the previous, then add header between previous match and match
                prevMatchDate = match.getDate();
                header = new Header(prevMatchDate);
                //upcomingMatches.add(header);
            }
            // Only display matches that still need to be played
            if(match.getDate().compareTo(new Date()) >= 0 ) {
                upcomingMatches.add(match);
            }
        }

        MatchAdapter adapter = new MatchAdapter(upcomingMatches);
        rv.setAdapter(adapter);
    }


    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
