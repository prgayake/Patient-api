package com.samyak.account.repositoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.QueryHint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transaction;

import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyak.account.dto.ReceiptSettlementRowDetail;
import com.samyak.account.model.FinanceLedger;
import com.samyak.account.model.FinancialTransaction;
import com.samyak.account.model.MasterCompanyParty;
import com.samyak.account.model.MasterCurrency;
import com.samyak.account.model.PaymentDetails;
import com.samyak.account.model.Receive;
import com.samyak.account.model.Voucher;
import com.samyak.account.repository.ReceiptRepository;
import com.samyak.account.repository.ContraRepository;
import com.samyak.account.service.FinancialTransactionService;
import com.samyak.account.service.PaymentDetailsService;
import com.samyak.account.service.VoucherService;

@Service
public class ReceiptRepositoryImpl implements ReceiptRepository {

	@PersistenceContext
	private EntityManager em;
	
	private Double localamt=0.0;
	private Double dollarAmt=0.0;
	
	private List<Long> receiveIdList=null;
	
	@Autowired
	private VoucherService voucherService;
	
	@Autowired
	private FinancialTransactionService financialTransactionService;

	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	@Override
	public Map<String, Object> GetReceiptSettlementHeader(String ledgerId) {
		
		CriteriaBuilder cb=em.getCriteriaBuilder();
		
		//--Get Detail from FinanceLedger Table
		CriteriaQuery<FinanceLedger> cq=cb.createQuery(FinanceLedger.class);
		Root<FinanceLedger> root=cq.from(FinanceLedger.class);
		
		cq.multiselect(root.get("financeLedgerId"),root.get("financeLedgerName"),root.get("companyPartyId"),root.get("currencyId"),root.get("openingBalance"),root.get("openingLocalBalance"))
						.where(cb.equal(root.get("financeLedgerId"), ledgerId));
		
		FinanceLedger fl=em.createQuery(cq).getSingleResult();
		
		//--Get Detail from Master_CompanyParty Table
		CriteriaQuery<MasterCompanyParty> cqCompanyParty=cb.createQuery(MasterCompanyParty.class);
		Root<MasterCompanyParty> rootCompanyParty=cqCompanyParty.from(MasterCompanyParty.class);
		
		cqCompanyParty.multiselect(rootCompanyParty.get("companyPartyId"),rootCompanyParty.get("companyPartyName"))
						.where(cb.equal(rootCompanyParty.get("companyPartyId"), fl.getCompanyPartyId()));
		
		
		MasterCompanyParty mcp=em.createQuery(cqCompanyParty).getSingleResult();
		
		Map<String, Object> map=new HashMap<String, Object>();
		
		/*
		 * ObjectMapper omap=new ObjectMapper();
		 * 
		 * map=omap.convertValue(em.createQuery(cqCompanyParty).getSingleResult(),Map.
		 * class);
		 */
		
		
		map.put("CompanyPartyId", mcp.getCompanyPartyId());
		map.put("CompanyPartyName", mcp.getCompanyPartyName());
		map.put("FinanceLedgerId", fl.getFinanceLedgerId());
		map.put("FinanceLedgerName", fl.getFinanceLedgerName());
		map.put("CurrencyId", fl.getCurrencyId());
		
		map.put("openingBalance", fl.getOpeningBalance());
		map.put("openingLocalBalance", fl.getOpeningLocalBalance());
		
		
		
		return map;
	}

