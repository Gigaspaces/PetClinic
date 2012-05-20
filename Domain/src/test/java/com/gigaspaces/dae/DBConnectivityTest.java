package com.gigaspaces.dae;

import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.assertNotNull;

public class DBConnectivityTest {
    @Test
    public void testDBConnectivity() throws IOException, ClassNotFoundException, SQLException {
        Properties properties= new Properties();
        properties.load(this.getClass().getResourceAsStream("/jdbc.properties"));
        assertNotNull(properties.getProperty("hibernate-dialect"));

        Class.forName(properties.getProperty("data-source-driver"));
        java.sql.Connection connection= DriverManager.getConnection(properties.getProperty("data-source-url"),
                properties.getProperty("data-source-username"),
                properties.getProperty("data-source-password"));
        connection.close();
    }
}
