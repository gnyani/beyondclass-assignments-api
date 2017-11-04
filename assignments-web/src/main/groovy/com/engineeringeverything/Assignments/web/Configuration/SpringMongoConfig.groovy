package com.engineeringeverything.Assignments.web.Configuration

import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
@EnableConfigurationProperties(MongoProperties.class)
public class SpringMongoConfig extends AbstractMongoConfiguration {


	@Autowired
	private MongoProperties props;



	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception{

		String host = props.getHost();
		int port = props.getPort();
		String db   = props.getDatabase();

		return new SimpleMongoDbFactory(new MongoClient(host, port), db);
	}

	 @Bean(name = "mongoTemplate")
	 public MongoTemplate mongoTemplate() throws Exception {

		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		 ((MappingMongoConverter)
				 mongoTemplate.getConverter()).setMapKeyDotReplacement("#dot#");

		 return mongoTemplate;

	}
	@Override
	protected String getDatabaseName() {
		return "mydatabase";
	}


    @Bean(name = "assignments")
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter(),"assignments");
    }


	@Override
	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient(props.getHost());
	}
		
}