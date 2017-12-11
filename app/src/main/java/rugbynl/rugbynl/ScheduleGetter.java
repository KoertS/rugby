package rugbynl.rugbynl;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASE on 6-9-17.
 */
// Title AsyncTask
public class ScheduleGetter {
    private List<Match> matches;
    private String team;
    String url;

    public List<Match> getSchedule(String url, String team){
        this.team = team;
        this.url = url;
        new getScheduleTask();
        return matches;
    }

    private class getScheduleTask extends AsyncTask<Void, Void, Void>{
        private String title;

        public getScheduleTask() {
            super();
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Create (loading) dialog


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
                matches = addTeamMatches(team, rows);
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
//            TextView titleTextView = (TextView) findViewById(R.id.title_textview);
//            titleTextView.setText(title);
            // Close dialog
            // fill table
            //  fillTable(matches);
        }
    }
}