package planner;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read in a list of venues from a text file.
 */
public class VenueReader {

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
        String currentLine = venueReader.readLine();
        // Parse name, capacity and traffic corridors
        while (currentLine != null) {
            String venueName = parseVenueName(currentLine);
            currentLine = venueReader.readLine();
            int venueCapacity = parseVenueCapacity(currentLine);
            Traffic venueTraffic = new Traffic();
            currentLine = venueReader.readLine();
            // Parse each Traffic Corridor
            while(!currentLine.equals("")) {
                Corridor trafficCorridor = parseVenueTrafficCorridor(
                  currentLine);
                int corridorTraffic = parseCorridorTraffic(currentLine,
                  venueCapacity, trafficCorridor);
                if (venueTraffic.getCorridorsWithTraffic()
                  .contains(trafficCorridor)) {
                    throw new FormatException();
                }
                venueTraffic.updateTraffic(trafficCorridor, corridorTraffic);
                if ((currentLine = venueReader.readLine()) == null) {
                    throw new FormatException();
                }
            } // Now add the venue to the list
            addVenueToList(readVenues, new Venue(venueName,
              venueCapacity, venueTraffic));
            currentLine = venueReader.readLine();
        }
        venueReader.close();
        fr.close();
        return readVenues;
    }

    /**
     * <p>
     *   Reads the name of a venue on a line of the read document.
     * </p>
     *
     * <p>
     *   Will throw an error when the line cannot be read correctly or when
     *   the venue name is an empty string.
     * </p>
     *
     * @param line
     *        the line to extract the name from.
     * @return the venue name as a string.
     * @throws FormatException
     *         when there is a problem reading the line of the file or when
     *         the venue name is an empty string.
     */
    private static String parseVenueName(String line)
      throws FormatException {
        try (Scanner venueLineScanner = new Scanner(line)) {
            String venueName = venueLineScanner.nextLine();
            if (venueName.equals(" ")) {
                throw new FormatException();
            }
            return venueName;
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     * <p>
     *   Reads the venue capacity from a line of the read file.
     * </p>
     *
     * <p>
     *   Fails when the line cannot be parsed as an integer.
     * </p>
     *
     * @param line
     *        the line to extract the capacity from.
     * @return the venue's capacity as an integer.
     * @throws FormatException
     *         when the line cannot be read as an integer.
     */
    private static int parseVenueCapacity(String line) throws FormatException {
        try (Scanner venueLineScanner = new Scanner(line)) {
            return venueLineScanner.nextInt();
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     * <p>
     *   Parses a traffic Corridor file from a line of text on the read file.
     * </p>
     *
     * <p>
     *   Each line contains the start and end locations of the corridors and
     *   the capacity of the corridor. This method does not handle the traffic
     *   value associated with the corridor.
     * </p>
     *
     * <p>
     *   This method throws an exception when the line cannot be parsed
     *   properly or if the location names of the corridor are empty
     *   strings.
     * </p>
     *
     * @param line
     *        the line to generate the Corridor from
     * @return a traffic Corridor parsed from the line
     * @throws FormatException
     *         if the names of the start or end Locations are empty strings
     *         or if the line cannot be parsed properly.
     */
    private static Corridor parseVenueTrafficCorridor(String line)
            throws FormatException {
        try (Scanner venueLineScanner = new Scanner(line).useDelimiter(",")) {
            String locationString = venueLineScanner.next();
            if (locationString.equals(" ")) {
                throw new FormatException();
            }
            Location startLocation = new Location(locationString);
            locationString = venueLineScanner.next();
            Location endLocation = new Location(locationString.replaceFirst(" ",
              ""));
            if (locationString.equals(" ")) {
                throw new FormatException();
            } // Now parse the Corridor capacity
            venueLineScanner.useDelimiter(":");
            String capacityString = venueLineScanner.next().replace(", ", "");
            int capacity = Integer.parseInt(capacityString);
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
     * <p>
     *   This method implements error checking to ensure that the traffic value
     *   is not larger than the venue's capacity or the capacity of its
     *   associated traffic corridor.
     * </p>
     *
     * <p>
     *   This method fails if the value cannot be read as a number or if the
     *   traffic value is larger than the capacity of the venue or the traffic
     *   corridor.
     * </p>
     *
     * @param line
     *        the line to extract the traffic value from.
     * @param venueCapacity
     *        the venue's capacity to ensure the traffic value is valid.
     * @param corridor
     *        the corridor to ensure traffic is safe.
     * @return the traffic value of a corridor as an integer.
     * @throws FormatException
     *         if the value cannot be parsed as an integer or if the value
     *         is larger than the capacities of either the venue or the
     *         traffic corridor.
     */
    private static int parseCorridorTraffic(String line, int venueCapacity,
      Corridor corridor) throws FormatException {
        try (Scanner venueLineScanner = new Scanner(line).useDelimiter(":");) {
            venueLineScanner.next(); // Skip the corridor data
            String trafficString = venueLineScanner.next().replace(" ", "");
            int corridorTraffic = Integer.parseInt(trafficString);
            if (corridorTraffic > venueCapacity ||
              corridorTraffic > corridor.getCapacity()) {
                throw new FormatException();
            }
            return corridorTraffic;
        } catch (Exception e) {
            throw new FormatException();
        }
    }

    /**
     * <p>
     *   Adds a venue to the read venues list if the venue was not added
     *   previously.
     * </p>
     *
     * <p>
     *   The method fails if the venue already exists on the list.
     * </p>
     *
     * @param venueList
     *        the read venues list.
     * @param venue
     *        the venue to add to the list.
     * @throws FormatException
     *         if the venue already exists on the list.
     */
    private static void addVenueToList(List<Venue> venueList, Venue venue)
      throws FormatException {
        if (venueList.contains(venue)) {
            throw new FormatException();
        }
        venueList.add(venue);
    }
}
