package com.vsp.api.product.cassandra.load;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.dse.auth.DsePlainTextAuthProvider;
import com.vsp.camel.route.BaseRouteBuilder;
import com.vsp.il.util.Preferences;
import com.vsp.product.dao.CassandraRetrieveDao;
import com.vsp.product.dao.CassandraSearchDao;
import com.vsp.product.dao.ClientProductsRepository;
import com.vsp.product.dao.RetrieveClientProductDao;
import com.vsp.product.dao.SearchClientProductDao;

@Configuration("product-api-srvclientproduct-config")
@ComponentScan(basePackages = { "com.vsp.product.mapper.cassandra.retrieve" }) // this is for com.vsp.product.mapper.cassandra.retrieve.Defaults implements ApplicationContextAware
public class CassandraConfig extends BaseRouteBuilder {
	private static final String cacheBeanName = "cacheLifecycle";
	private static boolean cacheOn = Preferences.getBoolean("command", "product.cache.on");
	private static boolean invalidateOn = Preferences.getBoolean("command", "product.cache.invalidate");
	private static boolean initialLoad = Preferences.getBoolean("command", "product.initialLoad");

	@Bean(name = "searchRepository")
	public ClientProductsRepository searchRepository() {
		ClientProductsRepository repository = new ClientProductsRepository();
		String contactpoints = Preferences.getString("dao", "cassandra.contactpoints");
		String port = Preferences.getString("dao", "cassandra.port");
		String keyspace = Preferences.getString("dao", "cassandra.keyspace");
		String username = Preferences.getString("dao", "cassandra.username");
		String password = Preferences.getString("dao", "cassandra.password");
		String[] contactpointlist = contactpoints.split(",");

//		if (isCassandraSearchOn()) {
			Builder builder = Cluster.builder();
			builder.addContactPoints(contactpointlist);
			builder.withAuthProvider(new DsePlainTextAuthProvider(username, password));
			builder.withPort(Integer.parseInt(port));
			builder.withRetryPolicy(DefaultRetryPolicy.INSTANCE);
			builder.withLoadBalancingPolicy(new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build()));
			Cluster cluster = builder.build();
			PoolingOptions poolingOptions = cluster.getConfiguration().getPoolingOptions();
			poolingOptions.setHeartbeatIntervalSeconds(10);
			Session session = cluster.connect(keyspace);
			SearchClientProductDao searchDao = new CassandraSearchDao(session);
			RetrieveClientProductDao retrieveDao = new CassandraRetrieveDao(session);

			repository = new ClientProductsRepository(searchDao, retrieveDao);
//		}
		return repository;
	}

	private boolean isCassandraSearchOn() {
		return Boolean.parseBoolean(Preferences.get("dao", "isCassandraOn"))
				|| Boolean.parseBoolean(Preferences.get("command", "product.initialLoad"));
	}

}
