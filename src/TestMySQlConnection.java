import java.sql.*;
import java.util.ArrayList;

public class TestMySQlConnection {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            String databaseName = "dvd_collection";
            int port = 3306;
            String ip = "127.0.0.1";
            String userName = "root";
            String password = "!Hamid5015951";

            String url = "jdbc:mysql://" + ip + ":" + port + "/" + databaseName;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, userName, password);

            ArrayList<Movie> movies = readTableRows(connection);
            writeMovieToTable(connection, new Movie("Test", "23-12-2020"));
                writeMovieToTable(connection, new Movie("Test", "23-12-2020"));
        } catch (SQLException | ClassNotFoundException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Movie> printResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        ArrayList<Movie> movies = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("movie_id");
            String title = resultSet.getString("title");
            Date releaseDate = resultSet.getDate("release_date");
            System.out.println("id: " + id);
            System.out.println("title: " + title);
            System.out.println("releaseDate: " + releaseDate);
            movies.add(new Movie(title, releaseDate));
        }
        return movies;
    }

    private static ArrayList<Movie> readTableRows(Connection connection) throws SQLException {
        // Statements allow to issue SQL queries to the database
        Statement statement = connection.createStatement();
        // Result set get the result of the SQL query
        ResultSet resultSet = statement.executeQuery("select * from movies");
        return printResultSet(resultSet);
    }

    private static void writeMovieToTable(Connection connection, Movie item) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into dvd_collection.movies (title, release_date) values (?, ?)");

        // Parameters start with 1
        preparedStatement.setString(1, item.title);
        preparedStatement.setDate(2, item.release_date);
        preparedStatement.executeUpdate();
    }

}
