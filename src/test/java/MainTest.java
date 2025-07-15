import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MainTest {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        System.out.println(map.keySet().stream().collect(Collectors.toList()));
    }
}
