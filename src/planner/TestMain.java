package planner;

import java.io.IOException;
import java.util.*;

public class TestMain {
  public static void main(String[] args) {
    try {
      List<Venue> venues = VenueReader.read("read_02_correctlyFormatted_oneVenue.txt");
      System.out.println(venues.get(0).toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    } finally {
      System.out.println("I'm learning how to use the try/catch/finally");
    }
  }
}
