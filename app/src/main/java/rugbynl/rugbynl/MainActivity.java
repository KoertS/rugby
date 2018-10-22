package rugbynl.rugbynl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        fillTable(matches);
    }

    public void fillTable(List<Match> matches) {
        List<Match> upcomingMatches= new ArrayList<Match>();
        for(int i = 0; i < matches.size(); i++){
            // filter out previous matches
            Match match = matches.get(i);
            DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");

            System.out.println("test: comparing match date: " + dataFormat.format(match.getDate()));
            System.out.println("test: with current date: " + dataFormat.format(new Date()));
            System.out.println("test: " + match.getDate().compareTo(new Date()));
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
