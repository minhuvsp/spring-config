package com.vsp.rating.db.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class H2Config {

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "com.vsp.rating.soldrate.db.model" });

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "org.hibernate.hbm2ddl.auto");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.format_sql", "true");
		properties.setProperty("hibernate.use_sql_comments", "true");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		em.setJpaProperties(properties);

		return em;
	}
	@Bean
	public DataSource dataSource() {	
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		
		Resource resource = new ClassPathResource("sql/h2setup/h2_ddls.sql");
		
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(resource);
		
		DatabasePopulatorUtils.execute(populator, dataSource);
		
		return dataSource;
	}
	
	@Bean(name = "txManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}
		
	
	@Bean
	public DbSetup dbSetup(final DataSource dataSource){
		return new DbSetup(){

			@Override
			public void setup(StatementRunner runner) {
				
				System.out.println(">>>>>>>>>>>>>DbSetup.setup");
				
				Connection conn = null;
				Statement stmt = null;
				try{
					conn = dataSource.getConnection();
					stmt = conn.createStatement();
					
					runner.run(stmt);
				
				}catch(SQLException se){
					throw new RuntimeException(se);
				}catch(Exception e){
					   throw new RuntimeException(e);
				}finally{
				      try{
				         if(stmt!=null){
				            stmt.close();
				         }
				      }catch(SQLException se2){
				      }
				      try{
				         if(conn!=null){
				            conn.close();
				         }
				      }catch(SQLException se){
				         se.printStackTrace();
				      }
				   }
				
			}
			
			private Set<String> runNames = new HashSet<String>();

			@Override
			public void setupOnce(StatementRunner runner, String name) {
				if(!runNames.contains(name)){
					runNames.add(name);
					setup(runner);
				}
				
			}		
		};
	}

}

