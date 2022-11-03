package com.test.kakaopaysecTest.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STOCK_HIT" , indexes = {@Index(name="indexkey", columnList = "hdate,code"), @Index(columnList="hdate,hit")})
public class StockHitEntity {
	@Id
	@GeneratedValue
	private int id;
	
	private int code;
	
	@Temporal(TemporalType.DATE)
	private Date hdate;
	
	private int hit;
	
}