	@Override
	public Map<String, Object> DataForCalcOnAccountBalance(String ledgerId,String companyPartyId) {
		
		Map<String,Object> onAccBal=new HashMap<String, Object>();
		
		CriteriaBuilder cb=em.getCriteriaBuilder();
		
		//******************** 1. FOR SALE QUERY -- FOR DR AMT ************************************//
		/*
		 * select SUM(LOCAL_TOTAL) AS SALE_LOCAL_TOTAL_DR,SUM(DOLLAR_TOTAL) AS
		 * SALE_DOLLAR_TOTAL_DR 
		 * from receive 
		 * where active=1 and
		 * receive_fromId="+CompanyId+" and purchase=1 and receive_sell=0 and r_return=0
		 * and opening_stock=0 and stockTransfer_Type=0 and company_id=1
		 * 
		 * receiveSell
		 * rReturn
		 * openingStock
		 * stockTransferType
		 * companyId
		 */
		
		CriteriaQuery<Receive> cqSaleDrAmt=cb.createQuery(Receive.class);
		Root<Receive> rootSaleDrAmt=cqSaleDrAmt.from(Receive.class);
		cqSaleDrAmt.multiselect(rootSaleDrAmt.get("receiveId"),rootSaleDrAmt.get("localTotal"),rootSaleDrAmt.get("dollarTotal"))
									.where(cb.and(
												cb.equal(rootSaleDrAmt.get("active"), true),
												cb.equal(rootSaleDrAmt.get("receiveFromId"), companyPartyId),
												cb.equal(rootSaleDrAmt.get("purchase"), 1),
												cb.equal(rootSaleDrAmt.get("receiveSell"), 0),
												cb.equal(rootSaleDrAmt.get("rReturn"), 0),
												cb.equal(rootSaleDrAmt.get("openingStock"), 0),
												cb.equal(rootSaleDrAmt.get("stockTransferType"), 0),
												cb.equal(rootSaleDrAmt.get("companyId"), 1)
											));
		
		List<Receive> liReceive=em.createQuery(cqSaleDrAmt).getResultList();

		localamt=0.0;
		dollarAmt=0.0;
		
		receiveIdList=new ArrayList<Long>();
		
		liReceive.forEach(rev->{
			localamt+=rev.getLocalTotal();
			dollarAmt+=rev.getDollarTotal();
			receiveIdList.add(rev.getReceiveId());
		});
		
		onAccBal.put("SaleLocalTotalDr", localamt);
		onAccBal.put("SaleDollarTotalDr", dollarAmt);
		
		//******************** 2. FOR SALE RETURN QUERY -- FOR CR AMT ************************************//
		/*select SUM(LOCAL_TOTAL) AS SALE_RETURN_LOCAL_TOTAL_CR,SUM(DOLLAR_TOTAL) AS SALE_RETURN_DOLLAR_TOTAL_CR 
		 * from receive
		 *  where active=1 and receive_fromId="+CompanyId+" and purchase=1 and receive_sell=1 and r_return=1 and
		 *   opening_stock=0 and stockTransfer_Type=0 and company_id=1*/
		CriteriaQuery<Receive> cqSaleCrAmt=cb.createQuery(Receive.class);
		Root<Receive> rootSaleCrAmt=cqSaleCrAmt.from(Receive.class);
		cqSaleCrAmt.multiselect(rootSaleCrAmt.get("receiveId"),rootSaleCrAmt.get("localTotal"),rootSaleCrAmt.get("dollarTotal"))
									.where(cb.and(
												cb.equal(rootSaleCrAmt.get("active"), true),
												cb.equal(rootSaleCrAmt.get("receiveFromId"), companyPartyId),
												cb.equal(rootSaleCrAmt.get("purchase"), 1),
												cb.equal(rootSaleCrAmt.get("receiveSell"), 1),
												cb.equal(rootSaleCrAmt.get("rReturn"), 1),
												cb.equal(rootSaleCrAmt.get("openingStock"), 0),
												cb.equal(rootSaleCrAmt.get("stockTransferType"), 0),
												cb.equal(rootSaleCrAmt.get("companyId"), 1)
											));
		
		List<Receive> liReceive1=em.createQuery(cqSaleCrAmt).getResultList();

		localamt=0.0;
		dollarAmt=0.0;
		
		liReceive1.forEach(rev->{
			localamt+=rev.getLocalTotal();
			dollarAmt+=rev.getDollarTotal();
		});
		
		onAccBal.put("SaleReturnLocalTotalCr", localamt);
		onAccBal.put("SaleReturnDollarTotalCr", dollarAmt);
		
		

		//*********************3. FOR FT -- FOR CR AMT	**************************************************//
		
		/*SELECT SUM(FT.LOCAL_AMOUNT) AS FT_LOCAL_AMOUNT_CR,SUM(FT.DOLLAR_AMOUNT) AS FT_DOLLAR_AMOUNT_CR
		 *  FROM FINANCIAL_TRANSACTION FT,VOUCHER V 
		 *  WHERE FT.COMPANY_ID=1 AND V.ACTIVE=1 AND FT.ACTIVE=1 AND FT.LEDGER_ID="+LedgerId+" AND V.VOUCHER_ID=FT.VOUCHER_ID 
		 *  AND TRANSACTION_TYPE=1*/
		
		//Get All voucher id from FinancialTransaction FT where FT.active=1 and ft.ledgerId=ledgerId===>result1VoucherId
		
		//Get All VoucherId from VoucherTable where voucher.active=1 and voucherId in (result1VoucherId)==>result2_VoucherId
		
		//Get SUM(FT.LOCAL_AMOUNT) & SUM(FT.DOLLAR_AMOUNT) from FinancialTransaction where vocuherId in (result2_VoucherId)
		
		//-------------OR
		
		/*
		 * try { List<Long> liVoucherId=new ArrayList<Long>();
		 * 
		 * for(int i=0;i<liVoucher.size();i++) {
		 * liVoucherId.add(Long.parseLong(liVoucher.get(i).toString()));
		 * }}catch(Exception e) {}
		 */
		
		//-------------Get All VoucherID where Active=true
		CriteriaQuery<Voucher> cq=cb.createQuery(Voucher.class);
		Root<Voucher> root=cq.from(Voucher.class);
		cq.select(root.get("voucherId")).where(cb.equal(root.get("active"), true));
		List<Object> liVoucher=em.createQuery(cq).getResultStream().collect(Collectors.toList());
		
		//Get  From FinancialTransaction where LedgerId=? and Ft.Active=1 and voucherId in (?,?,.....) and Transactiontype=true
		CriteriaQuery<FinancialTransaction> cqFtCrAmt=cb.createQuery(FinancialTransaction.class);
		Root<FinancialTransaction> rootFtCrAmt=cqFtCrAmt.from(FinancialTransaction.class);
		cqFtCrAmt.multiselect(rootFtCrAmt.get("localAmount"),rootFtCrAmt.get("dollarAmount"))
								.where(cb.and(
										cb.equal(rootFtCrAmt.get("active"), true),
										rootFtCrAmt.get("voucherId").in(liVoucher),
										cb.equal(rootFtCrAmt.get("ledgerId"),ledgerId),
										cb.equal(rootFtCrAmt.get("transactionType"), true)
										));
		
		List<FinancialTransaction> liFtCrAmt=em.createQuery(cqFtCrAmt).getResultList();
		
		//Sum of all localAmount and Dollar Amount
		localamt=0.0;
		dollarAmt=0.0;
		
		liFtCrAmt.forEach(ftCrAmt->{
			localamt+=ftCrAmt.getLocalAmount();
			dollarAmt+=ftCrAmt.getDollarAmount();
		});
		
		onAccBal.put("FtLocalAmountCr", localamt);
		onAccBal.put("FtDollarAmountCr", dollarAmt);
		
		System.out.println("====LocalAmtCr==="+localamt);
		
		//********************	4. FOR FT -- FOR DR AMT	***************************************************//
		
		/*SELECT SUM(FT.LOCAL_AMOUNT) AS FT_LOCAL_AMOUNT_DR,SUM(FT.DOLLAR_AMOUNT) AS FT_DOLLAR_AMOUNT_DR 
		 * FROM FINANCIAL_TRANSACTION FT,VOUCHER V 
		 * WHERE FT.COMPANY_ID=1 AND V.ACTIVE=1 AND FT.ACTIVE=1 AND FT.LEDGER_ID="+LedgerId+" AND V.VOUCHER_ID=FT.VOUCHER_ID 
		 * AND TRANSACTION_TYPE=0*/
		
		//Get  From FinancialTransaction where LedgerId=? and Ft.Active=1 and voucherId in (?,?,.....) and Transactiontype=true
				CriteriaQuery<FinancialTransaction> cqFtCrAmt1=cb.createQuery(FinancialTransaction.class);
				Root<FinancialTransaction> rootFtCrAmt1=cqFtCrAmt1.from(FinancialTransaction.class);
				cqFtCrAmt1.multiselect(rootFtCrAmt1.get("localAmount"),rootFtCrAmt.get("dollarAmount"))
										.where(cb.and(
												cb.equal(rootFtCrAmt1.get("active"), true),
												rootFtCrAmt1.get("voucherId").in(liVoucher),
												cb.equal(rootFtCrAmt1.get("ledgerId"),ledgerId),
												cb.equal(rootFtCrAmt1.get("transactionType"), false)
												));
				
				List<FinancialTransaction> liFtCrAmt1=em.createQuery(cqFtCrAmt1).getResultList();
				
				//Sum of all localAmount and Dollar Amount
				localamt=0.0;
				dollarAmt=0.0;
				
				liFtCrAmt1.forEach(ftCrAmt1->{
					localamt+=ftCrAmt1.getLocalAmount();
					dollarAmt+=ftCrAmt1.getDollarAmount();
				});
				
				onAccBal.put("FtLocalAmountDr", localamt);
				onAccBal.put("FtDollarAmountDr", dollarAmt);
		
				System.out.println("====LocalAmtDr==="+localamt);
		
		//******************** 5. OPENING AMT (IF POSITIVE THEN DR, IF NEGATIVE THEN CR) ************************//
		/*SELECT SUM(OPENING_LOCALBALANCE) AS OPENING_LOCALBALANCE,SUM(OPENING_DOLLARBALANCE) AS OPENING_DOLLARBALANCE 
		 * FROM LEDGER WHERE LEDGER_ID="+LedgerId*/		
		
		/*select SUM(L.OpeningBalance) OpeningBalance,SUM(L.OpeningLocalBalance) OpeningLocalBalance 
		 * from FinanceLedger L where FinanceLedgerId=81*/
		
		//---------Already done at ABove function *GetReceiptSettlementHeader*
		
		
		
		
		//*********************	6. PAYMENT RECEIVED AGAINST INVOICES ************************************//
		/*select sum(local_amount) as PD_ReceivedAmtLocal,sum(dollar_amount) as PD_ReceivedAmtDollar 
		 * from payment_details where active=1 and for_head=9 and for_headId in 
		 * (select Receive_Id 
		 * from receive 
		 * where active=1 and receive_fromId="+CompanyId+" and purchase=1 and receive_sell=0 and r_return=0 
		 * and opening_stock=0 and stockTransfer_Type=0 and company_id=1)*/
		
		CriteriaQuery<PaymentDetails> cqPayDetail=cb.createQuery(PaymentDetails.class);
		Root<PaymentDetails> rootPayDetail=cqPayDetail.from(PaymentDetails.class);
		cqPayDetail.multiselect(rootPayDetail.get("localAmount"),rootPayDetail.get("dollarAmount"))
								.where(cb.and(
											cb.equal(rootPayDetail.get("active"), true),
											rootPayDetail.get("forHeadId").in(receiveIdList),
											cb.equal(rootPayDetail.get("forHead"),9)
										));
		
		List<PaymentDetails> liPayDetail=em.createQuery(cqPayDetail).getResultList();
		
		//Sum of all localAmount and Dollar Amount
		localamt=0.0;
		dollarAmt=0.0;
		
		liPayDetail.forEach(PayDetail->{
			localamt+=PayDetail.getLocalAmount();
			dollarAmt+=PayDetail.getDollarAmount();
		});
		
		onAccBal.put("PdReceivedAmtLocal", localamt);
		onAccBal.put("PdReceivedAmtDollar", dollarAmt);		
		
		/*
		 * System.out.println("====Fional Map====");
		 * 
		 * System.out.println(onAccBal);
		 */
		
		
		
		return onAccBal;
	}
	
	
	@Override
	public List<Map<String, Object>> GetSettlementRowDetailDebtors(String companyPartyId,String currencyId) {
	
		List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map=null;
		
		Query qrSp=em.createNativeQuery("{call SamyakReceipt(?,?,?)}");
		
		qrSp.setParameter(1, "getTransactionRowDetail");
		qrSp.setParameter(2, companyPartyId);
		qrSp.setParameter(3,currencyId);
		
		
		System.out.println(" Company Party Id "+companyPartyId);
		@SuppressWarnings("unchecked")
		List<Object[]> li=qrSp.getResultList();
		
		List<String> liColList=GetColList();
		
		for(Object[] obj:li) {
			List<Object> liData=Arrays.asList(obj);
			map=new HashMap<String, Object>();
			for(int i=0;i<liData.size();i++) {
				map.put(liColList.get(i), liData.get(i));
			}
			
			listMap.add(map);
		}
		
		return listMap;
	}
	
