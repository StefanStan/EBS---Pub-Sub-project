package project.generator.utils;

import project.generator.config.GeneratorConfig;
import project.generator.domain.MyPair;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class Util {

    {
        populateAppConfig();
    }

    public static String OPERATOR_REGEX = null;
    
    public static String serializePublication(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for(Map.Entry<String, Object> temp : map.entrySet()) {
            sb.append("(").append(temp.getKey()).append(",").append(serializeObj(temp.getValue())).append(");");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");

        return sb.toString();
    }

    public static Map<String, Object> deserializePublication(String pub, GeneratorConfig config) throws InvalidObjectException, ParseException {
        Map<String, Object> result = new HashMap<>();

        pub = pub.replaceAll("[{}]", "");
        String[] pairs = pub.split(";");
        for(String pair : pairs) {
            String temp = pair.replaceAll("[()]", "");
            String[] members = temp.split(",");

            result.put(members[0], deserializeObj(members[1], config.getConfigByName(members[0]).getType()));
        }

        return result;
    }

    public static String serializeSubscription(Map<String, MyPair> map) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for(Map.Entry<String, MyPair> temp : map.entrySet()) {
            sb.append("(").append(temp.getKey()).append(",").append(serializeObj(temp.getValue())).append(");");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");

        return sb.toString();
    }

    public static Map<String, MyPair> deserializeSubscription(String sub, GeneratorConfig config) throws InvalidObjectException, ParseException {
        Map<String, MyPair> result = new HashMap<>();

        sub = sub.replaceAll("[{}]", "");
        String[] pairs = sub.split(";");
        for(String pair : pairs) {
            String temp = pair.replaceAll("[()]", "");
            String[] members = temp.split(",");

            result.put(members[0], (MyPair) deserializeObj(members[1]+","+members[2], config.getConfigByName(members[0]).getType()));
        }

        return result;
    }

    public static void writeListOfStringToFile(List<String> list, String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        list.forEach(pw::println);

        pw.close();
        bw.close();
        fw.close();
    }

    private static String serializeObj(Object o) {
        if(o instanceof String) {
            return "\"" + o + "\"";
        } else if (o instanceof Date) {
            return new SimpleDateFormat("dd.MM.yyyy").format(o);
        } else if (o instanceof MyPair) {
            return ((MyPair) o).getOperator() + "," + serializeObj(((MyPair) o).getOperand());
        } else {
            return o.toString();
        }
    }

    private static Object deserializeObj(String obj, String type) throws ParseException, InvalidObjectException {
        switch (type.toLowerCase()) {
            case "string":
                return obj.replaceAll("\"", "");
            case "double":
                return Double.parseDouble(obj);
            case "int":
                return Integer.parseInt(obj);
            case "datetime":
                return new SimpleDateFormat("dd.MM.yyyy").parse(obj);
            default:
                if(obj.substring(0,1).matches(OPERATOR_REGEX)) {
                    String[] pairs = obj.split(",");
                    return MyPair.from(pairs[0], deserializeObj(pairs[1], type));
                }
                throw new InvalidObjectException(obj);
        }
    }

    private void populateAppConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("app_config.properties");

            // load a properties file
            prop.load(input);

            OPERATOR_REGEX = prop.getProperty("OPERATOR_REGEX");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
