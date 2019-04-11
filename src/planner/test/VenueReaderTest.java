package planner.test;

import java.util.*;
import java.io.*;
import planner.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Basic tests for the {@link VenueReader} implementation class.
 * 
 * A more extensive test suite will be performed for assessment of your code,
 * but this should get you started writing your own unit tests.
 */
public class VenueReaderTest {

    // Correct line separator for executing machine
    private final static String LINE_SEPARATOR = System.getProperty(
            "line.separator");

    /**
     * Test reading from a file where there are no venues.
     */
    @Test
    public void testCorrectlyFormattedZeroVenues() throws Exception {
        // string representations of the venues that we expect from the file
        List<String> expectedVenues = new ArrayList<>();
        // the actual venues read from the file
        List<Venue> actualVenues = VenueReader.read(
                "read_01_correctlyFormatted_zeroVenues.txt");
        // check that the expected and actual venues read are the same
        checkVenues(expectedVenues, actualVenues);
    }

    /**
     * Test reading from a file where there is one typical venue.
     */
    @Test
    public void testCorrectlyFormattedOneVenue() throws Exception {
        // string representations of the venues that we expect from the file
        List<String> expectedVenues = new ArrayList<>();
        expectedVenues.add("The Zoo (93)" + LINE_SEPARATOR
                + "Corridor City to Royal Queensland Show - EKKA (400): 51"
                + LINE_SEPARATOR + "Corridor City to St. Lucia (500): 7"
                + LINE_SEPARATOR + "Corridor Valley to City (300): 71"
                + LINE_SEPARATOR);

        // the actual venues read from the file
        List<Venue> actualVenues = VenueReader.read(
                "read_02_correctlyFormatted_oneVenue.txt");
        // check that the expected and actual venues read are the same
        checkVenues(expectedVenues, actualVenues);
    }

    /**
     * Test reading from a file where there are many venues.
     * 
     * Note that one of the venues doesn't generate any traffic -- that's OK.
     */
    @Test
    public void testCorrectlyFormattedManyVenues() throws Exception {
        // string representations of the venues that we expect from the file
        List<String> expectedVenues = new ArrayList<>();

        expectedVenues.add("The Gabba (200)" + LINE_SEPARATOR
                + "Corridor l1 to l2 (200): 150" + LINE_SEPARATOR
                + "Corridor l2 to l3 (100): 50" + LINE_SEPARATOR);

        expectedVenues.add("Tivoli (50)" + LINE_SEPARATOR);

        expectedVenues.add("Suncorp Stadium (100)" + LINE_SEPARATOR
                + "Corridor l0 to l1 (100): 25" + LINE_SEPARATOR
                + "Corridor l1 to l2 (200): 70" + LINE_SEPARATOR);

        // the actual venues read from the file
        List<Venue> actualVenues = VenueReader.read(
                "read_03_correctlyFormatted_manyVenues.txt");
        // check that the expected and actual venues read are the same
        checkVenues(expectedVenues, actualVenues);
    }

    /**
     * Test reading from a file where the traffic specified for a corridor is
     * incorrect.
     */
    @Test
    public void testIncorrectlyFormattedCorridorTraffic() throws Exception {
        // Error on line 4: traffic exceeds venue capacity
        try {
            VenueReader.read("read_04_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }

        // Error on line 5: traffic exceeds corridor capacity
        try {
            VenueReader.read("read_05_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }

        // Error on line 4: traffic is missing
        try {
            VenueReader.read("read_06_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }
    }

    /**
     * Test reading from a file where a corridor in the traffic of a venue has
     * an error.
     */
    @Test
    public void testIncorrectlyFormattedCorridor() throws Exception {
        // Error on line 6: same corridor appears more than once in traffic
        try {
            VenueReader.read("read_07_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }

        // Error on line 4: incorrectly formatted corridor - END is ""
        try {
            System.out.println(VenueReader.read(
                    "read_08_incorrectlyFormatted.txt"));
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }

    }

    /**
     * Test reading from a file where a venue's name is invalid.
     */
    @Test
    public void testIncorrectlyFormattedVenueName() throws Exception {
        // Error on line 6: venue name cannot be ""
        try {
            VenueReader.read("read_09_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }
    }

    /**
     * Test reading from a file where a venue's capacity is invalid.
     */
    @Test
    public void testIncorrectlyFormattedVenueCapacity() throws Exception {
        // Error on line 7: venue capacity is invalid
        try {
            VenueReader.read("read_10_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }
    }

    /**
     * Test reading from a file where a venue does not have an empty line at the
     * end.
     */
    @Test
    public void testIncorrectlyFormattedVenueMissingEmptyLine()
            throws Exception {
        // Error on line 9: empty line expected to complete venue
        try {
            VenueReader.read("read_11_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }
    }

    /**
     * Test reading from a file that contains the same description of a venue
     * twice.
     */
    @Test
    public void testIncorrectlyFormattedDuplicateVenues() throws Exception {
        // Error found when line 10 reached: duplicate venue detected
        try {
            VenueReader.read("read_12_incorrectlyFormatted.txt");
            Assert.fail("FormatException not thrown");
        } catch (FormatException e) {
            // OK
            // System.out.println(e.getMessage()); // Uncomment to check message
        }
    }

    // -----Helper Methods-------------------------------

    /**
     * Check that the list actualVenues has all of the venues described by
     * expectedVenueStrings, in the order that they appear in that list.
     * 
     * @param expectedVenueStrings
     *            A list of the expected string representations of the venues
     *            that should appear in actualVenues.
     * @param actualVenues
     *            The list of venues to be checked against expectedVenueStrings.
     */
    private void checkVenues(List<String> expectedVenueStrings,
            List<Venue> actualVenues) {
        Assert.assertEquals(expectedVenueStrings.size(), actualVenues.size());
        for (int i = 0; i < expectedVenueStrings.size(); i++) {
            Assert.assertEquals(expectedVenueStrings.get(i), actualVenues.get(i)
                    .toString());
        }
    }

}
