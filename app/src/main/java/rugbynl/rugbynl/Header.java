package rugbynl.rugbynl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Header extends ListItem {
    private String header;

    public Header(Date prevMatchDate){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        header = df.format(prevMatchDate);
    }

    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
}