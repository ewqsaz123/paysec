package com.test.kakaopaysecTest.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

//import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
public class DatabaseConfig {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Bean
	JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

}
