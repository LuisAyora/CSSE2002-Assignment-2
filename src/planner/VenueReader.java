package planner;

import java.io.*;
import java.text.Format;
import java.text.Normalizer;
import java.util.*;

/**
 * Provides a method to read in a list of venues from a text file.
 */
public class VenueReader {

    // Correct line separator for executing machine (used in toString method)
    private final static String LINE_SEPARATOR = System.getProperty(
      "line.separator");

    /**
     * <p>
     * Reads a text file called fileName that describes the venues in a
     * municipality, and returns a list containing each of the venues read from
     * the file, in the order that they appear in the file.
     * </p>
     * 
     * <p>
     * The file contains zero or more descriptions of different venues. (I.e. a
     * file containing zero venues contains zero lines; a file containing one
     * venue contains exactly one description of a venue and no other lines or
     * information; a file containing multiple venues contains each description
     * of a venue, one after the other with no other information or lines in the
     * file.)
     * </p>
     * 
     * <p>
     * A description of a venue consists of exactly (i) one line consisting of
     * the name of the venue followed by (ii) one line containing a positive
     * integer denoting the capacity of the venue followed by (iii) a
     * description of the traffic generated by hosting an event of maximum size
     * at the venue, followed by (iv) an empty line.
     * </p>
     * 
     * <p>
     * For (i) the venue name is the entire string on the first line of the
     * venue description (i.e. it may contain white space characters etc.). The
     * only constraint on the venue name is that it may not be equal to the
     * empty string ("").
     * </p>
     * 
     * <p>
     * For (ii) the second line of a venue description may not contain leading
     * or trailing whitespace characters, it may only contain a positive integer
     * denoting the venue capacity.
     * </p>
     * 
     * <p>
     * For (iii) the traffic is described by one line for each corridor that
     * will have traffic from the venue when it hosts an event of maximum size.
     * Each line is a string of the form <br>
     * <br>
     * "START, END, CAPACITY: TRAFFIC"<br>
     * <br>
     * where START and END are different non-empty strings denoting the name of
     * the start location of the corridor and the end location of the corridor,
     * respectively; CAPACITY is a positive integer denoting the capacity of the
     * corridor; and TRAFFIC is a positive integer denoting the amount of
     * traffic from the venue that will use the corridor when the venue hosts
     * the largest event that it can. The strings denoting the start and end
     * locations of the corridor may contain any characters other than a comma
     * (',') or semicolon (':'). Both CAPACITY and TRAFFIC should be positive
     * integers with no additional leading or trailing whitespace. For example,
     * <br>
     * <br>
     * "St. Lucia, Royal Queensland Show - EKKA, 120: 60"<br>
     * <br>
     * represents a traffic corridor from "St. Lucia" to "Royal Queensland Show
     * - EKKA" with a maximum capacity of 120, that will have 60 people from the
     * venue using it when the venue hosts an event of maximum size. <br>
     * <br>
     * Note that the start, end and capacity of a corridor are separated by the
     * string ", ". The corridor and its traffic are separated by ": ". <br>
     * <br>
     * The corridors and their respective traffic may appear in any order (i.e.
     * the corridors aren't necessarily sorted in any way.) Each corridor may
     * only appear once in the traffic description for a venue (i.e. there is
     * only one line for each corridor), and the traffic on that corridor should
     * be less than or equal to the capacity of the venue, and less than or
     * equal to the capacity of the corridor.
     * </p>
     * 
     * <p>
     * For (iv) an empty line is a line with no characters at all (i.e. the
     * contents of the line is the empty string "").
     * </p>
     * 
     * <p>
     * Two equivalent venues shouldn't appear twice in the file.
     * </p>
     * 
     * <p>
     * If a FormatException is thrown, it will have a meaningful message that
     * accurately describes the problem with the input file format, including
     * the line of the file where the problem was detected.
     * </p>
     * 
     * @param fileName
     *            the name of the file to read from.
     * @return a list of the venues from the file, in the order in which they
     *         appear in the file.
     * @throws IOException
     *             if there is an error reading from the input file.
     * @throws FormatException
     *             if there is an error with the input format (e.g. there is
     *             more than one venue description in the file that describes
     *             the same venue, or the file format is not as specified above
     *             in any other way.) The FormatExceptions thrown should have a
     *             meaningful message that accurately describes the problem with
     *             the input file format, including the line of the file where
     *             the problem was detected.
     */
    public static List<Venue> read(String fileName) throws IOException,
            FormatException {
        ArrayList<Venue> readVenues = new ArrayList<>();
        FileReader fr = new FileReader(fileName);
        BufferedReader venueReader = new BufferedReader(fr);
        String currentLine;

        while ((currentLine = venueReader.readLine()) != null) {
            String venueName = parseVenueName(currentLine);
            currentLine = venueReader.readLine();
            int venueCapacity = parseVenueCapacity(currentLine);
            Traffic venueTraffic = new Traffic();

            // Parse each Traffic Corridor
            while(!(currentLine = venueReader.readLine()).equals("")) {
                Corridor trafficCorridor = parseVenueTrafficCorridor(
                  currentLine);
                int corridorTraffic = parseCorridorTraffic(currentLine, venueCapacity);
                if (corridorTraffic > trafficCorridor.getCapacity()) {
                    throw new FormatException();
                }
                if (venueTraffic.getCorridorsWithTraffic().
                  contains(trafficCorridor)) {
                    throw new FormatException();
                }
                venueTraffic.updateTraffic(trafficCorridor, corridorTraffic);
            }

            addVenueToList(readVenues, new Venue(venueName,
              venueCapacity, venueTraffic));
        }

        venueReader.close();
        fr.close();
        return readVenues; // REMOVE THIS LINE AND WRITE THIS METHOD
    }

