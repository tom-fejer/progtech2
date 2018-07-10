package multiplex;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author TFEJER
 */
public class View extends JFrame {

    private final ImagePanel IMAGEPANEL_DEFAULT = new ImagePanel("img/00_Default.png");
    private final ImagePanel IMAGEPANEL_1 = new ImagePanel("img/01_Shawshank.jpg");
    private final ImagePanel IMAGEPANEL_2 = new ImagePanel("img/02_Godfather.jpg");
    private final ImagePanel IMAGEPANEL_3 = new ImagePanel("img/03_Godfather_II.jpg");
    private final ImagePanel IMAGEPANEL_4 = new ImagePanel("img/04_DarkKnight.jpg");
    private final ImagePanel IMAGEPANEL_5 = new ImagePanel("img/05_12AngryMen.jpg");
    private final ImagePanel IMAGEPANEL_6 = new ImagePanel("img/06_SchindlersList.jpg");
    private final ImagePanel IMAGEPANEL_7 = new ImagePanel("img/07_LotR.jpg");
    private final ImagePanel IMAGEPANEL_8 = new ImagePanel("img/08_PulpFiction.jpg");
    private final ImagePanel IMAGEPANEL_9 = new ImagePanel("img/09_GoodBadUgly.jpg");
    private final ImagePanel IMAGEPANEL_10 = new ImagePanel("img/10_FightClub.jpg");

    private CardLayout cardLayout = new CardLayout();
    private CardLayout seatsCardLayout = new CardLayout();
    private CardLayout imageCardLayout = new CardLayout();

    private JButton btnListMovies = new JButton("List Movies");
    private JButton btnListShows = new JButton("List Shows");
    private JButton btnCreateShow = new JButton("Create new show");
    private JButton btnDeleteShow = new JButton("Delete selected show");
    private JButton btnBookShow = new JButton("Book for selected show");
    private JButton btnBookSeat = new JButton("Book for selected seat");
    private JButton btnBack1 = new JButton("<< Back to Menu");
    private JButton btnBack2 = new JButton("<< Back");
    private JButton btnBack3 = new JButton("<< Back");
    private JButton btnBack4 = new JButton("<< Back");
    private JButton btnFinalize;
    private JButton btnNewShow = new JButton("Create new show");
    private JButton btnSeatCount1 = new JButton("1 seat");
    private JButton btnSeatCount2 = new JButton("2 seats");
    private JButton btnSeatCount4 = new JButton("4 seats");
    private JButton btnSynopsis = new JButton("< Read synopsis >");

    private JLabel[][] seatLabels;
    private JLabel selectedSeatsLabel1 = new JLabel("Selected (row, col): ");
    private JLabel selectedSeatsLabel2 = new JLabel("");
    private JLabel finalizeSelectedSeats = new JLabel();
    private JLabel finalizeSelectedMovieLabel = new JLabel();
    private JLabel finalizeSelectedTimeLabel = new JLabel();
    private JLabel finalizeSelectedRoomLabel = new JLabel();
    private JLabel selectedMovieTitleLabel = new JLabel();
    private JLabel soldTicketsLabel = new JLabel("Sold tickets for selected movie: ");
    private JLabel soldTickets = new JLabel();

    private JPanel panelSelectionMenu = new JPanel();
    private JPanel panelListMovies = new JPanel();
    private JPanel panelListShows = new JPanel();
    private JPanel panelCreateShow = new JPanel();
    private JPanel panelDeleteShow = new JPanel();
    private JPanel panelBooking = new JPanel();
    private JPanel panelFinalizeBooking = new JPanel();
    private JPanel cardPanel = new JPanel();
    private JPanel imageCardPanel = new JPanel(imageCardLayout);
    private JPanel seatsCardPanel = new JPanel();
    private List<JPanel> seatsPanels = new ArrayList<JPanel>();
    private JPanel comboPanel = new JPanel();

    private JTable moviesTable;
    private JTable showsTable;

    private String selectedSeats = "";
    private String selectedMovieTitle = "";
    private String selectedRoomName = "Main Room";
    private String selectedStartDate = "";
    private String selectedStartTime = "12:00";

