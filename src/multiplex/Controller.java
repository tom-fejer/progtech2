package multiplex;

import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author TFEJER
 */
public class Controller {

    private final View view;
    private final Model model;

    /**
     * Initializes the view and model objects, and adds <code>ActionListener</code> for buttons in
     * the view that need data from the model.
     *
     * @param v
     * @param m
     */
    public Controller(View v, Model m) {
        view = v;
        model = m;

        ResultSetTableModel tableModelMovies = model.getTableModelMovies();
        ResultSetTableModel tableModelShows = model.getTableModelShows();
        view.setMoviesTable(new JTable(tableModelMovies));
        view.setShowsTable(new JTable(tableModelShows));
        addShowsTableSorter();
        addListeners();

        // ensure database connection is closed when user quits application
        view.addWindowListener(new WindowAdapter() {
            // disconnect from database and exit when window has closed
            @Override
            public void windowClosed(WindowEvent event) {
                tableModelMovies.disconnectFromDatabase();
                System.exit(0);
            } // end method windowClosed
        } // end WindowAdapter inner class
        ); // end call to addWindowListener
    }

    private void addShowsTableSorter() {
        JTable showsTable = view.getShowsTable();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(showsTable.getModel());
        showsTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
    }

    private void addListeners() {
        addCreateShowBtnListener();
        addBookShowBtnListener();
        addDeleteShowBtnListener();
        addFinalizeBtnListener();
        addListShowsBtnListener();
        addMoviesTableListener();
        addNewShowBtnListener();
        addShowsTableListener();
        addSynopsisBtnListener();

        generateFilterText();
        addMoviesFilterListener();
        addRoomsFilterListener();
        addStartTimesFilterListener();
    }

