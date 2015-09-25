package goja;

import goja.config.GojaConfig;
import goja.tuples.Pair;
import org.junit.Test;

import java.util.List;

public class GojaConfigTest {

    @Test
    public void testReload() throws Exception {

    }

    @Test
    public void testClear() throws Exception {

    }

    @Test
    public void testGetConfigProps() throws Exception {

    }

    @Test
    public void testChainConfig() throws Exception {
        List<Pair<String, String>> pairs = GojaConfig.chainConfig();
        for (Pair<String, String> pair : pairs) {
            System.out.println("pair = " + pair);
        }
    }
}