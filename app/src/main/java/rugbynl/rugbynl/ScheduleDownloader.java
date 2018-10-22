package rugbynl.rugbynl;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ScheduleDownloader extends AsyncTask<String, Void, Void> {
    private final MatchesReaderWriter matchesReaderWriter;
    // Team name, used to select matches in which obelix plays
    private String team = "obelix";
    // Urls of the schedules
    private String urlObelixHeren1 = "https://www.erugby.nl/pub/nrb/2018-2019/2e_klasse_Heren_Zuid__1e_fase/index.htm";
    private String urlObelixHeren2 = "https://www.erugby.nl/pub/nrb/2018-2019/4e_klasse_Heren_Zuid_-_Oost__1e_fase/index.htm";
    private String urlObelixDames1 = "https://www.erugby.nl/pub/nrb/2018-2019/1e_klasse_Dames__1e_fase/index.htm";
    private String[] urlObelixTeams = new String[]{urlObelixHeren1, urlObelixHeren2, urlObelixDames1};

    private List<Match> matches = new ArrayList<Match>();
    private MainActivity mainActivity;
    private String title;
    private ProgressDialog dialog;

    public ScheduleDownloader(MatchesReaderWriter matchesReaderWriter, MainActivity mainActivity) {
        this.matchesReaderWriter = matchesReaderWriter;
        this.mainActivity = mainActivity;
        dialog = new ProgressDialog(mainActivity);

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
        for (String urlTeam : urlObelixTeams) {

            try {
                // Connect to the website
                Document doc = Jsoup.connect(urlTeam).get();
                // Get the html document title
                title = doc.title();

                // http://stackoverflow.com/questions/24772828/how-to-parse-html-table-using-jsoup
                Elements table = doc.select(".results"); // select the table with schedule and results
                Elements rows = table.select("tr");

                addTeamMatches(matches, rows);
                System.out.println("test: " + urlTeam + " " + matches.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * This function returns list of index of the header. Header contains date of the matches
     * @param rows
     * @return List of indexes of headers
     */
    private List<Integer> getHeaderIndexes(Elements rows) {
        List<Integer> headerIndex = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).select("td").size() < 2) { // if collum size is 1 then it is header
                headerIndex.add(i);
            }
        }
        return headerIndex;
    }

    private List<Match> addTeamMatches(List<Match> matches, Elements rows) {
        List<Integer> headerIndexes = getHeaderIndexes(rows);

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
        //TODO: make a enum for months
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
        matchesReaderWriter.saveData(matches);
        mainActivity.setMatches(matches);
        // Close dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public List<Match> getMatches(){
        return matches;
    }
}