    private int selectedMoviesTableRow = -1;
    private int selectedShowsTableRow = -1;
    private int selectedSeatNum = 0;
    private int clickCount = 0;

    private JComboBox roomNamesNewShow = new JComboBox();
    private JComboBox startTimesNewShow = new JComboBox();
    private JComboBox startDatesNewShow = new JComboBox();

    private JComboBox moviesFilter = new JComboBox();
    private JComboBox roomsFilter = new JComboBox();
    private JComboBox startTimesFilter = new JComboBox();

    private boolean firstNewShow = true;

    /**
     * Initializes the GUI panels.
     */
    public View() {
        super("Multiplex");

        cardPanel.setLayout(cardLayout);
        panelSelectionMenu.setLayout(new BorderLayout());
        panelListMovies.setLayout(new BorderLayout());
        panelListShows.setLayout(new BorderLayout());
        panelCreateShow.setLayout(new BorderLayout());
        panelDeleteShow.setLayout(new BorderLayout());
        panelBooking.setLayout(new BorderLayout());
        panelFinalizeBooking.setLayout(new BorderLayout());

        createMainCardPanel();
        createSelectionMenu();
        createImageCardPanel();
        createMoviesListPanel();
        createShowsListPanel();
        createBookingPanel();
        createFinalizeBookingPanel();
        createCreateShowPanel();

        addBtnListener(new btnListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Custom class used for displaying the movie posters.
     */
    public class ImagePanel extends JPanel {

        private Image image;

        /**
         *
         * @param imgSrc
         */
        public ImagePanel(String imgSrc) {
            ImageIcon ii = new ImageIcon(this.getClass().getResource(imgSrc));
            image = ii.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    /**
     * Implements the <code>actionPerformed</code> method of the <code>ActionListener</code> for all
     * the simple buttons.
     */
    public class btnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().getClass().equals(JButton.class)) {
                JButton src = (JButton) e.getSource();

                if (src.equals(getBtnListMovies())) {
                    getCardLayout().show(getCardPanel(), "panelListMovies");
                }

                if (src.equals(getBtnBack1())) {
                    getCardLayout().show(getCardPanel(), "panelSelectionMenu");
                }

                if (src.equals(getBtnBack2()) || src.equals(getBtnBack4())) {
                    getCardLayout().show(getCardPanel(), "panelListMovies");
                }

                if (src.equals(getBtnBack3())) {
                    getCardLayout().show(getCardPanel(), "panelListShows");
                }

                if (src.equals(getBtnBookSeat())) {
                    String selectedMovie = "Selected movie: " + selectedMovieTitle;
                    finalizeSelectedMovieLabel.setText(selectedMovie);
                    String selectedRoom = "Selected room: " + selectedRoomName;
                    finalizeSelectedRoomLabel.setText(selectedRoom);
                    String selectedTime = "Selected time: " + selectedStartTime.substring(0, 16);
                    finalizeSelectedTimeLabel.setText(selectedTime);
                    getCardLayout().show(getCardPanel(), "panelFinalizeBooking");
                }

                if (src.equals(getBtnSeatCount1())) {
                    setSelectedSeatNum(1);
                }

                if (src.equals(getBtnSeatCount2())) {
                    setSelectedSeatNum(2);
                }

                if (src.equals(getBtnSeatCount4())) {
                    setSelectedSeatNum(4);
                }
            }
        }
    }

    /**
     * Adds an ActionListener for buttons.
     *
     * @param l
     */
    public void addBtnListener(ActionListener l) {
        btnListMovies.addActionListener(l);
        btnBack1.addActionListener(l);
        btnBack2.addActionListener(l);
        btnBack3.addActionListener(l);
        btnBack4.addActionListener(l);
        btnBookShow.addActionListener(l);
        btnBookSeat.addActionListener(l);
        btnSeatCount1.addActionListener(l);
        btnSeatCount2.addActionListener(l);
        btnSeatCount4.addActionListener(l);
    }

    private void createMainCardPanel() {
        cardPanel.add(panelSelectionMenu, "panelSelectionMenu");
        cardPanel.add(panelListMovies, "panelListMovies");
        cardPanel.add(panelListShows, "panelListShows");
        cardPanel.add(panelCreateShow, "panelCreateShow");
        cardPanel.add(panelBooking, "panelBooking");
        cardPanel.add(panelFinalizeBooking, "panelFinalizeBooking");
        cardPanel.setPreferredSize(new Dimension(1280, 720));
        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "panelSelectionMenu");
    }

