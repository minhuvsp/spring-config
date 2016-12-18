package com.vsp.api.product.cassandra.load;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsp.api.product.model.ClientProduct;
import com.vsp.il.util.Preferences;
import com.vsp.jaxrs.provider.VspObjectMapperFactory;
import com.vsp.product.dao.ClientProductsRepository;

public class LoadCassandraFromCache {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private static final String SUCCESS = "Success";
	private static final String DELIMTER = ":";
	
//	@Autowired
	private ClientProductsRepository searchRepository;

	private String path;

	private int numFileInputLines = 0;
	private int numLinesProcessed = 0;

	public LoadCassandraFromCache() {
		init();
	}
		
	public void init() {
		ApplicationContext context = new AnnotationConfigApplicationContext(CassandraConfig.class);

		searchRepository = context.getBean(ClientProductsRepository.class);
	}
	
	private String saveProductToCassandra(ClientProduct cp) 
	{
		String result = SUCCESS;

		searchRepository.updateRetrieveTables(cp);

		return result;
	}

    private ClientProduct fromJson(String value) throws IOException {
        ObjectMapper objectMapper = VspObjectMapperFactory.buildJsonMapper();
        return objectMapper.readValue(value, ClientProduct.class);
    }
    
    private String getContentFromFile(String fileName){
     	StringBuilder buff = new StringBuilder();
    	try {
    	      BufferedReader input =  new BufferedReader(new FileReader(path + "/" + fileName));
    	      try {
    	        String line = null; 
     	        while (( line = input.readLine()) != null){
    	          buff.append(line);
    	        }
    	      }
    	      finally {
    	        input.close();
    	      }
    	    }
    	    catch (IOException ex){
    	    	logger.error("IO Exception trying to read cache file " + fileName + ex);
    	    }
    	
    	return buff.toString();
    }
    
	private ClientProduct readProductFromCache(String filename) 
	{
		ClientProduct result = null;
	   	String jsonProduct = getContentFromFile(filename);
    	try {
    		result = fromJson(jsonProduct);
		} catch (IOException e) {
			logger.error("IO Exception trying to retrieve from cache " + filename + e);
		}
    	
 		return result;
	}
	
	private String processCacheFile(String filename) 
	{
		StringBuffer result = new StringBuffer(filename).append(DELIMTER);
		if (filename != null && filename.length() > 0) {
			ClientProduct cp = readProductFromCache(filename);	
			
			if (cp != null) {
				result.append(saveProductToCassandra(cp));
				
				numLinesProcessed++;
			}
		}		
		return result.toString();
	}
	
	public void execute(String[] args) {

		logger.info("Start Execution ...");
		
		path = args[0];
		String input_file = args[1];

		try {
			List<String> fileInput = Files.readAllLines(Paths.get(path + "/" + input_file), StandardCharsets.UTF_8);
			numFileInputLines = fileInput.size();
			logger.info("# of file input lines=" + numFileInputLines);

			Long t1 = System.currentTimeMillis();

			List<String> results = fileInput.parallelStream()
					.map(string -> processCacheFile(string))
					.collect(Collectors.toList());
			
			Long t2 = System.currentTimeMillis();        
			Long executionTime = t2 - t1;
			
			logger.info("rerieveProduct took {} ms", executionTime);
			logger.info("average time {} ms", executionTime / numLinesProcessed);

			String result_file = path + "/output-cacheload.txt";
			Files.write(Paths.get(result_file), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Execution Completed.");
	}
	
	/* 
	 * program arguments: path file preferences
	 * path: directory contains cache files
	 * file: file contains list of cache file names
	 * preferences: directory contains preferences files like dao.preferences with cassandra.port, etc.
	 * A sample program arguments: "C:/LocalWAS/cache/product" "input-cacheload.txt" "C:/LocalWAS/preferences/cassandraload"
	 * 
	 * A sample VM argument: handle large files by increasing the default JVM size 64m of stand-alone java application
	 * and adjust of number of threads by changing the value of parallelism=#
	 * -Denv=LOCAL -Xms64m -Xmx512m -Djava.util.concurrent.ForkJoinPool.common.parallelism=16
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("\n	Usage: path file preferences\n");
			System.exit(1);
		}
		
		if (!Preferences.initialized()) {
			Preferences.initialize("cassandraload", args[2]); 
		}

        LoadCassandraFromCache runner = new LoadCassandraFromCache();
		runner.execute(args);
		       
		System.exit(0);
	}
}
