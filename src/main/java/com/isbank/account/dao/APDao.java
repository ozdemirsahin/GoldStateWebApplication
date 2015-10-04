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

	public List<GoldState> retrieveGoldStateOfBranch(Integer branchCode, Timestamp timeStamp,
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
						+ "'" + timeStamp + "'" + "AND GOLD_ACCOUNTING_TYPE=" + "'" + accountingType.get(i) + "'";

				preparedStatement = dbConnection.prepareStatement(selectSQL);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					GoldState goldState = new GoldState();

					BigDecimal debitAmount = (rs.getBigDecimal("DEBIT_AMOUNT") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("DEBIT_AMOUNT"));
					BigDecimal creditAmount = (rs.getBigDecimal("CREDIT_AMOUNT") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("CREDIT_AMOUNT"));
					BigDecimal balance = creditAmount.subtract(debitAmount);

					goldState.setType(accountingType.get(i));
					goldState.setCreditAmount(creditAmount);
					goldState.setDebitAmount(debitAmount);
					goldState.setBalance(balance);

					goldStateList.add(goldState);

					if (accountingType.get(i).equals("28503") == false) {
						totalDebitAmount=totalDebitAmount.add(debitAmount);
						totalCreditAmount=totalCreditAmount.add(creditAmount);
					}

					GoldState goldStateTry = new GoldState();

					BigDecimal debitAmountTry = (rs.getBigDecimal("DEBIT_AMOUNT_TRY") == null ? new BigDecimal(0)
							: rs.getBigDecimal("DEBIT_AMOUNT_TRY"));
					BigDecimal creditAmountTry = (rs.getBigDecimal("CREDIT_AMOUNT_TRY") == null ? new BigDecimal(0)
							: rs.getBigDecimal("CREDIT_AMOUNT_TRY"));
					BigDecimal balanceTry = creditAmountTry.subtract(debitAmountTry);

					if (accountingType.get(i).equals("28500"))
						goldStateTry.setType("284000");
					else if (accountingType.get(i).equals("28501"))
						goldStateTry.setType("284010");
					else if (accountingType.get(i).equals("28502"))
						goldStateTry.setType("284020");
					else if (accountingType.get(i).equals("28503"))
						goldStateTry.setType("284030");

					goldStateTry.setCreditAmount(creditAmountTry);
					goldStateTry.setDebitAmount(debitAmountTry);
					goldStateTry.setBalance(balanceTry);

					goldStateList.add(goldStateTry);

					if (goldStateTry.getType().equals("284030") == false) {
						totalDebitAmountTry=totalDebitAmountTry.add(debitAmountTry);
						totalCreditAmountTry=totalCreditAmountTry.add(creditAmountTry);
					}

				}
			}
			GoldState goldStateGramAmount = new GoldState();
			
			goldStateGramAmount.setCreditAmount(totalCreditAmount);
			goldStateGramAmount.setDebitAmount(totalDebitAmount);
			goldStateGramAmount.setBalance(totalCreditAmount.subtract(totalDebitAmount));
			goldStateGramAmount.setType("XAU GİŞE GRAM  TUTAR");

			goldStateList.add(goldStateGramAmount);

			GoldState goldStateGramAmountTry = new GoldState();
			goldStateGramAmountTry.setCreditAmount(totalCreditAmountTry);
			goldStateGramAmountTry.setDebitAmount(totalDebitAmountTry);
			goldStateGramAmountTry.setBalance(totalCreditAmountTry.subtract(totalDebitAmountTry));
			goldStateGramAmountTry.setType("TRY TOPLAM MALİYET");
			
			goldStateList.add(goldStateGramAmountTry);
			
			GoldState goldStateCostUnit=new GoldState();
			goldStateCostUnit.setCreditAmount(totalCreditAmountTry.divide(totalCreditAmount,5,RoundingMode.HALF_EVEN));
			goldStateCostUnit.setDebitAmount(totalDebitAmountTry.divide(totalDebitAmount,5,RoundingMode.HALF_EVEN));
			goldStateCostUnit.setBalance(goldStateCostUnit.getCreditAmount().subtract(goldStateCostUnit.getDebitAmount()));
			goldStateCostUnit.setType("TRY BİRİM MALİYET");
			
			goldStateList.add(goldStateCostUnit);
			
			GoldState goldStateUsd=new GoldState();
			goldStateUsd.setCreditAmount(BigDecimal.ZERO);
			goldStateUsd.setDebitAmount(BigDecimal.ZERO);
			goldStateUsd.setBalance(BigDecimal.ZERO);
			goldStateUsd.setType("USD 284030");
			
			goldStateList.add(goldStateUsd);
	
			GoldState goldStateXau=new GoldState();
			goldStateXau.setCreditAmount(BigDecimal.ZERO);
			goldStateXau.setDebitAmount(BigDecimal.ZERO);
			goldStateXau.setBalance(BigDecimal.ZERO);
			goldStateXau.setType("XAU 019000");
			
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
