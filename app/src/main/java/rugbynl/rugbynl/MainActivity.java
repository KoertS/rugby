package rugbynl.rugbynl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String url;
    String urlObelix1 = "http://www.erugby.nl/pub/nrb/2017-2018/2e_Klasse_Heren_Zuid/index.htm";
    String urlObelix2 = "http://www.erugby.nl/pub/nrb/2017-2018/4e_Klasse_Heren_Zuid_-_Oost/index.htm";

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
                url = urlObelix1;
                List<Match> matches = scheduleGetter.getSchedule(urlObelix1, "obelix");
                System.out.print(matches.get(0).getHome());
            }
        });

        obelix2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = urlObelix2;
                List<Match> matches = scheduleGetter.getSchedule(urlObelix2, "obelix");
                System.out.print(matches.get(0).getHome());
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





}
