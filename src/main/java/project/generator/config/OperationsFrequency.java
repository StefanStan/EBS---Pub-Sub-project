package project.generator.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stefan Stan on 16.03.2017.
 */
public class OperationsFrequency {

    private int GT;

    private int LT;

    private int EQ;

    private int DIFF;

    private int GTE;

    private int LTE;

    public int getGT() {
        return GT;
    }

    public void setGT(int GT) {
        this.GT = GT;
    }

    public int getLT() {
        return LT;
    }

    public void setLT(int LT) {
        this.LT = LT;
    }

    public int getEQ() {
        return EQ;
    }

    public void setEQ(int EQ) {
        this.EQ = EQ;
    }

    public int getDIFF() {
        return DIFF;
    }

    public void setDIFF(int DIFF) {
        this.DIFF = DIFF;
    }

    public int getGTE() {
        return GTE;
    }

    public void setGTE(int GTE) {
        this.GTE = GTE;
    }

    public int getLTE() {
        return LTE;
    }

    public void setLTE(int LTE) {
        this.LTE = LTE;
    }

    public String getOperatorUsed(int percent) {
        if(getEQ() == 100) {
            return "=";
        } else if(getDIFF() == 100) {
            return "!=";
        } else if(getGT() == 100) {
            return ">";
        } else if(getLT() == 100) {
            return "<";
        } else if(getGTE() == 100) {
            return ">=";
        } else if(getLTE() == 100) {
            return "<=";
        } else {
            TreeMap<Integer, List<String>> map = new TreeMap<Integer, List<String>>();
            addToMap(map, getEQ(), "=");
            addToMap(map, getDIFF(), "!=");
            addToMap(map, getGT(), ">");
            addToMap(map, getLT(), "<");
            addToMap(map, getGTE(), ">=");
            addToMap(map, getLTE(), "<=");

            return getSymbolBasedOnProbability(map, percent);
        }
    }

    private static void addToMap(Map<Integer, List<String>> map, int n, String symbol) {
        if(n == 0) {
            return;
        }
        if(map.get(n) == null) {
            List<String> list = new ArrayList<>();
            list.add(symbol);
            map.put(n, list);
        } else {
            map.get(n).add(symbol);
        }
    }

    private static String getSymbolBasedOnProbability(TreeMap<Integer, List<String>> map, int percent) {
        int currentPercent = 0;
        for(Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            for(String temp : entry.getValue()) {
                if(percent >= currentPercent && percent < currentPercent + entry.getKey()) {
                    return temp;
                } else {
                    currentPercent += entry.getKey();
                }
            }
        }
        return null;
    }
}