    private void createSelectionMenu() {
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));
//        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(btnListMovies);
//        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(Box.createHorizontalGlue());
//        panelSelectionMenu.add(btnPanel, BorderLayout.NORTH);

        ImagePanel imagePanel = new ImagePanel("img/menuBG.jpg");
        imagePanel.add(btnPanel, BorderLayout.CENTER);
        panelSelectionMenu.add(imagePanel, BorderLayout.CENTER);
    }

    private void createMoviesListPanel() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(btnBack1);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(btnSynopsis);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(soldTicketsLabel);
        buttonPane.add(soldTickets);
        soldTicketsLabel.setVisible(false);
        soldTickets.setVisible(false);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(btnCreateShow);
        buttonPane.add(btnListShows); // get list of shows for selected movie
        panelListMovies.add(buttonPane, BorderLayout.PAGE_END);
        panelListMovies.add(imageCardPanel, BorderLayout.LINE_START);
    }

    private void createImageCardPanel() {
        imageCardPanel.add(IMAGEPANEL_DEFAULT, "imagePanelDefault");
        imageCardPanel.add(IMAGEPANEL_1, "imagePanel1");
        imageCardPanel.add(IMAGEPANEL_2, "imagePanel2");
        imageCardPanel.add(IMAGEPANEL_3, "imagePanel3");
        imageCardPanel.add(IMAGEPANEL_4, "imagePanel4");
        imageCardPanel.add(IMAGEPANEL_5, "imagePanel5");
        imageCardPanel.add(IMAGEPANEL_6, "imagePanel6");
        imageCardPanel.add(IMAGEPANEL_7, "imagePanel7");
        imageCardPanel.add(IMAGEPANEL_8, "imagePanel8");
        imageCardPanel.add(IMAGEPANEL_9, "imagePanel9");
        imageCardPanel.add(IMAGEPANEL_10, "imagePanel10");
        imageCardPanel.setPreferredSize(new Dimension(485, 720));
        imageCardLayout.show(imageCardPanel, "imagePanelDefault");
    }

    private void createShowsListPanel() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(btnBack2);
        buttonPane.add(Box.createHorizontalGlue());
        btnDeleteShow.setBackground(Color.RED);
        btnDeleteShow.setForeground(Color.WHITE);
        btnDeleteShow.setEnabled(false);
        buttonPane.add(btnDeleteShow);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(btnBookShow);
        panelListShows.add(buttonPane, BorderLayout.SOUTH);
    }

    void generateShowFilters(String[] movieTitles, String[] roomNames, String[] dates) {
        JPanel filterPane = new JPanel();
        filterPane.setLayout(new BoxLayout(filterPane, BoxLayout.LINE_AXIS));
        filterPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        filterPane.add(Box.createHorizontalGlue());
        moviesFilter = new JComboBox(movieTitles);
        filterPane.add(moviesFilter);
        filterPane.add(Box.createRigidArea(new Dimension(10, 0)));
        roomsFilter = new JComboBox(roomNames);
        filterPane.add(roomsFilter);
        filterPane.add(Box.createRigidArea(new Dimension(10, 0)));
        startTimesFilter = new JComboBox(dates);
        filterPane.add(startTimesFilter);
        filterPane.add(Box.createHorizontalGlue());
        panelListShows.add(filterPane, BorderLayout.NORTH);
        panelListShows.validate();
        panelListShows.repaint();
    }

    private void createBookingPanel() {
        JPanel selectSeatsCount = new JPanel();
        selectSeatsCount.add(
                new JLabel("Select number of seats to book:"), BorderLayout.BEFORE_FIRST_LINE
        );
        selectSeatsCount.add(btnSeatCount1);
        selectSeatsCount.add(btnSeatCount2);
        selectSeatsCount.add(btnSeatCount4);
        panelBooking.add(selectSeatsCount, BorderLayout.NORTH);

        seatsCardPanel = new JPanel(seatsCardLayout);
        panelBooking.add(seatsCardPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btnPanel.add(btnBack3);
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(selectedSeatsLabel1);
        btnPanel.add(selectedSeatsLabel2);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(btnBookSeat);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        panelBooking.add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a table of seats, where seats are represented with <code>JLabel</code> objects with
     * different background colors for booked and for available seats.
     *
     * @param rows the weight of the seats panel
     * @param cols the height of the seats panel
     * @return the seats <code>JPanel</code> object that holds all the <code>JLabel</code> seats.
     */
    public JPanel generateSeatsPanel(int rows, int cols) {
        seatLabels = new JLabel[rows][cols];

        JPanel seats = new JPanel();
        seats.setLayout(new GridLayout(rows, cols));

        for (int row = 0; row < seatLabels.length; row++) {
            JLabel rowNumber = new JLabel(Integer.toString(row + 1));
            rowNumber.setHorizontalAlignment(SwingConstants.CENTER);
            seats.add(rowNumber);

            for (int col = 0; col < seatLabels[row].length; col++) {
                JLabel seat = new JLabel();
                seat.setOpaque(true);
                seat.setBackground(Color.GREEN);
                seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                seat.setPreferredSize(new Dimension(10, 10));
                seat.addMouseListener(new seatMouseListener(row, col));
                seats.add(seat);
                seatLabels[row][col] = seat;
            }
        }
        return seats;
    }

    public JPanel generateSeatsPanel(int rows, int cols, DefaultTableModel table) {
        seatLabels = new JLabel[rows][cols];
        JPanel seats = new JPanel();
        seats.setLayout(new GridLayout(rows, cols));
        for (int row = 0; row < seatLabels.length; row++) {
            JLabel rowNumber = new JLabel(Integer.toString(row + 1));
            rowNumber.setHorizontalAlignment(SwingConstants.CENTER);
            seats.add(rowNumber);

            for (int col = 0; col < seatLabels[row].length; col++) {
                JLabel seat = new JLabel();
                seat.setOpaque(true);
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (Integer.valueOf(table.getValueAt(i, 2).toString()).equals(row)
                            && Integer.valueOf(table.getValueAt(i, 3).toString()).equals(col)) {
                        seat.setBackground(Color.RED);
                        break;
                    } else {
                        seat.setBackground(Color.GREEN);
                    }
                }
                seat.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                seat.setPreferredSize(new Dimension(10, 10));
                seat.addMouseListener(new seatMouseListener(row, col));
                seats.add(seat);
                seatLabels[row][col] = seat;
            }
        }
        return seats;
    }

    /**
     * Creates a table of seats, where seats are represented with <code>JLabel</code> objects with
     * different background colors for booked and for available seats.
     *
     * @param rows the weight of the seats panel
     * @param cols the height of the seats panel
     * @param labels the <code>JLabel[][]</code> that stores the booked seats information
     * @return the seats <code>JPanel</code> object that holds all the <code>JLabel</code> seats.
     */
    public JPanel generateSeatsPanel(int rows, int cols, JLabel[][] labels) {
        seatLabels = labels;

        JPanel seats = new JPanel();
        seats.setLayout(new GridLayout(rows, cols));

        for (int row = 0; row < seatLabels.length; row++) {
            JLabel rowNumber = new JLabel(Integer.toString(row + 1));
            rowNumber.setHorizontalAlignment(SwingConstants.CENTER);
            seats.add(rowNumber);

            for (int col = 0; col < seatLabels[row].length; col++) {
                seats.add(seatLabels[row][col]);
            }
        }
        return seats;
    }

    /**
     * Inner class that handles the seat selection in the seats panel.
     */
    public class seatMouseListener extends MouseAdapter {

        private int row, col;

        /**
         *
         * @param row the selected seat row
         * @param col the selected seat column
         */
        public seatMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (clickCount < selectedSeatNum) {
                    if (seatLabels[row][col].getBackground().equals(Color.GREEN)) {
                        seatLabels[row][col].setBackground(Color.YELLOW);
                        clickCount++;
                    } else if (seatLabels[row][col].getBackground().equals(Color.YELLOW)) {
                        seatLabels[row][col].setBackground(Color.GREEN);
                        clickCount--;
                    }
                } else if (seatLabels[row][col].getBackground().equals(Color.YELLOW)) {
                    seatLabels[row][col].setBackground(Color.GREEN);
                    clickCount--;
                }
                updateSelectedSeats();
            }
        }
    }

    private void updateSelectedSeats() {
        StringBuffer selectedSeatText = new StringBuffer();
        int selectedNum = 0;
        for (int i = 0; i < seatLabels.length; i++) {
            for (int j = 0; j < seatLabels[i].length; j++) {
                if (seatLabels[i][j].getBackground().equals(Color.YELLOW)) {
                    selectedSeatText.append(Integer.toString(i + 1) + ", " + Integer.toString(j + 1));
                    selectedSeatText.append(";  ");
                    selectedNum++;
                }
            }
        }
        if (selectedSeatText.length() > 2) {
            selectedSeatText.delete(selectedSeatText.length() - 3, selectedSeatText.length() - 1);
        }
        selectedSeats = selectedSeatText.toString();
        selectedSeatsLabel2.setText(selectedSeats);
        finalizeSelectedSeats.setText("Selected seats: " + selectedSeats);

        if (selectedNum > 1) {
            btnBookSeat.setText("Book for selected seats");
        } else {
            btnBookSeat.setText("Book for selected seat");
        }
    }

    /**
     * Creates the Booking finalize panel.
     */
    public void createFinalizeBookingPanel() {
        JPanel finalizeSelectionPanel = new JPanel();
        finalizeSelectionPanel.setBorder(BorderFactory.createEmptyBorder(300, 550, 20, 200));
        finalizeSelectionPanel.setLayout(new GridLayout(4, 1)); // title, startTime, RoomName, seats
        finalizeSelectedMovieLabel.setText(selectedMovieTitle);
        finalizeSelectedRoomLabel.setText(selectedRoomName);
        finalizeSelectedTimeLabel.setText(selectedStartTime);
        finalizeSelectionPanel.add(finalizeSelectedMovieLabel);
        finalizeSelectionPanel.add(finalizeSelectedRoomLabel);
        finalizeSelectionPanel.add(finalizeSelectedTimeLabel);

        finalizeSelectedSeats.setText("Selected seats: " + selectedSeats);
//        finalizeSelectedSeats.setPreferredSize(new Dimension(500, 200));
//        finalizeSelectedSeats.setHorizontalAlignment(SwingConstants.CENTER);
        finalizeSelectionPanel.add(finalizeSelectedSeats, BorderLayout.CENTER);
        panelFinalizeBooking.add(finalizeSelectionPanel, BorderLayout.NORTH);

        btnFinalize = new JButton("Finalize");
        btnFinalize.setBackground(Color.GREEN);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.RED);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCardLayout().show(getCardPanel(), "panelBooking");
            }
        });
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(btnFinalize);
        panelFinalizeBooking.add(buttonPane, BorderLayout.SOUTH);
    }

    private void createCreateShowPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(btnBack4);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(btnNewShow);
        panelCreateShow.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the names of the rooms and the start times for the selected movie.
     *
     * @param roomNames the name of the rooms for the selected movie
     * @param startDates the dates for the selected movie
     * @param startTimes the starting times for the selected movie
     */
    public void updateNewShow(String[] roomNames, String[] startDates, String[] startTimes) {
        if (firstNewShow) {
            this.roomNamesNewShow = new JComboBox(roomNames);
            comboPanel.add(this.roomNamesNewShow);
        }
        if (firstNewShow) {
            this.startDatesNewShow = new JComboBox(startDates);
            comboPanel.add(this.startDatesNewShow);
            this.startDatesNewShow.setSelectedIndex(0);
        }
        if (firstNewShow) {
            this.startTimesNewShow = new JComboBox(startTimes);
            comboPanel.add(this.startTimesNewShow);
        } else {
            this.startTimesNewShow.removeAllItems();
            for (String item : startTimes) {
                this.startTimesNewShow.addItem(item);
            }
        }
        if (firstNewShow) {
            panelCreateShow.add(comboPanel, BorderLayout.CENTER);
            firstNewShow = false;
        }
        panelCreateShow.validate();
        panelCreateShow.repaint();
    }

    public JButton getBtnListMovies() {
        return btnListMovies;
    }

    public JButton getBtnBack1() {
        return btnBack1;
    }

    public JButton getBtnBack2() {
        return btnBack2;
    }

    public JButton getBtnBack3() {
        return btnBack3;
    }

    public JButton getBtnBack4() {
        return btnBack4;
    }

    public JButton getBtnBookShow() {
        return btnBookShow;
    }

    public JButton getBtnBookSeat() {
        return btnBookSeat;
    }

    public JButton getBtnListShows() {
        return btnListShows;
    }

    public JButton getBtnCreateShow() {
        return btnCreateShow;
    }

    public JButton getBtnDeleteShow() {
        return btnDeleteShow;
    }

    public JButton getBtnSeatCount1() {
        return btnSeatCount1;
    }

    public JButton getBtnSeatCount2() {
        return btnSeatCount2;
    }

    public JButton getBtnSeatCount4() {
        return btnSeatCount4;
    }

    public JButton getBtnSynopsis() {
        return btnSynopsis;
    }

    public JButton getBtnFinalize() {
        return btnFinalize;
    }

    public JButton getBtnNewShow() {
        return btnNewShow;
    }

    public JComboBox getRoomNamesNewShow() {
        return roomNamesNewShow;
    }

    public JComboBox getStartDatesNewShow() {
        return startDatesNewShow;
    }

    public JComboBox getStartTimesNewShow() {
        return startTimesNewShow;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JTable getMoviesTable() {
        return moviesTable;
    }

    public JTable getShowsTable() {
        return showsTable;
    }

    public JLabel[][] getSeatLabels() {
        return seatLabels;
    }

    public String getSelectedMovieTitle() {
        return selectedMovieTitle;
    }

    public JLabel getSelectedMovieTitleLabel() {
        return this.selectedMovieTitleLabel;
    }

    public int getSelectedMoviesTableRow() {
        return selectedMoviesTableRow;
    }

    public int getSelectedShowsTableRow() {
        return selectedShowsTableRow;
    }

    public JComboBox getMoviesFilter() {
        return moviesFilter;
    }

    public JComboBox getRoomsFilter() {
        return roomsFilter;
    }

    public JPanel getPanelFinalizeBooking() {
        return panelFinalizeBooking;
    }

    public JComboBox getStartTimesFilter() {
        return startTimesFilter;
    }

    public JLabel getSoldTicketsLabel() {
        return soldTicketsLabel;
    }

    public JLabel getSoldTickets() {
        return soldTickets;
    }

//    /**
//     *
//     * @param seatLabels
//     */
//    public void setSeatsPanel(JLabel[][] seatLabels) {
//        int seatRow = seatLabels.length;
//        int seatCol = seatLabels[0].length;
//        JPanel seats = new JPanel();
//        seats.setLayout(new GridLayout(seatRow, seatCol));
//
//        for (int row = 0; row < seatLabels.length; row++) {
//            JLabel rowNumber = new JLabel(Integer.toString(row + 1));
//            rowNumber.setHorizontalAlignment(SwingConstants.CENTER);
//            seats.add(rowNumber);
//
//            for (int col = 0; col < seatLabels[row].length; col++) {
//                seats.add(seatLabels[row][col]);
//            }
//        }
//        seatsPanel = seats;
//        seatsCardPanel.add(seatsPanel);
//        seatsCardPanel.validate();
//        seatsCardPanel.repaint();
//    }
    public void setSeatsPanel(JPanel seatsPanel, int selectedShowID) {
        seatsPanels.add(seatsPanel);
        seatsCardPanel.add(seatsPanels.get(seatsPanels.size() - 1), "seatsPanel"
                + Integer.toString(selectedShowID));
        seatsCardPanel.validate();
        seatsCardPanel.repaint();
        seatsCardLayout.show(seatsCardPanel, "seatsPanel"
                + Integer.toString(selectedShowID));
    }

    public void setSelectedSeatNum(int selectedSeatNum) {
        this.selectedSeatNum = selectedSeatNum;
    }

    /**
     * Sets the JTable for movies with the JTable with actual data from database.
     *
     * @param moviesTable
     */
    public void setMoviesTable(JTable moviesTable) {
        this.moviesTable = moviesTable;
        this.moviesTable.setVisible(true);
        resizeColumnWidth(this.moviesTable);
        addMoviesTableListener(moviesTable);
        panelListMovies.add(new JScrollPane(moviesTable), BorderLayout.CENTER);
    }

    private void addMoviesTableListener(JTable moviesTable) {
        moviesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moviesTableMouseClicked(evt);
            }
        });
    }

    /**
     * Handles the selection from the movies JTable. Saves the row index of the currently selected
     * movie and displays the movie poster.
     *
     * @param evt
     */
    public void moviesTableMouseClicked(MouseEvent evt) {
        ResultSetTableModel model = (ResultSetTableModel) moviesTable.getModel();
        selectedMoviesTableRow = moviesTable.getSelectedRow();

        selectedMovieTitle = model.getValueAt(selectedMoviesTableRow, 0).toString();
        selectedMovieTitleLabel.setText(selectedMovieTitle);
        selectedMovieTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedMovieTitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelCreateShow.add(selectedMovieTitleLabel, BorderLayout.NORTH);
        panelCreateShow.validate();
        panelCreateShow.repaint();

        imageCardLayout.show(imageCardPanel, "imagePanel"
                + Integer.toString(selectedMoviesTableRow + 1));
    }

    /**
     * Sets the JTable of this View object to the given parameter.
     *
     * @param moviesTable the JTable object that holds the actual data from the database.
     */
    public void setShowsTable(JTable showsTable) {
        this.showsTable = showsTable;
        this.showsTable.setVisible(true);
        resizeColumnWidth(this.showsTable);
        addShowsListTableListener(this.showsTable);
        panelListShows.add(new JScrollPane(this.showsTable), BorderLayout.CENTER);
        this.showsTable.getColumnModel().getColumn(2).setCellRenderer(new TimestampCellRenderer());
    }

    void addShowsListTableListener(JTable showsTable) {
        showsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showsTableMouseClicked();
            }
        });
    }

    private void showsTableMouseClicked() {
        selectedShowsTableRow = showsTable.getSelectedRow();
        btnDeleteShow.setEnabled(true);
    }

    void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 10; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    void finalizeSeats() {
        for (int i = 0; i < seatLabels.length; i++) {
            for (int j = 0; j < seatLabels[i].length; j++) {
                if (seatLabels[i][j].getBackground().equals(Color.YELLOW)) {
                    seatLabels[i][j].setBackground(Color.RED);
                }
            }
        }
        resetSeats();
    }

    void resetSeats() {
        selectedSeatNum = 0;
        clickCount = 0;
        selectedSeats = "";
    }

    public void showMessage(String title, String message) {
        JTextArea msg = new JTextArea(message);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setSize(500, 200);
        msg.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(msg);
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    String getSelectedRoomName() {
        return roomNamesNewShow.getSelectedItem().toString();
    }

    String getSelectedStartDate() {
        return startDatesNewShow.getSelectedItem().toString();
    }

    String getSelectedStartTime() {
        return startTimesNewShow.getSelectedItem().toString();
    }

    String getCurrentRoomName() {
        return selectedRoomName;
    }

    String getCurrentStartDate() {
        return selectedStartDate;
    }

    String getCurrentStartTime() {
        return selectedStartTime;
    }

    void setSelectedTitle(String selectedTitle) {
        this.selectedMovieTitle = selectedTitle;
    }

    void setSelectedRoomName(String roomName) {
        this.selectedRoomName = roomName;
    }

    void setSelectedStartDate(String startDate) {
        this.selectedStartDate = startDate;
    }

    void setSelectedStartTime(String startTime) {
        this.selectedStartTime = startTime;
    }
}
