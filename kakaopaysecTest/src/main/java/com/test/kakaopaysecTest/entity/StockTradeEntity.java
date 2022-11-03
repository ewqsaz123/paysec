package com.test.kakaopaysecTest.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="STOCK_TRADE", indexes = {@Index(name="indexkey", columnList = "tdate"), @Index(columnList="code"), @Index(columnList="code,id,price")})
@Entity
public class StockTradeEntity {
//	@EmbeddedId
//	private StockTradeId id;
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "code")
	private int code;
	
	
	@Column(name = "tdate")
	@Temporal(TemporalType.DATE)
	private Date tdate;
	
	private int volume;
	
	private int price;
	
}
