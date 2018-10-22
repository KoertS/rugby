package rugbynl.rugbynl;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // Context
    private Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "pref";
    private static final String MATCHES = "matches";

    public MySharedPreference(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveMatches(String scoreString) {
        editor.putString(MATCHES, scoreString);
        editor.commit();
        System.out.println("Matches saved");
    }

    public String getMatches() {
        System.out.println("Matches loaded");
        return pref.getString(MATCHES, "");
    }
}