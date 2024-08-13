package cm.twentysix.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@EnableMongoRepositories(basePackages = {"cm.twentysix.product.domain.repository"})
@Configuration
public class MongoConfig {

}
