
package Interface;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author Grupo 04
 */
public class ScoreComparator implements Comparator<Map.Entry<String,Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        int r;
        if (o1.getValue() > o2.getValue()) r = -1;
        else r = 1; 
        return r;
    }
}
