/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiplex;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author TFEJER
 */
public class ControllerTest {

    public ControllerTest() {
    }

    /**
     * Test of getTimesAfter method, of class Controller.
     */
    @Test
    public void testGetTimesAfter() {
        System.out.println("getTimesAfter");
        String[] startTimes = {"10:00", "11:00", "12:00", "13:00", "14:00"};
        int afterTime = 0;
        Controller instance = new Controller(new View(), new Model());
        String[] expResult = {"12:00", "13:00", "14:00"};
        String[] result = instance.getTimesAfter(startTimes, 11);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getTimesBefore method, of class Controller.
     */
    @Test
    public void testGetTimesBefore() {
        System.out.println("getTimesBefore");
        String[] startTimes = {"10:00", "11:00", "12:00", "13:00", "14:00"};
        int beforeTime = 0;
        Controller instance = new Controller(new View(), new Model());
        String[] expResult = {"10:00", "11:00", "12:00"};
        String[] result = instance.getTimesBefore(startTimes, 13);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getAllMovieTitles method, of class Controller.
     */
    @Test
    public void testGetAllMovieTitles() {
        System.out.println("getAllMovieTitles");
        Controller instance = new Controller(new View(), new Model());
        String[] expResult = {"The Shawshank Redemption", "The Godfather", "The Godfather: Part II",
            "The Dark Knight", "12 Angry Men", "Schindler's List",
            "The Lord of the Rings: The Return of the King", "Pulp Fiction",
            "The Good, the Bad and the Ugly", "Fight Club"
        };
        String[] result = instance.getAllMovieTitles();
        assertArrayEquals(expResult, result);
    }

}
