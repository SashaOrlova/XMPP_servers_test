package Local.Configuration;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse and validate config from file
 */
public class ConfigParser {
    private static final String CONFIG_NAME = "local.conf";

    public static MainConfig readConfig() throws IOException {
        return readConfig(ConfigParser.CONFIG_NAME);
    }

    public static MainConfig readConfig(String fileName) throws IOException {
        String configContent = new String(Files.readAllBytes(Paths.get(fileName)));
        return (new Gson()).fromJson(configContent, MainConfig.class);
    }

    /**
     * check is config valid
     */
    public static boolean validateConfig(MainConfig config) {
        boolean allIPCorrect = true;
        for (InstanceConfig instance: config.getInstances()) {
            allIPCorrect &= ConfigParser.validateIPaddress(instance.getHost());
        }

        return config.getInstances() != null &&
                config.getInstanceCount() != 0 &&
                config.getInstanceCount() == config.getInstances().length &&
                config.getSendingMessagesCount() != 0 &&
                config.getUsersCount() != 0 &&
                allIPCorrect;
    }

    /**
     * check is ip valid
     * @param ipAddress
     * @return
     */
    public static boolean validateIPaddress(String ipAddress) {
        if (ipAddress.equals("localhost")) {
            return true;
        }

        Pattern ipRegex = Pattern.compile("^(?=.*[^\\.]$)((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.?){4}$");
        Matcher regexMetch = ipRegex.matcher(ipAddress);
        return regexMetch.matches();
    }
}
