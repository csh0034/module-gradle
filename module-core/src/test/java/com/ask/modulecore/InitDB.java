package com.ask.modulecore;

import java.io.InputStream;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create", webEnvironment = WebEnvironment.NONE)
@Slf4j
@Tag("init-db")
public class InitDB {

  @Autowired
  Environment environment;

  @Autowired
  private DataSource dataSource;

  @Test
  void init() throws Exception {
    log.info("InitDB.init");
    log.info("spring.jpa.hibernate.ddl-auto : {}", environment.getProperty("spring.jpa.hibernate.ddl-auto"));

    Connection connection = dataSource.getConnection();
    MySqlConnection iDatabaseConnection = new MySqlConnection(connection, "gradle_multi_module");

    InputStream is = new ClassPathResource("data.xml").getInputStream();
    FlatXmlDataSet flatXmlDataSet = new FlatXmlDataSetBuilder().build(is);
    DatabaseOperation.CLEAN_INSERT.execute(iDatabaseConnection, flatXmlDataSet);

    connection.close();
    iDatabaseConnection.close();
  }
}
