package com.test.kakaopaysecTest.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STOCK_PRICE", indexes = @Index(name="indexkey", columnList = "pdate,code"))
public class StockPriceEntity {
//	@EmbeddedId
//	private StockPriceId id;
//	
	@Id
	@GeneratedValue
	private int id;
	
	
	@Column(name = "code")
	private int code;
	
	@Column(name = "pdate")
	@Temporal(TemporalType.DATE)
	private Date pdate;
	
	private int price;
	
	
	
}
