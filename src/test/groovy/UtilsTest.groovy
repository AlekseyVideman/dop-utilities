import alekseyvideman.dop.Collection
import spock.lang.Shared
import spock.lang.Specification
import tools.jackson.databind.ObjectMapper

class UtilsTest extends Specification {

    static class CollectionTest extends Specification {

        def setup() {
            Collection.init(new ObjectMapper())
        }


        @Shared
        Map map = [a: [b: 0]]
        @Shared
        Map wrongMap = [a: [V: 0]]
        @Shared
        String[] path = ["a", "b"]

        def "Map must contain a value at given path, if a developer if 100% sure"() {
            when:
            Collection.get(map, path)

            then:
            notThrown(IllegalArgumentException)
        }

        def "throws exception for Map that must contain a value at given path, but doesn't"() {
            when:
            Collection.get(wrongMap, path)

            then:
            thrown(IllegalArgumentException)
        }

        def "check whether Map contains a value by given path or not, do not throw exception, return boolean"() {
            when:
            boolean exists = Collection.contains(aMap as Map, path)

            then:
            exists == result

            where:
            aMap     | result
            map      | true
            wrongMap | false
        }

        def "return null if value null"() {
            expect:
            (Collection.getOrNull(map, path) == null) == isNull

            where:
            map         | path       || isNull
            [a: [b: 1]] | ["a", "b"] || false
            [a: [b: 1]] | ["a", "j"] || true
            [a: [b: 1]] | ["b"]      || true
        }
    }
}
