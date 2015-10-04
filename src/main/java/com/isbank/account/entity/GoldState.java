package com.isbank.account.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class GoldState {
	
	private String type;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private BigDecimal balance;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	

}
