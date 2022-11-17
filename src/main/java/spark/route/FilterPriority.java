package spark.route;

import java.util.Objects;

public class FilterPriority implements Comparable<FilterPriority> {

    private static final FilterPriority HIGHEST = new FilterPriority(Integer.MAX_VALUE);
    private static final FilterPriority HIGH = new FilterPriority(10000);
    private static final FilterPriority NORMAL = new FilterPriority(0);
    private static final FilterPriority LOW = new FilterPriority(-10000);
    private static final FilterPriority LOWEST = new FilterPriority(Integer.MIN_VALUE);

    private final int priority;

    protected FilterPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Retrieves the priority of this filter. <br>
     * The higher the priority, the earlier the filter will be executed.
     *
     * @return The priority of this filter
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(FilterPriority o) {
        return Integer.compare(o.priority, priority);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FilterPriority that = (FilterPriority) o;
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority);
    }

    /**
     * The highest priority.
     */
    public static FilterPriority highest() {
        return HIGHEST;
    }

    /**
     * The second-highest priority.
     */
    public static FilterPriority high() {
        return HIGH;
    }

    /**
     * The normal priority.
     */
    public static FilterPriority normal() {
        return NORMAL;
    }

    /**
     * The second-lowest priority.
     */
    public static FilterPriority low() {
        return LOW;
    }

    /**
     * The lowest priority.
     */
    public static FilterPriority lowest() {
        return LOWEST;
    }

    /**
     * Create a FilterPriority with a custom priority.
     *
     * @param priority The priority
     * @return The FilterPriority with the given priority
     */
    public static FilterPriority custom(int priority) {
        return new FilterPriority(priority);
    }
}
