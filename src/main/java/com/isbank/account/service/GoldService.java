package com.isbank.account.service;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.isbank.account.dao.APDao;
import com.isbank.account.entity.GoldState;

@ManagedBean(name = "goldService")
@SessionScoped
public class GoldService{
	
	APDao dao=new APDao();
	
	public List<GoldState> retrieveGoldState(String queryType,List<String> typs,Integer branchCode,Timestamp timestamp) throws SQLException{
		 List<GoldState> goldStates = new ArrayList<GoldState>();

			if (queryType.equals("B")) {
				goldStates = retrieveGoldStatesOfBank(typs,timestamp);
			} else {
			    goldStates =retrieveGoldStatesOfBranch(typs, branchCode,timestamp);
			}
        return goldStates;
	}
	
	public List<GoldState> retrieveGoldStatesOfBranch(List<String> typs,Integer bc,Timestamp ts) throws SQLException{
        List<GoldState> list = new ArrayList<GoldState>();
         list=  dao.retrieveGoldStateOfBranch(bc, ts, typs);
        return list;
    }
	
	public List<GoldState> retrieveGoldStatesOfBank(List<String> typs,Timestamp ts) throws SQLException{
        List<GoldState> list = new ArrayList<GoldState>();
        list=dao.retrieveGoldStateOfBank(ts, typs);
        return list;
    }

}
