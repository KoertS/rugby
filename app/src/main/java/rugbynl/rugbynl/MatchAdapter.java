package rugbynl.rugbynl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ASE on 12-12-17.
 */
public class MatchAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Match> mDataSource;

    public MatchAdapter(Context context, List<Match> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_match, parent, false);

        // Get title element
        TextView opponentTextView =
                (TextView) rowView.findViewById(R.id.match_list_opponent);

        // Get subtitle element
        TextView dateTextView =
                (TextView) rowView.findViewById(R.id.match_list_date);

        // Get detail element
        TextView homeGameTextView =
                (TextView) rowView.findViewById(R.id.match_list_homeGame);


        //
        Match match = (Match) getItem(position);

        opponentTextView.setText(match.getOpponent());
        dateTextView.setText(match.getDate());
        homeGameTextView.setText(String.valueOf(match.isHomeGame()));

        return rowView;
    }
}
