package multiplex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author TFEJER
 */
public class Model {

    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/multiplexDB";
    private static final String USERNAME = "elteuser";
    private static final String PASSWORD = "1234";
    private final String LISTMOVIES_QUERY
            = "SELECT title, country, dubbed, director, playtime, maxPlays, rating FROM movies";
    private final String LISTROOMS_QUERY = "SELECT * FROM rooms";
    private final String ROOMSIZE_QUERY = "SELECT rooms.numOfRows, rooms.numOfColumns "
            + "FROM rooms INNER JOIN ";
    private String listShowsQuery = "SELECT movies.title, showID, startTime, roomID "
            + "FROM shows INNER JOIN movies ON shows.movieID = movies.movieID";

    private ResultSetTableModel tableModelMovies;
    private ResultSetTableModel tableModelShows;
    private ResultSetTableModel tableModelSelectedRoom;
    private ResultSetTableModel tableModelRoomsTimes;

    private HashMap<Integer, JLabel[][]> bookedSeats = new HashMap(); //show ID+booked seats

    /**
     * Executes the SQL query for the movies and for the shows.
     */
    public Model() {
        try {
            queryMoviesList();
            queryShowsList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);

            // ensure database connection is closed
            tableModelMovies.disconnectFromDatabase();
            tableModelShows.disconnectFromDatabase();

            System.exit(1); // terminate application
        }
    }

    /**
     * Adds a showID with the booked seats for it.
     *
     * @param showID the identifier of the selected show.
     * @param bookedSeats the JLabel matrix that holds the booked seats information.
     */
    public void addBookedSeats(int showID, JLabel[][] bookedSeats) {
        this.bookedSeats.put(showID, bookedSeats);
    }

    public HashMap<Integer, JLabel[][]> getBookedSeats() {
        return bookedSeats;
    }

    boolean saveBookedSeats(int showID, int rowNum, int columnNum, char status) {
        // SQL STATEMENT
        String sql = "INSERT INTO seats(showID, rowNum, columnNum, status)"
                + " VALUES(" + showID + ", " + rowNum + "," + columnNum + ",'" + status + "')";

        try {
            // GET CONNECTION
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);

            s.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    DefaultTableModel getBookedSeats(int selectedShowID) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("seatID");
        tableModel.addColumn("showID");
        tableModel.addColumn("row");
        tableModel.addColumn("column");
        tableModel.addColumn("status");

        // SQL STATEMENT
        String sql = "SELECT * FROM seats WHERE showID = " + selectedShowID;

        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String seatID = rs.getString(1);
                String showID = rs.getString(2);
                String row = rs.getString(3);
                String column = rs.getString(4);
                String status = rs.getString(5);

                tableModel.addRow(new String[]{seatID, showID, row, column, status});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Sets the SQL query with the given query parameter.
     *
     * @param filter the parameter to query the database with.
     */
    public void setListShowsQuery(String filter) {
        listShowsQuery.concat(filter);
        try {
            queryShowsList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);

            // ensure database connection is closed
            tableModelMovies.disconnectFromDatabase();
            tableModelShows.disconnectFromDatabase();

            System.exit(1); // terminate application
        }
    }

    private void queryShowsList() throws SQLException {
        tableModelShows = new ResultSetTableModel(DATABASE_URL, USERNAME, PASSWORD, listShowsQuery);
    }

    private void queryMoviesList() throws SQLException {
        tableModelMovies = new ResultSetTableModel(DATABASE_URL, USERNAME, PASSWORD, LISTMOVIES_QUERY);
    }

    void queryRoomsStartTimes(String query) throws SQLException {
        tableModelRoomsTimes = new ResultSetTableModel(DATABASE_URL, USERNAME, PASSWORD, query);
    }

    public ResultSetTableModel getTableModelMovies() {
        return tableModelMovies;
    }

    public ResultSetTableModel getTableModelShows() {
        return tableModelShows;
    }

    public ResultSetTableModel getTableModelRoomsTimes() {
        return tableModelRoomsTimes;
    }

    public String getListShowsQuery() {
        return listShowsQuery;
    }

    public ResultSetTableModel getTableModelSelectedRoom() {
        return tableModelSelectedRoom;
    }

    public void setTableModelSelectedRoom(String selectedRoomQuery) throws SQLException {
        this.tableModelSelectedRoom = new ResultSetTableModel(
                DATABASE_URL, USERNAME, PASSWORD, selectedRoomQuery
        );
    }

    public void setTableModelListShows(String listShowsQuery) throws SQLException {
        this.tableModelShows = new ResultSetTableModel(
                DATABASE_URL, USERNAME, PASSWORD, listShowsQuery
        );
    }

    public boolean deleteShow(int showID) {
        // SQL statement
        String sql = "DELETE FROM shows WHERE showID = " + Integer.toString(showID);

        try {
            // get connection
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // statement
            PreparedStatement s = con.prepareStatement(sql);

            // execute
            s.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    DefaultTableModel getAllRooms() {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("roomID");
        tableModel.addColumn("roomName");
        tableModel.addColumn("numOfRows");
        tableModel.addColumn("numOfColumns");

        // SQL STATEMENT
        String sql = "SELECT * FROM rooms";

        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String roomID = rs.getString(1);
                String roomName = rs.getString(2);
                String numOfRows = rs.getString(3);
                String numOfColumns = rs.getString(4);

                tableModel.addRow(new String[]{roomID, roomName, numOfRows, numOfColumns});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getRoomID(String roomName) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("roomID");

        // SQL STATEMENT
        String sql = "SELECT roomID FROM rooms WHERE roomName = '" + roomName + "'";

        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String roomID = rs.getString(1);

                tableModel.addRow(new String[]{roomID});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getMovieID(String movieTitle) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("movieID");

        // SQL STATEMENT
        String sql = "SELECT movieID FROM movies WHERE title = '" + movieTitle + "'";

        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String movieID = rs.getString(1);

                tableModel.addRow(new String[]{movieID});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getPastShows(String query) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("maxPlays");
        tableModel.addColumn("movieID");
        tableModel.addColumn("startTime");

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String maxPlays = rs.getString(1);
                String movieID = rs.getString(2);
                String startTime = rs.getString(3);

                tableModel.addRow(new String[]{maxPlays, movieID, startTime});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getSynopsis(String movieTitle) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("synopsis");
        String query = "SELECT synopsis FROM movies WHERE title = '" + movieTitle + "'";

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String synopsis = rs.getString(1);

                tableModel.addRow(new String[]{synopsis});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getRating(String movieTitle) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("rating");
        String query = "SELECT rating FROM movies WHERE title = '" + movieTitle + "'";

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String synopsis = rs.getString(1);

                tableModel.addRow(new String[]{synopsis});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getAllShows(String selectedRoomName) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("startTime");
        tableModel.addColumn("roomID");
        tableModel.addColumn("roomName");
        tableModel.addColumn("playTime");
        tableModel.addColumn("movieID");

        String query = "SELECT * FROM"
                + "(SELECT s.startTime, s.roomID, s.movieID, r.roomName, m.playTime "
                + "FROM shows AS s "
                + "INNER JOIN rooms AS r "
                + "ON s.roomID = r.roomID "
                + "INNER JOIN movies AS m "
                + "ON s.movieID = m.movieID) AS a "
                + "WHERE a.roomName = '" + selectedRoomName + "'";

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String startTime = rs.getString(1);
                String roomID = rs.getString(2);
                String roomName = rs.getString(3);
                String playTime = rs.getString(4);
                String movieID = rs.getString(5);

                tableModel.addRow(new String[]{startTime, roomID, roomName, playTime, movieID});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    DefaultTableModel getShows(String selectedMovie, String selectedDay) {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("startTime");

        if (selectedDay.length() == 9) {
            char lastChar = selectedDay.charAt(selectedDay.length() - 1);
            String newChars = "0" + lastChar;
            selectedDay = selectedDay.substring(0, selectedDay.length() - 1);
            selectedDay = selectedDay.concat(newChars);
        }

        String query = "SELECT startTime FROM "
                + "(SELECT s.startTime, m.playTime, s.roomID, s.movieID, m.title "
                + "FROM shows s "
                + "INNER JOIN movies m "
                + "ON s.movieID = m.movieID) a "
                + "WHERE a.title = '" + selectedMovie + "' "
                + "AND CAST(DATE(a.startTime) AS VARCHAR(10)) LIKE '"
                + selectedDay + "%'";

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String startTime = rs.getString(1);

                tableModel.addRow(new String[]{startTime});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    boolean addNewShow(int movieID, String startTime, int roomID) {
        // SQL STATEMENT
        String sql = "INSERT INTO shows(movieID, startTime, roomID)"
                + " VALUES(" + movieID + ", '" + startTime + "'," + roomID + ")";

        try {
            // GET COONECTION
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(sql);

            s.execute();

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    DefaultTableModel getAllMovieTitles() {
        // ADD COLUMNS TO TABLE MODEL
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("title");
        String query = "SELECT title FROM movies";

        // SQL STATEMENT
        try {
            Connection con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            // PREPARED STMT
            PreparedStatement s = con.prepareStatement(query);
            ResultSet rs = s.executeQuery();

            // LOOP THRU GETTING ALL VALUES
            while (rs.next()) {
                // GET VALUES
                String title = rs.getString(1);

                tableModel.addRow(new String[]{title});
            }

            return tableModel;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
