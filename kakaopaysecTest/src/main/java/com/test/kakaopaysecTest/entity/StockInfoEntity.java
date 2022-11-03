package com.test.kakaopaysecTest.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
@Table(name = "STOCK_INFO", indexes = { @Index(name="indexkey", columnList = "code") ,@Index(columnList="name")})
public class StockInfoEntity {
	@Id
	@GeneratedValue
	private int id;
	
	private int code;
	
	@Column(length=100)
	private String name;
	
	
}
