package com.vsp.rating.soldrate.dao.functional;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.vsp.api.rating.model.ContractType;
import com.vsp.api.rating.model.RateStructure;
import com.vsp.api.rating.model.soldrate.BillType;
import com.vsp.api.rating.model.soldrate.DivisionType;
import com.vsp.il.util.Preferences;
import com.vsp.rating.db.config.DBConfig;
import com.vsp.rating.soldrate.db.model.Benefit;
import com.vsp.rating.soldrate.db.model.BillingRateFeeAssociation;
import com.vsp.rating.soldrate.db.model.RecordStatusEnum;
import com.vsp.rating.soldrate.db.model.SoldRate;
import com.vsp.rating.soldrate.db.model.SoldRateTier;
import com.vsp.rating.soldrate.service.PersistenceRetrieveService;
import com.vsp.rating.soldrate.service.PersistenceRetrieveServiceImpl;
import com.vsp.rating.soldrate.service.PersistenceSaveService;


public class DBConfigBeanJava
{
	private PersistenceSaveService persistenceSaveService ;

	public DBConfigBeanJava () {
		init();
	}
	
	public void init()
	{
		System.out.println("------------------------------------------------------");
		System.out.println("Begin Execution of " + getClass().getName());

        if (!Preferences.initialized()) {
        	Preferences.initialize("TestDB", "./src/main/resources/META-INF");
        }
        
		ApplicationContext context = new AnnotationConfigApplicationContext(DBConfig.class);

		persistenceSaveService = context.getBean(PersistenceSaveService.class);

	}

	public void testCreateWithSoldRateTier() throws Exception {
//		logger.debug("testCreateWithSoldRateTier()");
		
		SoldRateTier soldRateTier = new SoldRateTier();
		soldRateTier.setAmount(new Float(100));
		soldRateTier.setBenefitName("Vision Plan");
		soldRateTier.setTierName("Tier1");
		Set<SoldRateTier> rateTiers = new HashSet<SoldRateTier>();
		rateTiers.add(soldRateTier);
		
		SoldRate soldRate = new SoldRate();
//		soldRate.setSoldRateId(persistenceSaveService.generateSoldRateId());
		soldRate.setSoldRateId("000000059956");
		soldRate.setContractType(ContractType.Combo);
		soldRate.setRateType("rateType");
		soldRate.setRateStructure(RateStructure.TwoRate);
		Calendar calendar = new GregorianCalendar(2015,0,1);
		soldRate.setEffectiveDate(calendar.getTime());
		calendar = new GregorianCalendar(9999,11,31);
		soldRate.setLimitDate(calendar.getTime());
		soldRate.setStatus(RecordStatusEnum.ACTIVE.getStatusCode());
		soldRate.setUpdateId("JUnit");
		soldRate.setSoldRateTierSet(rateTiers);

		SoldRate  newSoldRate = persistenceSaveService.persistSoldRate(soldRate);
		Assert.assertNotNull(newSoldRate);
		Assert.assertNotNull(newSoldRate.getId());
	}


	public static void main(String[] args) throws Exception {

		DBConfigBeanJava runner = new DBConfigBeanJava();
		
		runner.testCreateWithSoldRateTier();
       
		System.exit(0);
	}

}
