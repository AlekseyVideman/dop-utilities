package alekseyvideman.dop;

import org.jspecify.annotations.NullMarked;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Safe access to values in nested maps, e.g. data deserialized from JSON. <br>
 * A value is addressed by a path - a sequence of keys, where every key but the last one points to another map.
 *
 * <p>The class must be initialized with {@link #init(ObjectMapper)} before use, because the mapper is used
 * to render the offending map in error messages.
 */
@SuppressWarnings("all")
public class Collection {

    private static ObjectMapper json =  new ObjectMapper() {
        @Override
        public String writeValueAsString(Object value) throws JacksonException {
            throw new UnsupportedOperationException("Collection is not initialized with method init()");
        }
    };

    private Collection() {}

    /**
     * Sets the mapper used to render maps in error messages. <br>
     * Must be called before any other method of this class, otherwise an {@link UnsupportedOperationException} is thrown
     * from within a failing lookup.
     *
     * @param json the mapper to render maps in error messages
     */
    public static void init(ObjectMapper json) {
        Collection.json = json;
    }

    /**
     * Retrieves a value from nested maps by the given path. <br>
     * This method is for the case when there is 100% certainty that a value exists along the specified path.
     *
     * @param map  the map to read from
     * @param path the sequence of keys leading to the value
     * @param <T>  the expected type of the value
     * @return the value found at the given path
     * @throws IllegalArgumentException if a developer makes an mistake in the path or passes wrong map
     */
    @NullMarked
    public static <T> T get(Map map, String... path) {
        Object result = map;
        String prevKey = "";

        for (String key : path) {
            if (!(result instanceof Map)) {
                throw new IllegalArgumentException("Key '%s' is not pointing to a Map in map %s at path %s".formatted(prevKey,
                                                                                                                      json.writeValueAsString(map),
                                                                                                                      Arrays.toString(path)));
            }
            Map<String, Object> currentMap = (Map<String, Object>) result;
            result = currentMap.get(key);
            prevKey = key;
        }

        if (Objects.isNull(result)) {
            throw new IllegalArgumentException("Null value for key '%s' in map %s at path %s".formatted(prevKey,
                                                                                                        json.writeValueAsString(map),
                                                                                                        Arrays.toString(path)));
        }

        return (T) result;
    }

    /**
     * Checks if there is a value in nested maps by the given path. <br>
     * This method is for the case the when a map is allowed to have null values for polymorphism.
     *
     * @param map  the map to read from
     * @param path the sequence of keys leading to the value
     * @return {@code true} if there is a non-null value at the given path, {@code false} otherwise
     */
    public static boolean contains(Map map, String... path) {
        Object result = map;
        for (String key : path) {
            if (!(result instanceof Map)) {
                return false;
            }
            Map<String, Object> currentMap = (Map<String, Object>) result;
            result = currentMap.get(key);
        }

        return result != null;
    }

    /**
     * For cases when null values are permitted
     *
     * @param map  the map to read from
     * @param path the sequence of keys leading to the value
     * @param <T>  the expected type of the value
     * @return the value found at the given path, or {@code null} if there is no such value
     */
    public static <T> T getOrNull(Map map, String... path) {
        if (contains(map, path)) {
            return Collection.get(map, path);
        }
        return null;
    }

}
