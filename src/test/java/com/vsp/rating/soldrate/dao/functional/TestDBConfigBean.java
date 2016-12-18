package com.vsp.rating.soldrate.dao.functional;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
import com.vsp.rating.soldrate.service.PersistenceSaveService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={
//		TestDBConfigBean.Config.class // move Config out as its own class
		DBConfig.class
})
@Transactional()
@TransactionConfiguration(defaultRollback=true) // this line is working with annotation @Transactional() and default transaction in PersistenceSaveService
public class TestDBConfigBean
{
	@Autowired
	private PersistenceSaveService persistenceSaveService = null;

	@Autowired
	private PersistenceRetrieveService persistenceRetrieveService = null;

	@Before
	public void init()
	{
		System.out.println("------------------------------------------------------");
		System.out.println("Begin Execution of " + getClass().getName());

        if (!Preferences.initialized()) {
        	Preferences.initialize("TestDB", "./src/main/resources/META-INF");
        }
	}

	@After
	public void cleanup()
	{
		System.out.println("End Execution of " + getClass().getName());
		System.out.println("------------------------------------------------------");
	}

//	@Ignore
	@Test
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

	@Ignore
	@Test
	public void testUpdateSoldRate() throws Exception {
//		logger.debug("testCreateWithSoldRateTier()");
		
		List<SoldRate> soldRateList = persistenceRetrieveService.getSoldRateByID("000000059954");
		SoldRate existingSoldRate = soldRateList.get(0);
		existingSoldRate.setContractType(ContractType.Risk);

		existingSoldRate.setUpdateId("JUnit");

		SoldRate  newSoldRate = persistenceSaveService.updateSoldRate(existingSoldRate);
		Assert.assertNotNull(newSoldRate);
		Assert.assertNotNull(newSoldRate.getId());
	}

	@Ignore
	@Test
	public void testRetrieveSoldRate() throws Exception {
//		logger.debug("testCreateWithSoldRateTier()");
		
		List<SoldRate> soldRateList = persistenceRetrieveService.getSoldRateByID("000000059954");
		SoldRate soldRate = soldRateList.get(0);
		Assert.assertNotNull(soldRate);
		Assert.assertNotNull(soldRate.getId());
	}

	@Ignore
	@Test
	public void testRetrieveBillingRateFeeAssoc() throws Exception {
		String clientId = "U9999999";
		String classId = "1221";
		String divisionId = "1357";
		
		BillingRateFeeAssociation billingRateFeeAssociation = persistenceRetrieveService.getBillingRateFeeAssociationByID(clientId, divisionId, classId);
		Assert.assertNotNull(billingRateFeeAssociation);
		Assert.assertNotNull(billingRateFeeAssociation.getContractType());
	}

	@Ignore
	@Test
	public void testRetrieveBillingRateFeeAssocByClient() throws Exception {
		String clientId = "12074292";
		
		List<BillingRateFeeAssociation> listB = persistenceRetrieveService.getAllBillingRateFeeAssociationByClientID(clientId);
		
		Assert.assertNotNull(listB);
		Assert.assertNotNull(listB.get(0));
	}

	@Test
	public void testPersistBillingRateFeeAssocOnly() {
		BillingRateFeeAssociation billingRateFeeAssociation = new BillingRateFeeAssociation();
		billingRateFeeAssociation.setClientId("U9999999");
		billingRateFeeAssociation.setClassId("8888");
		billingRateFeeAssociation.setCreateId("TestUpdate");
		billingRateFeeAssociation.setClientName("UpdateDoug");
 		billingRateFeeAssociation.setContractType(ContractType.Combo);
		billingRateFeeAssociation.setBillType(BillType.Claims);
		billingRateFeeAssociation.setDivisionType(DivisionType.CLAIMS);
		billingRateFeeAssociation.setDivisionName("TestDivisionName");
		billingRateFeeAssociation.setClientStatus(RecordStatusEnum.ACTIVE.getStatusCode());
		billingRateFeeAssociation.setDivisionStatus(RecordStatusEnum.ACTIVE.getStatusCode());
		billingRateFeeAssociation.setClassStatus(RecordStatusEnum.ACTIVE.getStatusCode());
		billingRateFeeAssociation.setBillingRateFeeAsscStatus(RecordStatusEnum.ACTIVE.getStatusCode());
		billingRateFeeAssociation.setEffectiveDate(new DateMidnight(2016, 05, 01).toDate());
		billingRateFeeAssociation.setLimitDate(new DateMidnight(9999, 12, 31).toDate());
		
		Benefit benefit = new Benefit();
		benefit.setBenefitName("A");
		billingRateFeeAssociation.getBenefitList().add(benefit);
		
		persistenceSaveService.persistBillingRateFeeAscOnly(billingRateFeeAssociation);
	}

	@Test
	public void testDeleteBillingRateFeeAssoc() {
		persistenceSaveService.deleteBRFAbyIds("U9999999", "8888", "7777");
	}

	@Ignore
	@Test
	public void testDeleteSoldFee() {
		persistenceSaveService.deleteBySoldFeeId("000000001382");
	}

	@Ignore
	@Test
	public void testDeleteSoldRate() {
		persistenceSaveService.deleteBySoldRateId("000000140528");
	}

	@Ignore
	@Test
	public void testDeleteSoldRateAssoc() {
		persistenceSaveService.deleteSRAbySoldRateId("000000140528");
	}


}
