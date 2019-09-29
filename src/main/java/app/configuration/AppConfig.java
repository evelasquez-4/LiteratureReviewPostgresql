package app.configuration;


import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient; 
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;



public class AppConfig {
	
	private static final String DBNAME = "dblpDb";
	private static final String HOST = "localhost";
	private static final String COLLECTIONNAME = "documents";
	private static final int PORT = 27017;
	
	// insert db configuration batch
	public static final Integer INSERTNUMBERS = 20;//10000;
	
	
	private static AppConfig instance = new AppConfig();
	private MongoClient mongo = null;
	private Datastore dataStore = null;
	private Morphia morphia = null;
	
	
	public AppConfig() {}
	
	@Bean
	@SuppressWarnings("deprecation")
	public DB getDB() 
	{
		return mongo.getDB(DBNAME);
	}
	
	@Bean
	public DBCollection getCollection() {
		DB db = getDB();
		return db.getCollection(COLLECTIONNAME);
	}
	
	@Bean
	@SuppressWarnings("deprecation")
	public MongoClient getMongo() throws RuntimeException {
		if (mongo == null) {
			
			MongoClientOptions.Builder options = MongoClientOptions.builder()
                    .connectionsPerHost(2)
                    .maxConnectionIdleTime((60 * 1_000))
                    .maxConnectionLifeTime((120 * 1_000));
                    
			MongoClientURI uri = new MongoClientURI("mongodb://"+HOST+":"+PORT+"/"+DBNAME, options);
			
			try {
				mongo = new MongoClient(uri);
				mongo.setWriteConcern(WriteConcern.ACKNOWLEDGED);
	
			} catch (MongoException ex) {
				System.out.println(ex.getMessage());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			
			// To be able to wait for confirmation after writing on the DB
			mongo.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		}
		return mongo;
	}
	
	@Bean
	public Morphia getMorphia() {
		if (morphia == null) {
	
			morphia = new Morphia();
		}

		return morphia;
	}
	
	public Datastore getDatastore() {
		if (dataStore == null) {	
			dataStore = getMorphia().createDatastore(getMongo(), DBNAME);
		}

		return dataStore;
	}
	
	@Bean
	public void init() {
		getMongo();
		getMorphia();
		getDatastore();
	}
	
	@Bean
	public void close() {
		
		if (mongo != null) {
			try {
				mongo.close();
				mongo = null;
				morphia = null;
				dataStore = null;
				
				System.err.println("Shut down connection........");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("mongo object was null, wouldn't close connection");
		}
	}
	
	public static AppConfig getInstance() {
		return instance;
	}
	
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoDbFactory factory = factory();
	    MongoTemplate mongoTemplate = new MongoTemplate(factory);
	    
	    return mongoTemplate;
	}
	
	@Bean
	public MongoDbFactory factory()
	{
		MongoClientOptions.Builder options = MongoClientOptions.builder();
		options.connectTimeout(900000000);
		options.socketTimeout(900000000);
		options.serverSelectionTimeout(-1);
		options.maxWaitTime(900000000);
		
		MongoClientURI uri = new MongoClientURI("mongodb://"+HOST+":"+PORT+"/"+DBNAME, options);
		
		return new SimpleMongoDbFactory(uri);
		
		
	}
	
}
