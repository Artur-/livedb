package org.vaadin.artur.livedb.data;

import com.vaadin.flow.spring.annotation.SpringComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import io.r2dbc.spi.ConnectionFactory;

@SpringComponent
public class DatabaseSetup {

    @Bean
    public CommandLineRunner loadData(
            ConnectionFactory connectionFactory,
            ItemRepository itemRepository) {

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.info("Setting up database");
            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
            resourceDatabasePopulator.setSeparator("\n");
            resourceDatabasePopulator.addScript(new ClassPathResource("init.sql"));
            resourceDatabasePopulator.populate(connectionFactory).block();
            
            logger.info("Generating demo data");
            itemRepository.deleteAll().block();
            itemRepository.save(new Item("Carrot", 2)).block();
            itemRepository.save(new Item("Cucumber", 5)).block();
            itemRepository.save(new Item("Tomato", 15)).block();
            itemRepository.save(new Item("Avocado", 666)).block();

            logger.info("Generated demo data");
        };
    }

}
