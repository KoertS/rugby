package rugbynl.rugbynl;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String url;
    String urlObelix1 = "http://www.erugby.nl/pub/nrb/2017-2018/2e_Klasse_Heren_Zuid/index.htm";
    String urlObelix2 = "http://www.erugby.nl/pub/nrb/2017-2018/4e_Klasse_Heren_Zuid_-_Oost/index.htm";
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Locate the Buttons in activity_main.xml
        Button obelix1 = (Button) findViewById(R.id.obelix1);
        Button obelix2 = (Button) findViewById(R.id.obelix2);


        // Capture button click
        obelix1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = urlObelix1;
                new getSchedule().execute();
            }
        });

        obelix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = urlObelix2;
                new getSchedule().execute();
            }
        });

    }
/*
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

        for (int i = 1; i < matches.size(); i+=2) { // fill table
            TableRow tbrow = new TableRow(this);

        //    String dateRaw = matches.get(i-1).select("td").text();
            String[] dateSplit = dateRaw.split(" ");
            String date = dateSplit[1] + " " + dateSplit[2].substring(0,3) + ".";
       //     Element row = matches.get(i);
            Elements cols = row.select("td");
            String time = cols.get(0).text();
            String home = shorten(cols.get(2).text());
            String guest = shorten(cols.get(3).text());
            String score = cols.get(4).text();

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
*/
    /**
     * Places spaces around string
     * @param s, string
     * @return " " + string + " "
     */
    private String space(String s) {
        return " " + s + " ";
    }

    /**
     * Shortens a string if above certain length
     * @param s, string
     * @return string shorten then maxLength
     */
    private String shorten(String s) {
        int maxLength=13;
        if(s.length() < maxLength) return s;
        s = s.substring(0, maxLength);
        return s;
    }

    // Title AsyncTask
    private class getSchedule extends AsyncTask<Void, Void, Void>{
        private String title;
        private List<Match> matches;
        private String team = "obelix";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Create (loading) dialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the website
                Document doc = Jsoup.connect(url).get();
                // Get the html document title
                title = doc.title();

                // http://stackoverflow.com/questions/24772828/how-to-parse-html-table-using-jsoup
                Elements table = doc.select(".results"); // select the table with schedule and results
                Elements rows = table.select("tr");
                List<Integer> headerIndexes = getHeaderIndexes(rows);
                addTeamMatches(team, rows);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private List<Integer>  getHeaderIndexes(Elements rows) {
            List<Integer> headerIndex = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++){
                if(rows.get(i).select("td").size() < 2){ // if collum size is 1 then it is header
                    headerIndex.add(i);
                }
            }
            return headerIndex;
        }

        private List<Match> addTeamMatches(String team, Elements rows) {
            List<Integer> headerIndexes = getHeaderIndexes(rows);
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < rows.size(); i++){ // loop through all matches
                if(rows.get(i).text().toLowerCase().contains(team)){ // only pick matches that contain team
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
         * @param number Integer
         * @param headerIndexes List<Integer>
         * @return first number that is smaller than the given number
         */
        private int getMatchingHeader(int number, List<Integer> headerIndexes) {
            for(int i = headerIndexes.size()-1; i > -1;i--){
                if(headerIndexes.get(i) < number){
                    return headerIndexes.get(i);
                }
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Void results){
            // Set title into TextView
            TextView titleTextView = (TextView) findViewById(R.id.title_textview);
            titleTextView.setText(title);
            // Close dialog
            mProgressDialog.dismiss();
            // fill table
          //  fillTable(matches);
        }
    }



}
