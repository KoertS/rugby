package rugbynl.rugbynl;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String urlObelix1 = "http://www.erugby.nl/pub/nrb/2017-2018/2e_Klasse_Heren_Zuid/index.htm";
    private String urlObelix2 = "http://www.erugby.nl/pub/nrb/2017-2018/4e_Klasse_Heren_Zuid_-_Oost/index.htm";
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

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

                    match.setDate(getDate(i, headerIndexes, rows));

                    Elements cols = rows.get(i).select("td");
                    match.setTime(cols.get(0).text());
                    match.setLocation(cols.get(1).text());
                    match.setHomeTeam(cols.get(2).text());
                    match.setAwayTeam(cols.get(3).text());
                    match.setScore(cols.get(4).text());
                    match.setOpponent();

                    matches.add(match);
                }
            }
            return matches;
        }

        private Date getDate(int i, List<Integer> headerIndexes, Elements rows) {
            int indexOfHeader = getMatchingHeader(i, headerIndexes);

            String dateString = rows.get(indexOfHeader).select("td").text();
            String[] dateSplit = dateString.split(" ");

            String day = String.format("%02d", Integer.parseInt(dateSplit[1])); // force length to 2 by adding 0 in front
            String month = monthToNumber(dateSplit[2]);
            String year = dateSplit[3];

            String strDate = day + "/" + month + "/" + year;
            // Convert the date string to Date object
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = dateFormat.parse(strDate);
            } catch (ParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
            }
            return date;
        }

        private String monthToNumber(String month) {
            switch (month.toLowerCase()){
                case "januari":
                    return "01";
                case "februari":
                    return "02";
                case "maart":
                    return "03";
                case "april":
                    return "04";
                case "mei":
                    return "05";
                case "juni":
                    return "06";
                case "juli":
                    return "07";
                case "augustus":
                    return "08";
                case "september":
                    return "09";
                case "oktober":
                    return "10";
                case "november":
                    return "11";
                case "december":
                    return "12";
                default:
                    return "00";
            }
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
