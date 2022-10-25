package aero.cubox.api.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DatasourceJPAConfig {

  @Autowired
  private PoolProperties poolProperties;

  @Bean
  @Primary
  public DataSource dataSource() {
    return HikariDatasourceBuilder.build(poolProperties);
  }

}
