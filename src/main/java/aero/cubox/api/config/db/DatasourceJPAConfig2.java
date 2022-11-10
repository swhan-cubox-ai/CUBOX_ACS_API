package aero.cubox.api.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DatasourceJPAConfig2 {

  @Autowired
  private PoolProperties2 poolProperties;

  @Bean
  public DataSource dataSource2() {
    return HikariDatasourceBuilder2.build(poolProperties);
  }

}
