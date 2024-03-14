package api.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigUtils {

    static Map<String, String> configs = new HashMap<String, String>();

    public static Map<String, String> getLoadedConfigurations() {
        if (!configs.isEmpty()) {
            return configs;
        } else {
            return getConfigForExecutionEnvironment();
        }
    }

    private static Map<String, String> getConfigForExecutionEnvironment() {
        String runEnvironment = System.getProperty("runenv");
        if (runEnvironment == null) {
            configs = loadConfigs();
        } else {
            try {
                String path =
                        System.getProperty(
                                "configfile", "src/test/resources/" + runEnvironment + ".properties");
                configs = loadConfigs(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        configs = overrideIfSpecifiedAsEnvVariable(configs);

//        System.out.println(configs);
        return configs;
    }

    public static void clearConfigs() {
        configs.clear();
    }

    private static Map<String, String> overrideIfSpecifiedAsEnvVariable(
            Map<String, String> mergedEnvConfig) {
        for (String key : mergedEnvConfig.keySet()) {
            String updatedValue =
                    (null != System.getenv(key)) ? System.getenv(key) : mergedEnvConfig.get(key);
            mergedEnvConfig.put(key, updatedValue);
        }
        return mergedEnvConfig;
    }

    private static Map<String, String> loadConfigs() {
        Map<String, String> propertyMap = new HashMap<String, String>();
        Properties p = new Properties();
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream("src/test/resources/config.properties");
            p.load(inputStream);
            propertyMap.putAll(
                    p.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
            inputStream.close();
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        return propertyMap;
    }

    public static Map<String, String> loadConfigs(String file) throws IOException {
        Map<String, String> propertyMap = new HashMap<String, String>();
        Properties p = new Properties();
        FileInputStream inputStream = new FileInputStream(file);
        p.load(inputStream);
        propertyMap.putAll(
                p.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
        inputStream.close();
        return propertyMap;
    }

    public static String getConfig(String key) {
        return getLoadedConfigurations().get(key);
    }
}
