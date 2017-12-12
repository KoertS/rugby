package rugbynl.rugbynl;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String urlObelix1 = "http://www.erugby.nl/pub/nrb/2017-2018/2e_Klasse_Heren_Zuid/index.htm";
    private String urlObelix2 = "http://www.erugby.nl/pub/nrb/2017-2018/4e_Klasse_Heren_Zuid_-_Oost/index.htm";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);



        // Locate the Buttons in activity_main.xml
        Button obelix1 = (Button) findViewById(R.id.obelix1);
        Button obelix2 = (Button) findViewById(R.id.obelix2);

        // Capture button click
        obelix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] scheduleParams = {urlObelix1, "obelix"};
                new getSchedule().execute(scheduleParams);
            }
        });

        obelix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] scheduleParams = {urlObelix2, "obelix"};
                new getSchedule().execute(scheduleParams);
            }
        });

    }

    public void fillTable(List<Match> matches) {
        String[] listItems = new String[matches.size()];
        for(int i = 0; i < matches.size(); i++){
            Match match = matches.get(i);
            listItems[i] = match.getOpponent();
        }

        MatchAdapter adapter = new MatchAdapter(this, matches);
        mListView.setAdapter(adapter);
    }



    /**
     * Retrieves schedule. String params: url, team
     */
    private class getSchedule extends AsyncTask<String, Void, Void> {
        private String title;
        private ProgressDialog dialog;
        private List<Match> matches;

        public getSchedule() {
            dialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create (loading) dialog
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            String team = params[1];
            try {
                // Connect to the website
                Document doc = Jsoup.connect(url).get();
                // Get the html document title
                title = doc.title();

                // http://stackoverflow.com/questions/24772828/how-to-parse-html-table-using-jsoup
                Elements table = doc.select(".results"); // select the table with schedule and results
                Elements rows = table.select("tr");

                matches = addTeamMatches(team, rows);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        private List<Integer> getHeaderIndexes(Elements rows) {
            List<Integer> headerIndex = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).select("td").size() < 2) { // if collum size is 1 then it is header
                    headerIndex.add(i);
                }
            }
            return headerIndex;
        }

        private List<Match> addTeamMatches(String team, Elements rows) {
            List<Integer> headerIndexes = getHeaderIndexes(rows);
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++) { // loop through all matches
                if (rows.get(i).text().toLowerCase().contains(team)) { // only pick matches that contain team
                    Match match = new Match(team);
                    int indexOfHeader = getMatchingHeader(i, headerIndexes);

                    String dateString = rows.get(indexOfHeader).select("td").text();
                    String[] dateSplit = dateString.split(" ");

                    String day = String.format("%02d", Integer.parseInt(dateSplit[1])); // force length to 2 by adding 0 in front
                    String month = dateSplit[2];
                    String year = dateSplit[3];
                    String date = day + " " + month + " " + year;
                    match.setDate(date);

                    Elements cols = rows.get(i).select("td");
                    match.setTime(cols.get(0).text());
                    match.setHomeTeam(cols.get(2).text());
                    match.setAwayTeam(cols.get(3).text());
                    match.setScore(cols.get(4).text());
                    match.setOpponent();

                    matches.add(match);
                }
            }
            return matches;
        }

        /**
         * This function returns first smaller number in list given a number
         *
         * @param number        Integer
         * @param headerIndexes List<Integer>
         * @return first number that is smaller than the given number
         */
        private int getMatchingHeader(int number, List<Integer> headerIndexes) {
            for (int i = headerIndexes.size() - 1; i > -1; i--) {
                if (headerIndexes.get(i) < number) {
                    return headerIndexes.get(i);
                }
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Void results) {
            // Display result:
            // Set title into TextView
            TextView titleTextView = (TextView) findViewById(R.id.title_textview);
            titleTextView.setText(title);

            // fill table
            fillTable(matches);


            // Close dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

}
