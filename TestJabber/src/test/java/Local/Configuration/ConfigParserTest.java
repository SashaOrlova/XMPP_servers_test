package Local.Configuration;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ConfigParserTest {

    @Test
    public void validateConfig() {
        InstanceConfig instanceConfig = new InstanceConfig("localhost", 3030);

        MainConfig wrongConfig = new MainConfig(
                6,
                -1,
                4,
                new InstanceConfig[] {
                        instanceConfig
                },
                "localhost",
                "localhost",
                0,
                0,
                0
        );

        MainConfig correctConfig = new MainConfig(
                1,
                100,
                4,
                new InstanceConfig[] {
                        instanceConfig,
                },
                "localhost",
                "localhost",
                0,
                100,
                100000
        );

        MainConfig wrongInstanceNumberConfig = new MainConfig(
                1,
                100,
                4,
                new InstanceConfig[] {
                        instanceConfig,
                        instanceConfig,
                        instanceConfig,
                        instanceConfig
                },
                "localhost",
                "localhost",
                0,
                100,
                100000
        );

        assertTrue(ConfigParser.validateConfig(correctConfig));
        assertFalse(ConfigParser.validateConfig(wrongConfig));
        assertFalse(ConfigParser.validateConfig(wrongInstanceNumberConfig));
    }

    @Test
    public void validateIPaddress() {
        String rightIP = "172.16.254.1";
        String wrongIP = "900.126.254.1";
        String localhostIP = "localhost";

        assertTrue(ConfigParser.validateIPaddress(rightIP));
        assertFalse(ConfigParser.validateIPaddress(wrongIP));
        assertTrue(ConfigParser.validateIPaddress(localhostIP));
    }

    @Test
    public void readConfigExample() throws IOException {
        MainConfig config = ConfigParser.readConfig("src/test/resources/config_example.conf");

        assertTrue(ConfigParser.validateConfig(config));
    }
}