    /**
     * Adds an <code>ActionListener</code> to the finalize button.
     */
    private void addFinalizeBtnListener() {
        view.getBtnFinalize().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.finalizeSeats();
                int selectedShowTableRow = view.getShowsTable()
                        .convertRowIndexToModel(view.getSelectedShowsTableRow());
                int selectedShowID = (Integer) model.getTableModelShows()
                        .getValueAt(selectedShowTableRow, 1);
                int rows = view.getSeatLabels().length;
                int cols = view.getSeatLabels()[0].length;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (view.getSeatLabels()[i][j].getBackground().equals(Color.RED)) {
                            model.saveBookedSeats(selectedShowID, i, j, 'Y');
                        }
                    }
                }
                model.addBookedSeats(selectedShowID, view.getSeatLabels());
                view.getCardLayout().show(view.getCardPanel(), "panelSelectionMenu");
            }
        });
    }

    /**
     * Adds an <code>ActionListener</code> to the createShow button.
     */
    private void addCreateShowBtnListener() {
        view.getBtnCreateShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.getSelectedMoviesTableRow() == -1) {
                    view.showMessage("Oops!", "Please select a movie first!");
                } else {
                    view.setSelectedStartDate(generateStartDates()[0]);
                    generateAvailableRoomsAndTimes();
                    updateStartTimes();
                    addRoomNamesListener();
                    addStartDatesListener();
                    addStartTimesListener();
                    view.getSelectedMovieTitleLabel().setText(view.getSelectedMovieTitle());
                    view.getCardLayout().show(view.getCardPanel(), "panelCreateShow");
                }
            }

        });
    }

    private void generateAvailableRoomsAndTimes() throws IllegalStateException {
        String[] roomNames = getAllRooms();
        String[] startDates = generateStartDates();
        String[] startTimes = generateStartTimes();
        view.updateNewShow(roomNames, startDates, startTimes);
    }

    private String[] getAllRooms() {
        DefaultTableModel tableModel = model.getAllRooms();
        int rowCount = tableModel.getRowCount();

        String[] rooms = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            rooms[i] = tableModel.getValueAt(i, 1).toString();
        }
        return rooms;
    }

    private String[] generateStartTimes() {
        String[] startTimes = new String[14];
        for (int i = 0; i < 14; i++) {
            startTimes[i] = Integer.toString(10 + i) + ":00";
        }
        switch (getRating()) {
            case "2":
                startTimes = getTimesAfter(startTimes, 16); // from 17:00
                break;
            case "3":
                startTimes = getTimesAfter(startTimes, 20); // from 21:00
                break;
        }
        return startTimes;
    }

    String[] getTimesAfter(String[] startTimes, int afterTime) {
        List<String> temp = new ArrayList<String>();
        String after = Integer.toString(afterTime) + ":00";
        for (String startTime : startTimes) {
            if (startTime.compareTo(after) > 0) {
                temp.add(startTime);
            }
        }
        String[] newStartTimes = temp.toArray(new String[temp.size()]);
        return newStartTimes;
    }

    String[] getTimesBefore(String[] startTimes, int beforeTime) {
        List<String> temp = new ArrayList<String>();
        String before = Integer.toString(beforeTime) + ":00";
        for (String startTime : startTimes) {
            if (startTime.compareTo(before) < 0) {
                temp.add(startTime);
            }
        }
        String[] newStartTimes = temp.toArray(new String[temp.size()]);
        return newStartTimes;
    }

    // Generates an array of dates, ranging from today up to a week later.
    private String[] generateStartDates() {
        String[] startDates = new String[7];
        LocalDateTime now;
        for (int i = 0; i < 7; i++) {
            now = LocalDateTime.now().plusDays(i + 1);
            String month = Integer.toString(now.getMonthValue());
            String day = Integer.toString(now.getDayOfMonth());
            startDates[i] = "2018-0" + month + "-" + day;
        }
        return startDates;
    }

    // Generates an array of dates, ranging from today up to a week later.
    private String[] generateFilterStartDates() {
        String[] startDates = new String[7];
        LocalDateTime now;
        for (int i = 0; i < 7; i++) {
            now = LocalDateTime.now().plusDays(i);
            String month = Integer.toString(now.getMonthValue());
            String day = Integer.toString(now.getDayOfMonth());
            startDates[i] = "2018-0" + month + "-" + day;
        }
        return startDates;
    }

    private DefaultTableModel getPastShows() throws IllegalStateException {
        // SQL query: count all past shows for selected movie
        // if pastShows < 'maxPlays' for movie --> new show creatable
        int selectedMovieRow = view.getSelectedMoviesTableRow();
        String query
                = "SELECT * FROM "
                + "(SELECT movies.maxPlays, movies.movieID, shows.startTime "
                + "FROM movies INNER JOIN shows ON movies.movieID = shows.movieID) as a "
                + "WHERE movieID = "
                + Integer.toString(selectedMovieRow + 1)
                + "AND startTime < " // Query past startTimes only
                + "'" + LocalDateTime.now()
                        .toString().replace("T", " ").substring(0, 19) + "'";
        return model.getPastShows(query);
    }

    /**
     * Adds an ActionListener to the "List shows" button.
     *
     * @param tableModelMovies the table model for movies
     * @param tableModelShows the table model for shows
     */
    private void addListShowsBtnListener() {
        view.getBtnListShows().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getFutureShows();
                addShowsTableSorter();
//                generateFilterText();
//                addMoviesFilterListener();
//                addRoomsFilterListener();
//                addStartTimesFilterListener();
                view.getCardLayout().show(view.getCardPanel(), "panelListShows");
            }
        });
    }

    private void getFutureShows() {
        int selectedMovieRow = view.getSelectedMoviesTableRow();
        try {
            String queryAll = "SELECT * FROM"
                    + "(SELECT movies.title, shows.showID, shows.movieID, shows.startTime, movies.playTime, shows.roomID, rooms.roomName "
                    + "FROM shows "
                    + "INNER JOIN movies ON shows.movieID = movies.movieID "
                    + "INNER JOIN rooms ON shows.roomID = rooms.roomID) as a "
                    + "WHERE startTime > " // Query future startTimes only
                    + "'" + LocalDateTime.now()
                            .toString().replace("T", " ").substring(0, 19) + "'";
            String query
                    = "SELECT * FROM "
                    + "(SELECT movies.title, shows.showID, shows.movieID, shows.startTime, movies.playTime, shows.roomID, rooms.roomName "
                    + "FROM shows "
                    + "INNER JOIN movies ON shows.movieID = movies.movieID "
                    + "INNER JOIN rooms ON shows.roomID = rooms.roomID) as a "
                    + "WHERE movieID = "
                    + Integer.toString(selectedMovieRow + 1)
                    + "AND startTime > " // Query future startTimes only
                    + "'" + LocalDateTime.now()
                            .toString().replace("T", " ").substring(0, 19) + "'";
            if (selectedMovieRow == -1) {
                model.getTableModelShows().setQuery(queryAll);
            } else {
                model.getTableModelShows().setQuery(query);
            }
            view.getShowsTable().getColumnModel().getColumn(3).setCellRenderer(new TimestampCellRenderer());
            view.resizeColumnWidth(view.getShowsTable());
            addShowsTableSorter();
            view.addShowsListTableListener(view.getShowsTable());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            // ensure database connection is closed
            model.getTableModelMovies().disconnectFromDatabase();
            model.getTableModelShows().disconnectFromDatabase();

            System.exit(1); // terminate application
        }
    }

    private void generateFilterText() {
        String[] movieTitles = getAllMovieTitles();
        String[] roomNames = getAllRooms();
        String[] startDates = generateFilterStartDates();
        view.generateShowFilters(movieTitles, roomNames, startDates);
    }

    String[] getAllMovieTitles() {
        DefaultTableModel tableModel = model.getAllMovieTitles();
        int rowCount = tableModel.getRowCount();

        String[] titles = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            titles[i] = tableModel.getValueAt(i, 0).toString();
        }
        return titles;
    }

    private void addMoviesFilterListener() {
        view.getMoviesFilter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = "SELECT * FROM "
                        + "(SELECT movies.title, shows.showID, shows.movieID, shows.startTime, movies.playTime, shows.roomID, rooms.roomName "
                        + "FROM shows "
                        + "INNER JOIN movies ON shows.movieID = movies.movieID "
                        + "INNER JOIN rooms ON shows.roomID = rooms.roomID) as a "
                        + "WHERE startTime > " // Query future startTimes only
                        + "'" + LocalDateTime.now()
                                .toString().replace("T", " ").substring(0, 19) + "' "
                        + "AND title = '"
                        + view.getMoviesFilter().getSelectedItem().toString().replace("'", "''") + "'";
                try {
                    model.getTableModelShows().setQuery(query);
                    view.getShowsTable().getColumnModel().getColumn(3).setCellRenderer(new TimestampCellRenderer());
                    view.resizeColumnWidth(view.getShowsTable());
                    addShowsTableSorter();
                    view.addShowsListTableListener(view.getShowsTable());
                    if (model.getTableModelShows().getRowCount() == 0) {
                        view.showMessage("Oops!", "Selected movie doesn't have any shows yet!");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void addRoomsFilterListener() {
        view.getRoomsFilter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = "SELECT * FROM "
                        + "(SELECT movies.title, shows.showID, shows.movieID, shows.startTime, movies.playTime, shows.roomID, rooms.roomName "
                        + "FROM shows "
                        + "INNER JOIN movies ON shows.movieID = movies.movieID "
                        + "INNER JOIN rooms ON shows.roomID = rooms.roomID) as a "
                        + "WHERE startTime > " // Query future startTimes only
                        + "'" + LocalDateTime.now()
                                .toString().replace("T", " ").substring(0, 19) + "' "
                        + "AND roomName = '"
                        + view.getRoomsFilter().getSelectedItem().toString() + "'";
                try {
                    model.getTableModelShows().setQuery(query);
                    view.getShowsTable().getColumnModel().getColumn(3).setCellRenderer(new TimestampCellRenderer());
                    view.resizeColumnWidth(view.getShowsTable());
                    addShowsTableSorter();
                    view.addShowsListTableListener(view.getShowsTable());
                } catch (SQLException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void addStartTimesFilterListener() {
        view.getStartTimesFilter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = view.getStartTimesFilter().getSelectedItem().toString();
                if (date.length() == 9) {
                    char lastChar = date.charAt(date.length() - 1);
                    String newChars = "0" + lastChar;
                    date = date.substring(0, date.length() - 1);
                    date = date.concat(newChars);
                }

//                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String query = "SELECT * FROM "
                        + "(SELECT movies.title, shows.showID, shows.movieID, "
                        + "shows.startTime, movies.playTime, shows.roomID, rooms.roomName "
                        + "FROM shows "
                        + "INNER JOIN movies ON shows.movieID = movies.movieID "
                        + "INNER JOIN rooms ON shows.roomID = rooms.roomID) as a "
                        + "WHERE CAST(DATE(startTime) AS VARCHAR(10)) LIKE '"
                        + date + "%'"
                        + "AND startTime > "
                        + "'" + LocalDateTime.now()
                                .toString().replace("T", " ").substring(0, 19) + "' ";
                try {
                    model.getTableModelShows().setQuery(query);
                    view.getShowsTable().getColumnModel().getColumn(3).setCellRenderer(new TimestampCellRenderer());
                    view.resizeColumnWidth(view.getShowsTable());
                    addShowsTableSorter();
                    view.addShowsListTableListener(view.getShowsTable());
                } catch (SQLException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void addBookShowBtnListener() {
        view.getBtnBookShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedShowTableRow = view.getShowsTable()
                        .convertRowIndexToModel(view.getSelectedShowsTableRow());
                int selectedRoomID = (Integer) model.getTableModelShows()
                        .getValueAt(selectedShowTableRow, 5);
                String selectedRoomQuery
                        = "SELECT numOfRows, numOfColumns FROM rooms WHERE roomID = "
                        + Integer.toString(selectedRoomID);

                String selectedDate = model.getTableModelShows()
                        .getValueAt(selectedShowTableRow, 3).toString();
                lastMinuteCheck(selectedDate);

                try {
                    model.setTableModelSelectedRoom(selectedRoomQuery);
                    int rows = (Integer) model.getTableModelSelectedRoom().getValueAt(0, 0);
                    int columns = (Integer) model.getTableModelSelectedRoom().getValueAt(0, 1);
                    int selectedShowID = (Integer) model.getTableModelShows()
                            .getValueAt(selectedShowTableRow, 1);
                    DefaultTableModel bookedSeats = model.getBookedSeats(selectedShowID);
                    if (hasBookedSeats(bookedSeats, selectedShowID)) {
                        view.setSeatsPanel(view.generateSeatsPanel(
                                rows, columns, bookedSeats
                        ), selectedShowID);
                    } else {
                        view.setSeatsPanel(view.generateSeatsPanel(rows, columns), selectedShowID);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                view.getCardLayout().show(view.getCardPanel(), "panelBooking");
            }

        }
        );
    }

    private void lastMinuteCheck(String selectedDate) throws NumberFormatException {
        String selectedMonth = selectedDate.substring(5, 7);
        String selectedDay = selectedDate.substring(8, 10);
        String selectedHour = selectedDate.substring(11, 13);

        String currentMonth = String.valueOf(LocalDateTime.now().getMonthValue());
        if (LocalDateTime.now().getMonthValue() < 10) {
            currentMonth = "0" + currentMonth;
        }
        String currentDay = String.valueOf(LocalDateTime.now().getDayOfMonth());
        if (LocalDateTime.now().getDayOfMonth() < 10) {
            currentDay = "0" + currentDay;
        }
        String currentHour = String.valueOf(LocalDateTime.now().getHour());
        currentHour = String.valueOf(Integer.valueOf(currentHour) + 1);
        int currentMinute = LocalDateTime.now().getMinute();

        if (selectedMonth.equals(currentMonth)
                && selectedDay.equals(currentDay)
                && (selectedHour.equals(currentHour))) {
            if (currentMinute > 0 && currentMinute < 31) {
                view.showMessage("Warning", "Watch out! Selected show begins within an hour!");
            } else {
                view.showMessage("Warning",
                        "Watch out! Selected show begins within less than 30 minutes!");
            }
        }
    }

    private boolean hasBookedSeats(DefaultTableModel model, int selectedShowID) {
        String showID = String.valueOf(selectedShowID);
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).toString().equals(showID)) {
                return true;
            }
        }
        return false;
    }

    private void addMoviesTableListener() {
        view.getMoviesTable().addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getFutureShows();
                addShowsTableSorter();
                view.addShowsListTableListener(view.getShowsTable());
                DefaultTableModel tableModel = getPastShows();
                int pastShows = tableModel.getRowCount();
                int maxPlays = 0;
                if (pastShows > 0) {
                    maxPlays = Integer.parseInt(tableModel.getValueAt(0, 0).toString());
                    if (pastShows >= maxPlays) {
                        view.getBtnCreateShow().setEnabled(false);
                    } else {
                        view.getBtnCreateShow().setEnabled(true);
                    }
                }

                if (model.getTableModelShows().getRowCount() > 0) {
                    view.getBtnListShows().setEnabled(true);
                    view.getSoldTicketsLabel().setVisible(true);
                    view.getSoldTickets().setText(getSoldTickets());
                    view.getSoldTickets().setVisible(true);
                } else {
                    view.getBtnListShows().setEnabled(false);
                    view.getSoldTicketsLabel().setVisible(false);
                    view.getSoldTickets().setVisible(false);
                    view.showMessage("Oops!", "This movie doesn't have any future shows yet!");
                }
            }
        });
    }

    private void addShowsTableListener() {
        view.getShowsTable().addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = view.getShowsTable().getSelectedRow();
                String selectedTitle = model.getTableModelShows().getValueAt(row, 0).toString();
                String selectedRoom = model.getTableModelShows().getValueAt(row, 6).toString();
                String selectedTime = model.getTableModelShows().getValueAt(row, 3).toString();
                view.setSelectedTitle(selectedTitle);
                view.setSelectedRoomName(selectedRoom);
                view.setSelectedStartTime(selectedTime);
                view.getPanelFinalizeBooking().validate();
                view.getPanelFinalizeBooking().repaint();
            }
        });
    }

    private String getSoldTickets() {
        int shows = model.getTableModelShows().getRowCount();
        int soldTickets = 0;
        for (int i = 0; i < shows; i++) {
            int selectedShowID = Integer.valueOf(model.getTableModelShows().getValueAt(i, 1).toString());
            soldTickets += model.getBookedSeats(selectedShowID).getRowCount();
        }
        return String.valueOf(soldTickets);
    }

    private void addDeleteShowBtnListener() {
        view.getBtnDeleteShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedShowTableRow = view.getShowsTable()
                        .convertRowIndexToModel(view.getSelectedShowsTableRow());
                int selectedShowID = (Integer) model.getTableModelShows()
                        .getValueAt(selectedShowTableRow, 1);
                System.out.println("selectedShowID: " + selectedShowID);
                DefaultTableModel table = model.getBookedSeats(selectedShowID);
                if (isDeletable(table, selectedShowID)) {
                    model.deleteShow(selectedShowID);
                } else {
                    view.showMessage("Oops!", "Selected show has booked seats!");
                }
            }
        });
    }

    // returns true if all seats are available for given show
    private boolean isDeletable(DefaultTableModel model, int showID) {
        return !hasBookedSeats(model, showID);
    }

    private void addNewShowBtnListener() {
        view.getBtnNewShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMovieTitle = view.getSelectedMovieTitle();
                int movieID = Integer.parseInt(model.getMovieID(selectedMovieTitle).getValueAt(0, 0).toString());
                String selectedRoomName = view.getSelectedRoomName();
                int roomID = Integer.parseInt(model.getRoomID(selectedRoomName).getValueAt(0, 0).toString());
                String selectedStartDate = view.getSelectedStartDate();
                String selectedStartTime = view.getSelectedStartTime();
                String startTime = selectedStartDate + " " + selectedStartTime + ":00";
                if (model.addNewShow(movieID, startTime, roomID)) {
                    view.showMessage("Success", "New show created successfully.");
                } else {
                    view.showMessage("Error", "Database error!");
                }
            }
        });
    }

    private void addRoomNamesListener() {
        view.getRoomNamesNewShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setSelectedRoomName(view.getSelectedRoomName());
                updateStartTimes();
            }
        });
    }

    private void addStartDatesListener() {
        view.getStartDatesNewShow().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setSelectedStartDate(view.getSelectedStartDate());
                updateStartTimes();
            }
        });
    }

    private void addStartTimesListener() {
        view.getStartTimesNewShow().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // do some magic
            }
        });
    }

    private void updateStartTimes() {
        String[] roomNames = getAllRooms();
        String[] startDates = generateStartDates();
        String[] oldStartTimes = generateStartTimes();
        String[] startTimesBefore = new String[0];
        String[] startTimesAfter = new String[0];
        String[] oldStartTimesBefore = oldStartTimes.clone();
        String[] oldStartTimesAfter = oldStartTimes.clone();
        String selectedStartDate = view.getSelectedStartDate();
        boolean getDefaultTimes = true;

        if (selectedStartDate.equals("")) {
            view.showMessage("Oops!", "Please select a date!");
        } else {
            DefaultTableModel showsModel = model.getAllShows(view.getSelectedRoomName());
            int selectedMoviesTableRow = view.getSelectedMoviesTableRow();
            int selectedMoviePlayTime = (Integer) model.getTableModelMovies()
                    .getValueAt(selectedMoviesTableRow, 4);

            int numOfShows = showsModel.getRowCount();
            for (int i = 0; i < numOfShows; i++) {
                String startTime = showsModel.getValueAt(i, 0).toString(); // startTime
                int playTime = Integer.parseInt(showsModel.getValueAt(i, 4).toString()); // playTime
                double plusTime = playTime / 60.0 + 0.5;
                double finishTime = Integer.parseInt(startTime.substring(11, 13)) + plusTime;
                int roundedShowFinishTime;
                if (finishTime < Math.round(finishTime)) {
                    roundedShowFinishTime = (int) Math.round(finishTime - 1);
                } else {
                    roundedShowFinishTime = (int) Math.round(finishTime);
                }

                String month = startTime.substring(5, 7);
                String day = startTime.substring(8, 10).trim();
                String hour = startTime.substring(11, 13);

                double minusTime = selectedMoviePlayTime / 60.0 + 0.5;
                double playMinusTime = Integer.parseInt(hour) - minusTime;
                int roundedShowMinusTime;
                if (playMinusTime > Math.round(playMinusTime)) {
                    roundedShowMinusTime = (int) Math.round(playMinusTime + 1);
                } else {
                    roundedShowMinusTime = (int) Math.round(playMinusTime);
                }

                String selectedMonth = selectedStartDate.substring(5, 7);
                String selectedDay = selectedStartDate.substring(8);
                if (selectedDay.length() == 1) {
                    selectedDay = "0" + selectedDay;
                }
                if (month.equals(selectedMonth)
                        && day.equals(selectedDay)) {
                    startTimesBefore = getTimesBefore(oldStartTimesBefore, roundedShowMinusTime);
                    oldStartTimesBefore = startTimesBefore.clone();

                    startTimesAfter = getTimesAfter(oldStartTimesAfter, roundedShowFinishTime);
                    oldStartTimesAfter = startTimesAfter.clone();
                    getDefaultTimes = false;
                }
            }
            String[] startTimes;
            if (getDefaultTimes) {
                startTimes = oldStartTimes.clone();
            } else {
                int newSize = startTimesBefore.length + startTimesAfter.length;
                startTimes = new String[newSize];
                int i = 0;
                while (i < startTimesBefore.length) {
                    startTimes[i] = startTimesBefore[i];
                    i++;
                }
                int j = 0;
                while (i < newSize) {
                    startTimes[i] = startTimesAfter[j];
                    j++;
                    i++;
                }
            }

            // TODO: szűrni kell a kiválasztott film 3 vetítésének közös időintervallumait
            // startTimes tömböt kell tovább szűrni
            getDefaultTimes = true;
            String movie = view.getSelectedMovieTitle().replace("'", "''");
            String date = view.getSelectedStartDate();
            DefaultTableModel showStartsForDate = model.getShows(movie, date);

            numOfShows = showStartsForDate.getRowCount();
            if (numOfShows >= 3) {
                oldStartTimesBefore = startTimes.clone();
                oldStartTimesAfter = startTimes.clone();

                String startTime1 = showStartsForDate.getValueAt(0, 0).toString(); // startTime
                int startHour1 = Integer.valueOf(startTime1.substring(11, 13));
                int playTime = selectedMoviePlayTime;
                double plusTime = playTime / 60.0 + 0.5;
                double finishTime1 = Integer.parseInt(startTime1.substring(11, 13)) + plusTime;
                int roundedFinishTime1;
                if (finishTime1 < Math.round(finishTime1)) {
                    roundedFinishTime1 = (int) Math.round(finishTime1 - 1);
                } else {
                    roundedFinishTime1 = (int) Math.round(finishTime1);
                }

                String startTime2 = showStartsForDate.getValueAt(1, 0).toString(); // startTime
                int startHour2 = Integer.valueOf(startTime2.substring(11, 13));
                double finishTime2 = Integer.parseInt(startTime2.substring(11, 13)) + plusTime;
                int roundedFinishTime2;
                if (finishTime2 < Math.round(finishTime2)) {
                    roundedFinishTime2 = (int) Math.round(finishTime2 - 1);
                } else {
                    roundedFinishTime2 = (int) Math.round(finishTime2);
                }

                String startTime3 = showStartsForDate.getValueAt(1, 0).toString(); // startTime
                int startHour3 = Integer.valueOf(startTime3.substring(11, 13));
                double finishTime3 = Integer.parseInt(startTime3.substring(11, 13)) + plusTime;
                int roundedFinishTime3;
                if (finishTime3 < Math.round(finishTime3)) {
                    roundedFinishTime3 = (int) Math.round(finishTime3 - 1);
                } else {
                    roundedFinishTime3 = (int) Math.round(finishTime3);
                }

                int blockStart = -1;
                int blockEnd = -1;
                if (startHour1 <= startHour2
                        && startHour2 <= roundedFinishTime1) {
                    blockStart = startHour2;
                    blockEnd = roundedFinishTime2;
                    getDefaultTimes = false;
                } else if (startHour2 <= startHour1
                        && startHour1 <= roundedFinishTime2) {
                    blockStart = startHour1;
                    blockEnd = roundedFinishTime2;
                    getDefaultTimes = false;
                }

                if (!getDefaultTimes) {
                    if (blockStart <= startHour3
                            && startHour3 <= blockEnd) {
                        blockStart = startHour3;
                    } else if (startHour3 <= blockStart
                            && blockStart <= roundedFinishTime3) {
                        blockEnd = roundedFinishTime3;
                    } else {
                        getDefaultTimes = true;
                    }

                    if (!getDefaultTimes) {
                        double minusTime = selectedMoviePlayTime / 60.0 + 0.5;
                        double BlockStartMinus = blockStart - minusTime;
                        int roundedBlockStartMinus;
                        if (BlockStartMinus > Math.round(BlockStartMinus)) {
                            roundedBlockStartMinus = (int) Math.round(BlockStartMinus + 1);
                        } else {
                            roundedBlockStartMinus = (int) Math.round(BlockStartMinus);
                        }

                        startTimesBefore = getTimesBefore(oldStartTimesBefore, roundedBlockStartMinus);
                        startTimesAfter = getTimesAfter(oldStartTimesAfter, blockEnd);

                        int newSize = startTimesBefore.length + startTimesAfter.length;
                        startTimes = new String[newSize];
                        int i = 0;
                        while (i < startTimesBefore.length) {
                            startTimes[i] = startTimesBefore[i];
                            i++;
                        }
                        int j = 0;
                        while (i < newSize) {
                            startTimes[i] = startTimesAfter[j];
                            j++;
                            i++;
                        }
                    }
                }
            }

            view.updateNewShow(roomNames, startDates, startTimes);
        }
    }

    private void addSynopsisBtnListener() {
        view.getBtnSynopsis().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String synopsis = getSynopsis();
                if (synopsis.equals("")) {
                    view.showMessage("Oops!", "Select a movie first!");
                } else {
                    view.showMessage("Synopsis", synopsis);
                }
            }
        });
    }

    String getSynopsis() {
        String selectedMovieTitle = view.getSelectedMovieTitle().replace("'", "''");
        if (selectedMovieTitle.length() > 0) {
            return model.getSynopsis(selectedMovieTitle).getValueAt(0, 0).toString();
        } else {
            return "";
        }
    }

    String getRating() {
        String selectedMovieTitle = view.getSelectedMovieTitle().replace("'", "''");
        return model.getRating(selectedMovieTitle).getValueAt(0, 0).toString();
    }
}
