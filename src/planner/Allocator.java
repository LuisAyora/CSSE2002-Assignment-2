package planner;

import java.util.*;

/**
 * Provides a method for finding a safe allocation of events to venues.
 */
public class Allocator {

    /**
     * <p>
     * Returns a safe allocation of events to venues, if there is at least one
     * possible safe allocation, or null otherwise.
     * </p>
     * 
     * <p>
     * NOTE: What it means for an allocation of events to venues to be safe is
     * defined in the assignment handout.
     * </p>
     * 
     * @require events != null && venues != null && !events.contains(null) &&
     *          !venues.contains(null) && events does not contain duplicate
     *          events && venues does not contain duplicate venues.
     * @ensure Returns a safe allocation of events to venues, if there is at
     *         least one possible safe allocation, or null otherwise.
     */
    public static Map<Event, Venue> allocate(List<Event> events,
            List<Venue> venues) {
        // DO NOT MODIFY THE IMPLEMENTATION OF THIS METHOD
        Set<Map<Event, Venue>> allocations = allocations(events, venues);
        if (allocations.isEmpty()) {
            // returns null to signify that there is no possible safe allocation
            return null;
        } else {
            // returns one (any one) of the possible safe allocations
            return allocations.iterator().next();
        }
    }

    /**
     * Returns the set of all possible safe allocations of events to venues.
     * 
     * @require events != null && venues != null && !events.contains(null) &&
     *          !venues.contains(null) && events does not contain duplicate
     *          events && venues does not contain duplicate venues.
     * @ensure Returns the set of all possible safe allocations of events to
     *         venues. (Note: if there are no possible allocations, then this
     *         method should return an empty set of allocations.)
     */
    private static Set<Map<Event, Venue>> allocations(List<Event> events,
            List<Venue> venues) {
        return null; // REMOVE THIS LINE AND WRITE THIS METHOD
    }

}
