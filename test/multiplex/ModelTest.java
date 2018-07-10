/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplex;

import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author TFEJER
 */
public class ModelTest {

    public ModelTest() {
    }

    /**
     * Test of saveBookedSeats method, of class Model.
     */
    @Test
    public void testSaveBookedSeats() {
        System.out.println("saveBookedSeats");
        int showID = 0;
        int rowNum = 0;
        int columnNum = 0;
        char status = ' ';
        Model instance = new Model();
        boolean result = instance.saveBookedSeats(showID, rowNum, columnNum, status);
        assertTrue(result);
    }

    /**
     * Test of getBookedSeats method, of class Model.
     */
    @Test
    public void testGetBookedSeats_int() {
        System.out.println("getBookedSeats");
        int selectedShowID = 0;
        Model instance = new Model();
        int expResult = 0;
        int result = Integer.valueOf(instance.getBookedSeats(selectedShowID).getValueAt(0, 1)
                .toString());
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteShow method, of class Model.
     */
    @Test
    public void testDeleteShow() {
        System.out.println("deleteShow");
        int showID = 0;
        Model instance = new Model();
        boolean result = instance.deleteShow(showID);
        assertTrue(result);
    }

    /**
     * Test of getAllRooms method, of class Model.
     */
    @Test
    public void testGetAllRooms() {
        System.out.println("getAllRooms");
        Model instance = new Model();
        String[] expResult = {"Main Room", "Red Room", "VIP Room", "Green Room"};
        assertEquals(expResult[0], instance.getAllRooms().getValueAt(0, 1));
        assertEquals(expResult[1], instance.getAllRooms().getValueAt(1, 1));
        assertEquals(expResult[2], instance.getAllRooms().getValueAt(2, 1));
        assertEquals(expResult[3], instance.getAllRooms().getValueAt(3, 1));
    }

    /**
     * Test of getRoomID method, of class Model.
     */
    @Test
    public void testGetRoomID() {
        System.out.println("getRoomID");
        Model instance = new Model();
        String[] roomNames = {"Main Room", "Red Room", "VIP Room", "Green Room"};
        int[] roomIDs = {701, 702, 703, 704};
        int expResult1 = Integer.valueOf(instance.getRoomID(roomNames[0]).getValueAt(0, 0).toString());
        int expResult2 = Integer.valueOf(instance.getRoomID(roomNames[1]).getValueAt(0, 0).toString());
        int expResult3 = Integer.valueOf(instance.getRoomID(roomNames[2]).getValueAt(0, 0).toString());
        int expResult4 = Integer.valueOf(instance.getRoomID(roomNames[3]).getValueAt(0, 0).toString());
        assertEquals(roomIDs[0], expResult1);
        assertEquals(roomIDs[1], expResult2);
        assertEquals(roomIDs[2], expResult3);
        assertEquals(roomIDs[3], expResult4);
    }

    /**
     * Test of getMovieID method, of class Model.
     */
    @Test
    public void testGetMovieID() {
        System.out.println("getMovieID");
        String movieTitle = "Fight Club";
        Model instance = new Model();
        int expResult = 10;
        int result = Integer.valueOf(instance.getMovieID(movieTitle).getValueAt(0, 0).toString());
        assertEquals(expResult, result);
    }

    /**
     * Test of getSynopsis method, of class Model.
     */
    @Test
    public void testGetSynopsis() {
        System.out.println("getSynopsis");
        String movieTitle = "Fight Club";
        Model instance = new Model();
        String expResult = "An insomniac office worker, looking for a way to change his life, crosses paths with a devil-may-care soapmaker, forming an underground fight club that evolves into something much, much more.";
        String result = instance.getSynopsis(movieTitle).getValueAt(0, 0).toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRating method, of class Model.
     */
    @Test
    public void testGetRating() {
        System.out.println("getRating");
        String movieTitle = "Fight Club";
        Model instance = new Model();
        int expResult = 3;
        int result = (Integer.valueOf(instance.getRating(movieTitle).getValueAt(0, 0).toString()));
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllMovieTitles method, of class Model.
     */
    @Test
    public void testGetAllMovieTitles() {
        System.out.println("getAllMovieTitles");
        Model instance = new Model();
        int tableRows = instance.getAllMovieTitles().getRowCount();
        String[] expResult = {"The Shawshank Redemption", "The Godfather", "The Godfather: Part II",
            "The Dark Knight", "12 Angry Men", "Schindler's List",
            "The Lord of the Rings: The Return of the King", "Pulp Fiction",
            "The Good, the Bad and the Ugly", "Fight Club"};
        String[] result = new String[tableRows];
        for (int i = 0; i < tableRows; i++) {
            result[i] = instance.getAllMovieTitles().getValueAt(i, 0).toString();
        }
        assertEquals(expResult[0], result[0]);
        assertEquals(expResult[1], result[1]);
        assertEquals(expResult[2], result[2]);
        assertEquals(expResult[3], result[3]);
        assertEquals(expResult[4], result[4]);
        assertEquals(expResult[5], result[5]);
        assertEquals(expResult[6], result[6]);
        assertEquals(expResult[7], result[7]);
        assertEquals(expResult[8], result[8]);
        assertEquals(expResult[9], result[9]);
    }

}
