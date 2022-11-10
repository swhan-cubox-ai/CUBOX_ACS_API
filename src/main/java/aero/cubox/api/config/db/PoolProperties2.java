package aero.cubox.api.config.db;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource2")
@Component
public class PoolProperties2 implements Serializable {

  private String username;
  private String password;
  private String driverClassName;
  private String jdbcUrl;
  private Hikari hikari;


  @ToString
  @Getter
  @Setter
  public static class Hikari {

    private boolean autoCommit;
    private int maximumPoolSize;
    private int minimumIdle;
  }

}