package rugbynl.rugbynl;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ASE on 12-12-17.
 */
// https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private List<Match> matches;

    public MatchAdapter(List<Match> matches) {
        this.matches = matches;
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        MatchViewHolder mvh = new MatchViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MatchViewHolder matchViewHolder, int i) {
        matchViewHolder.homeTeam.setText(matches.get(i).getHomeTeam());
        matchViewHolder.awayTeam.setText(matches.get(i).getAwayTeam());
        matchViewHolder.time.setText(matches.get(i).getTime());
        DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        matchViewHolder.date.setText(dataFormat.format(matches.get(i).getDate()));
        matchViewHolder.location.setText(matches.get(i).getLocation());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView homeTeam;
        TextView awayTeam;
        TextView time;
        TextView date;
        TextView location;

        MatchViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardView);
            homeTeam = (TextView)itemView.findViewById(R.id.homeTeam);
            awayTeam = (TextView)itemView.findViewById(R.id.awayTeam);
            time = (TextView) itemView.findViewById(R.id.time);
            date = (TextView) itemView.findViewById(R.id.date);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }
}
