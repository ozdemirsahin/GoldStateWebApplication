package com.isbank.account.controller;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;

import com.isbank.account.entity.GoldState;
import com.isbank.account.service.GoldService;

@ManagedBean(name = "apcontroller")
@SessionScoped
public class APController {

	private List<GoldState> goldStates;
	private String queryType;
	private Integer branchCode;
	private Date date;
	private Date time;
	private List<String> types;
	private String selectedType;


	@ManagedProperty("#{goldService}")
	private GoldService service;

	@PostConstruct
	public void init() {
		types = new ArrayList<String>();
		types.add("28500");
		types.add("28501");
		types.add("28502");
		types.add("28503");
	}

	public List<GoldState> retrieveGoldState() throws SQLException {
		
		Timestamp ts=createTimestamp(date, time);
		if (queryType.equals("B")) {
		this.goldStates=service.retrieveGoldStatesOfBank(types,ts);
		} else {
		this.goldStates=service.retrieveGoldStatesOfBranch(types, branchCode,ts);
		}
		System.out.println("return size :" +goldStates.size());
	
		return this.goldStates;
	}

	public List<GoldState> getGoldStates() {
		return goldStates;
	}

	public String getQueryType() {
		return queryType;
	}

	public Integer getBranchCode() {
		return branchCode;
	}

	public Date getDate() {
		return date;
	}

	public Date getTime() {
		return time;
	}

	public List<String> getTypes() {
		return types;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public GoldService getService() {
		return service;
	}

	public void setGoldStates(List<GoldState> goldStates) {
		this.goldStates = goldStates;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public void setBranchCode(Integer branchCode) {
		this.branchCode = branchCode;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	public void setService(GoldService service) {
		this.service = service;
	}
	
	public void reset() {
        RequestContext.getCurrentInstance().reset("form:panel");
        RequestContext.getCurrentInstance().reset("form:dataTable");
    }

	public Timestamp createTimestamp(Date date,Date time){
		Calendar calA = Calendar.getInstance();
		calA.setTime(date);

		Calendar calB = Calendar.getInstance();
		calB.setTime(time);

		calA.set(Calendar.HOUR_OF_DAY, calB.get(Calendar.HOUR_OF_DAY));
		calA.set(Calendar.MINUTE, calB.get(Calendar.MINUTE));
		calA.set(Calendar.SECOND, calB.get(Calendar.SECOND));
		calA.set(Calendar.MILLISECOND, calB.get(Calendar.MILLISECOND));
		
		System.out.println("Merge Date :"+calA.getTime());
		
		return new Timestamp(calA.getTime().getTime());
		
	}

}
