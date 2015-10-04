package com.isbank.account.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.isbank.account.entity.GoldState;

public class APDao {

	private final String DB_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	private final String DB_CONNECTION = "jdbc:db2://ua0bdb2.isbank:451/UA0BPLOC";
	private final String DB_USER = "is96689";
	private final String DB_PASSWORD = "K1K1K1K1";

	public List<GoldState> retrieveGoldStateOfBranch(Integer branchCode, Timestamp timestamp,
			List<String> accountingType) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		List<GoldState> goldStateList = new ArrayList<GoldState>();
		BigDecimal totalDebitAmountTry = BigDecimal.ZERO;
		BigDecimal totalCreditAmountTry = BigDecimal.ZERO;
		BigDecimal totalDebitAmount = BigDecimal.ZERO;
		BigDecimal totalCreditAmount = BigDecimal.ZERO;

		dbConnection = getDBConnection();
		if (dbConnection == null)
			throw new SQLException("Can't get database connection");

		try {

			for (int i = 0; i < accountingType.size(); i++) {
				String selectSQL = "SELECT "
						+ "SUM(GOLD_DB_AMT) AS DEBIT_AMOUNT,SUM(GOLD_CRED_AMT) AS CREDIT_AMOUNT,SUM(GOLD_DB_AMT_TRY) AS DEBIT_AMOUNT_TRY,SUM(GOLD_CRED_AMT_TRY) AS CREDIT_AMOUNT_TRY "
						+ "FROM SCACH.GOLD_OPERATION " + "WHERE GOLD_BRANCH_CODE=" + branchCode + " AND GOLD_TSTP <="
						+ "'" + timestamp + "'" + "AND GOLD_ACCOUNTING_TYPE=" + "'" + accountingType.get(i) + "'";

				preparedStatement = dbConnection.prepareStatement(selectSQL);
				ResultSet rs = preparedStatement.executeQuery();
				
				String tryAccType="";
				
				if (accountingType.get(i).equals("28500"))
					tryAccType="284000";
				else if (accountingType.get(i).equals("28501"))
					tryAccType="284010";
				else if (accountingType.get(i).equals("28502"))
					tryAccType="284020";
				else if (accountingType.get(i).equals("28503"))
					tryAccType="284030";

				while (rs.next()) {
					
					GoldState goldState =getGoldState(rs.getBigDecimal("DEBIT_AMOUNT"), rs.getBigDecimal("CREDIT_AMOUNT"),accountingType.get(i));
					goldStateList.add(goldState);

					if (accountingType.get(i).equals("28503") == false) {
						totalDebitAmount=totalDebitAmount.add(goldState.getDebitAmount());
						totalCreditAmount=totalCreditAmount.add(goldState.getCreditAmount());
					}

					GoldState goldStateTry = getGoldState(rs.getBigDecimal("DEBIT_AMOUNT_TRY"), rs.getBigDecimal("CREDIT_AMOUNT_TRY"),tryAccType);
					goldStateList.add(goldStateTry);

					if (goldStateTry.getType().equals("284030") == false) {
						totalDebitAmountTry=totalDebitAmountTry.add(goldStateTry.getDebitAmount());
						totalCreditAmountTry=totalCreditAmountTry.add(goldStateTry.getCreditAmount());
					}

				}
			}
			
			GoldState goldStateGramAmount = getGoldState(totalDebitAmount, totalCreditAmount, "XAU GİŞE GRAM  TUTAR");
			goldStateList.add(goldStateGramAmount);

			GoldState goldStateGramAmountTry = getGoldState(totalDebitAmountTry, totalCreditAmountTry, "TRY TOPLAM MALİYET");	
			goldStateList.add(goldStateGramAmountTry);
			
			GoldState goldStateCostUnit=getGoldState(totalDebitAmountTry.divide(totalDebitAmount,5,RoundingMode.HALF_EVEN), totalCreditAmountTry.divide(totalCreditAmount,5,RoundingMode.HALF_EVEN), "TRY BİRİM MALİYET");	
			goldStateList.add(goldStateCostUnit);
			
			GoldState goldStateUsd=getGoldState(BigDecimal.ZERO, BigDecimal.ZERO, "USD 284030");		
			goldStateList.add(goldStateUsd);
	
			GoldState goldStateXau=getGoldState(BigDecimal.ZERO, BigDecimal.ZERO, "XAU 019000");		
			goldStateList.add(goldStateXau);
			

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return goldStateList;

	}

	private Connection getDBConnection() {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println("Driver Bulunamadı :" + e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	private GoldState getGoldState(BigDecimal dbAmount,BigDecimal crAmount,String accType){
		
		GoldState goldState = new GoldState();

		BigDecimal d = (dbAmount == null ? BigDecimal.ZERO
				: dbAmount);
		BigDecimal c = (crAmount == null ? BigDecimal.ZERO
				: crAmount);
		BigDecimal b = c.subtract(d);

		goldState.setType(accType);
		goldState.setCreditAmount(c);
		goldState.setDebitAmount(d);
		goldState.setBalance(b);
		
		return goldState;
	}
	
}
