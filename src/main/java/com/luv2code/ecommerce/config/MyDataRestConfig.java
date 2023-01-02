package com.luv2code.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.luv2code.ecommerce.entity.Country;
import com.luv2code.ecommerce.entity.Order;
import com.luv2code.ecommerce.entity.Product;
import com.luv2code.ecommerce.entity.ProductCategory;
import com.luv2code.ecommerce.entity.State;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{
	
	@Value("${allowed.origins}")
	private String[] allowedOrigings;
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public void configureRepositoryRestConfiguration(
			RepositoryRestConfiguration config, CorsRegistry cors) {
		
		HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, 
				HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH};
		
		// disable HTTP methods for product: PUT, POST, DELETE
		disabledHttpMethods(Product.class, config, theUnsupportedActions);
		disabledHttpMethods(ProductCategory.class, config, theUnsupportedActions);
		disabledHttpMethods(Country.class, config, theUnsupportedActions);
		disabledHttpMethods(State.class, config, theUnsupportedActions);
		disabledHttpMethods(Order.class, config, theUnsupportedActions); 
		// call an internal helper method
		exposeIds(config);
		
		// configure the CORS mapping
		cors.addMapping(config.getBasePath()+ "/**").allowedOrigins(allowedOrigings);
	}

	private void disabledHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
		config.getExposureConfiguration()
				.forDomainType(theClass)
				.withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
				.withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
	}

	private void exposeIds(RepositoryRestConfiguration config) {
		
		// expose entity ids
		
		// get a list of all entity classes from the entity manager
		Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
		
		// create an array of the entity types
		List<Class> entityClasses = new ArrayList<>();
		
		// get the entity types for the entities
		for (EntityType entityType: entities) {
			entityClasses.add(entityType.getJavaType());
		}
		
		// expose the entity Ids for the array of entity/ domain types
		Class[] domainTypes = entityClasses.toArray(new Class[0]);
		config.exposeIdsFor(domainTypes);
	}
}
