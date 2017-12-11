package rugbynl.rugbynl;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Locate the Buttons in activity_main.xml
        Button obelix1 = (Button) findViewById(R.id.obelix1);
        Button obelix2 = (Button) findViewById(R.id.obelix2);

        final ScheduleGetter scheduleGetter = new ScheduleGetter();

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
        // http://stackoverflow.com/questions/18207470/adding-table-rows-dynamically-in-android
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        stk.removeAllViews();
        // add headers
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" Datum ");
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Tijd ");
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Thuis ");
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Gasten ");
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Uitslag ");
        tbrow0.addView(tv4);
        stk.addView(tbrow0);

        for (Match match : matches) { // fill table
            TableRow tbrow = new TableRow(this);
            String date = match.getDate();
            String time = match.getTime();
            String home = match.getHome();
            String guest = match.getGuest();
            String score = match.getScore();

            TextView t1v = new TextView(this);
            t1v.setText(space(date));
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(space(time));
            t2v.setGravity(Gravity.START);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(space(home));
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);
            t4v.setText(space(guest));
            tbrow.addView(t4v);

            TextView t5v = new TextView(this);
            t5v.setText(space(score));
            tbrow.addView(t5v);

            stk.addView(tbrow);
        }

    }

    /**
     * Places spaces around string
     *
     * @param s, string
     * @return " " + string + " "
     */
    private String space(String s) {
        return " " + s + " ";
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
                    Match match = new Match();
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
                    match.setHome(cols.get(2).text());
                    match.setGuest(cols.get(3).text());
                    match.setScore(cols.get(4).text());

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
            System.out.print(matches.get(0).getHome());
            // fill table
            fillTable(matches);


            // Close dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

}
