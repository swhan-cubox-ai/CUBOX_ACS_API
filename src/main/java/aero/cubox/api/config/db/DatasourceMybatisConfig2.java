package aero.cubox.api.config.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "aero.cubox.api.mdm" }, sqlSessionFactoryRef = "db2SqlSessionFactory2")
public class DatasourceMybatisConfig2 {

        // @Autowired
        // private PoolProperties poolProperties;

        // @Bean(name = "db2DataSource")
        // public DataSource db2DataSource() {
        // return HikariDatasourceBuilder.toJson(poolProperties);
        // }

        @Bean(name = "db2SqlSessionFactory2")
        public SqlSessionFactory db2SqlSessionFactory2(@Qualifier("dataSource2") DataSource db2DataSource,
                        ApplicationContext applicationContext) throws Exception {
                SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
                sqlSessionFactoryBean.setDataSource(db2DataSource);
                sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));
                sqlSessionFactoryBean
                                .setTypeHandlers(new TypeHandler[] { new org.apache.ibatis.type.LocalDateTypeHandler()
                                });

                Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml");
                sqlSessionFactoryBean.setConfigLocation(myBatisConfig);

                return sqlSessionFactoryBean.getObject();
        }

        @Bean(name = "db2SqlSessionTemplate2")
        public SqlSessionTemplate db2SqlSessionTemplate2(SqlSessionFactory db2SqlSessionFactory) throws Exception {
                return new SqlSessionTemplate(db2SqlSessionFactory);
        }
}