    /**
     *
     * @param line
     * @return
     * @throws FormatException
     */
    private static String parseVenueName(String line) throws FormatException {
        try {
            Scanner venueLineScanner = new Scanner(line);
            String venueName = venueLineScanner.nextLine();
            venueLineScanner.close();
            return venueName;
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     *
     * @param line
     * @return
     * @throws FormatException
     */
    private static int parseVenueCapacity(String line) throws FormatException {
        try {
            Scanner venueLineScanner = new Scanner(line);
            int venueCapacity = venueLineScanner.nextInt();
            venueLineScanner.close();
            return venueCapacity;
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     *
     * @param line
     * @return
     * @throws FormatException
     */
    private static Corridor parseVenueTrafficCorridor(String line)
            throws FormatException {
        try {
            Scanner venueLineScanner = new Scanner(line).useDelimiter(",");

            String startLocationString = venueLineScanner.next();
            if (startLocationString.equals("")) {
                throw new FormatException();
            }
            Location startLocation = new Location(startLocationString);
            String endLocationString = venueLineScanner.next();
            Location endLocation = new Location(endLocationString.replaceFirst(" ",
              ""));
            venueLineScanner.useDelimiter(":");
            if (endLocationString.equals(" ")) {
                throw new FormatException();
            }
            String capacityString = venueLineScanner.next();
            int capacity = Integer.parseInt(capacityString.replace(", ", ""));

            venueLineScanner.close();

            return new Corridor(startLocation, endLocation, capacity);
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     * <p>
     *   Reads a line containing Traffic Corridor and extracts the value of the
     *   traffic associated with that corridor.
     * </p>
     *
     * <p>
     *   Line must be in the correct syntax or else it will throw a Format
     *   Exception.
     * </p>
     *
     * @param line
     * @return
     * @throws FormatException
     */
    private static int parseCorridorTraffic(String line, int venueCapacity)
            throws FormatException {
        try {
            Scanner venueLineScanner = new Scanner(line).useDelimiter(":");
            venueLineScanner.next();
            String trafficString = venueLineScanner.next();
            int corridorTraffic = Integer.parseInt(trafficString.replace(" ",
              ""));
            if (corridorTraffic > venueCapacity) {
                throw new FormatException();
            }
            venueLineScanner.close();
            return corridorTraffic;
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    private static void addVenueToList(List<Venue> venueList, Venue venue)
      throws FormatException {
        if (venueList.contains(venue)) {
            throw new FormatException();
        }
        venueList.add(venue);
    }
}
