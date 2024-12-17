package com.mars.NangPaGo.simple;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Disabled("전체 테스트에서 제외 (사유: 단순 연결 테스트)")
@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @DisplayName("단순 DB 연결 테스트")
    @Test
    void simpleDatabaseConnectionTest() throws SQLException {
        Connection connection = dataSource.getConnection();
        assertThat(connection).isNotNull();
        connection.close();
    }
}
