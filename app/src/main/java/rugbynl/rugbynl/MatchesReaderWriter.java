package rugbynl.rugbynl;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MatchesReaderWriter {
    private MySharedPreference sharedPreference;
    Gson gson = new Gson();

    public MatchesReaderWriter(Context applicationContext) {
        sharedPreference = new MySharedPreference(applicationContext);
    }

    public void saveData(List<Match> matches) {
        //convert ArrayList object to String by Gson
        String jsonScore = gson.toJson(matches);

        //save to shared preference
        sharedPreference.saveMatches(jsonScore);
    }

     /**
     * Retrieving data from sharepref
     */
    public List<Match> loadMatches() {
        //retrieve data from shared preference
        String jsonScore = sharedPreference.getMatches();
        Type type = new TypeToken<List<Match>>(){}.getType();
        List<Match> matches = gson.fromJson(jsonScore, type);

        if (matches == null) {
            matches = new ArrayList<>();
        }
        return matches;
    }
}
