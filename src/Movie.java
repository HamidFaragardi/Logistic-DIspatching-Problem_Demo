import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Movie {
    public String title;
    public Date release_date;

    public Movie(String title, String release_date) {
        this.title = title;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date date = formatter.parse(release_date);
            this.release_date = new Date(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Movie(String title, Date release_date) {
        this.title = title;
        this.release_date = release_date;
    }
}