	private List<String> GetColList(){
		
		List<String> colList=new ArrayList<String>();
		
		colList.add("receiveNo");
		colList.add("refNo");
		colList.add("exchangeRate");
		colList.add("voucherDate");
		colList.add("localTotal");
		colList.add("localReceived");
		colList.add("localPending");
		colList.add("dollarTotal");
		colList.add("dollarReceived");
		colList.add("dollarPending");
		colList.add("receiveId");
		colList.add("receiveFromId");
		colList.add("receiveCurrencyId");
		colList.add("CurrencySymbol");
		return colList;
	}

	@Transactional
	@Override
	public String saveOrUpdateReceipt(Voucher voucher, List<FinancialTransaction> listFinancialTransactions,
			List<PaymentDetails> listPaymentDetails) {
		
		try {
			voucherService.saveVoucherData(voucher);
			financialTransactionService.saveOrUpdateFinancialTransaction(listFinancialTransactions);
			paymentDetailsService.saveORUpdatePaymentDetail(listPaymentDetails);
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return "Error";
		}finally {
			
		}
		
		return "Success";
	}
	
	@Transactional
	@Override
	public String UpdateReceipt(Voucher voucher, List<FinancialTransaction> listFinancialTransactions,List<DataOperation> listDataOperation) {
		
		try {
			voucherService.saveVoucherData(voucher);
			financialTransactionService.saveOrUpdateFinancialTransaction(listFinancialTransactions);
			//dataOperationService.saveOrUpdateDataOperation(listDataOperation);
			
			
		}catch(Exception e) {
			e.printStackTrace();
			return "Error";
		}finally {
			
		}
		
		return "Success";
	}
	
	@Override
	public Double getExchangeRateById(Long id) {
		
		String idf = "1";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MasterCurrency> cq = cb.createQuery(MasterCurrency.class);
		Root<MasterCurrency> root = cq.from(MasterCurrency.class);
		cq.select(root.get("baseExchangeRate")).where(cb.equal(root.get("currencyId"), (long) 1));
		
		List baseExchangeRate = em.createQuery(cq).getResultList();
		
		
		return Double.parseDouble(baseExchangeRate.get(0).toString());
		
		
		
	}
	
	
	
}
