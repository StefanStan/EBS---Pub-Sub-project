package project.generator;

import com.google.gson.Gson;
import project.generator.config.ConfigObj;
import project.generator.config.GeneratorConfig;
import project.generator.domain.MyPair;
import project.generator.domain.Publication;
import project.generator.domain.Subscription;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class Generator {

    private GeneratorConfig config;

    public Generator() throws IOException {
        populateGenConfig();
    }

    public List<Publication> generatePublications() {
        List<Publication> pubs = new ArrayList<>();

        for(int i = 0; i < config.getTotalNumberOfMessages(); i++){
            Map<String, Object> pubFields = new HashMap<>();
            for (ConfigObj obj : config.getFields()) {
                try {
                    pubFields.put(obj.getName(), getObject(obj));
                } catch (IOException | ParseException e) {
                    continue;
                }
            }
            pubs.add(new Publication(pubFields));
        }

        return pubs;
    }

    public List<Subscription> generateSubscription(int nr) {
        List<Subscription> subs = new ArrayList<>();

        for(int i = 0; i < nr; i++){
            Map<String, MyPair> subFields = new HashMap<>();
            for (ConfigObj obj : config.getFields()) {
                MyPair pair = null;
                try {
                    pair = getObjectForSubscription(obj);
                    if(pair != null) {
                        subFields.put(obj.getName(), pair);
                    }
                } catch (IOException | ParseException e) {
                    continue;
                }
            }
            if(subFields.values().size() > 0) {
                subs.add(new Subscription(subFields));
            }
        }
        return subs;
    }

    private void populateGenConfig() throws IOException {
        String content = readFile("Config.json", Charset.defaultCharset());
        Gson g = new Gson();
        config = g.fromJson(content, GeneratorConfig.class);
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private Object getObject(ConfigObj obj) throws IOException, ParseException {
        switch (obj.getValues().getSource()) {
            case "Interval":
                    if(obj.getType().toLowerCase().equals("int")) {
                        return getRandomInt(obj.getValues().getStart(), obj.getValues().getEnd());
                    } else if (obj.getType().toLowerCase().equals("double")) {
                        return getRandomDouble(obj.getValues().getStart(), obj.getValues().getEnd());
                    } else if (obj.getType().toLowerCase().equals("datetime")) {
                        return getRandomDate(obj.getValues().getStart(), obj.getValues().getEnd());
                    } else {
                        throw new UnsupportedOperationException(obj.getType());
                    }
            case "File":
                    String readObj = readFromFile(obj.getValues().getPath());

                    if(obj.getType().toLowerCase().equals("int")) {
                        return getRandomInt(obj.getValues().getStart(), obj.getValues().getEnd());
                    } else if (obj.getType().toLowerCase().equals("double")) {
                        return getRandomDouble(obj.getValues().getStart(), obj.getValues().getEnd());
                    } else if (obj.getType().toLowerCase().equals("string")) {
                        return readObj;
                    } else if (obj.getType().toLowerCase().equals("datetime")) {
                        return new SimpleDateFormat("dd.MM.yyyy").parse(readObj);
                    } else {
                        throw new UnsupportedOperationException(obj.getType());
                    }
            default:
                throw new UnsupportedOperationException(obj.getValues().getSource());
        }
    }

    private MyPair getObjectForSubscription(ConfigObj obj) throws IOException, ParseException {
        if(!includeFieldInSub(obj.getFrequencyInSubscriptions())) {
            return null;
        }
        String operator = generateOperator(obj);
        if(operator != null) {
            Object operand = getObject(obj);
            return MyPair.from(operator, operand);
        }
        return null;
    }

    private boolean includeFieldInSub(int frequency) {
        return getRandomInt("1", "100") < frequency;
    }

    private String generateOperator(ConfigObj obj) {
        String operator = obj.getOperationsFrequency().getOperatorUsed(getRandomInt("1", "100"));
        return operator;
    }

    private Double getRandomDouble(String start, String end) {
        double rangeMin = Double.parseDouble(start);
        double rangeMax = Double.parseDouble(end);

        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    private Long nextLong(Random rng, long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits-val+(n-1) < 0L);
        return val;
    }

    private Integer getRandomInt(String start, String end) {
        int rangeMin = Integer.parseInt(start);
        int rangeMax = Integer.parseInt(end);

        Random r = new Random();
        return r.nextInt(rangeMax-rangeMin + 1) + rangeMin;
    }

    private Date getRandomDate(String start, String end) throws ParseException {
        Date rangeMin = new SimpleDateFormat("dd.MM.yyyy").parse(start);
        Date rangeMax = new SimpleDateFormat("dd.MM.yyyy").parse(end);

        Random random = new Random();
        long randomValue = nextLong(random,rangeMax.getTime() - rangeMin.getTime() + 1) + rangeMin.getTime();
        return new Date(randomValue);
    }

    private String readFromFile(String filePath) throws IOException {
        List<String> lines = getLinesFromFile(filePath);
        int index = getRandomInt("0", String.valueOf(lines.size()-1));

        return lines.get(index);
    }

    private List<String> getLinesFromFile(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath)).collect(Collectors.toList());
    }

    public GeneratorConfig getConfig() {
        return this.config;
    }
}
