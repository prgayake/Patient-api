package com.samyak.account.dal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samyak.account.model.FinancialTransaction;
import com.samyak.account.model.PaymentDetails;
import com.samyak.account.model.Voucher;
import com.samyak.account.repository.FinancialTransactionRepository;
import com.samyak.account.repository.PaymentDetailsRepository;
import com.samyak.account.repository.ReceiptRepository;
import com.samyak.account.repository.VoucherRepository;
import com.samyak.account.repositoryImpl.MultipleColRepositoryImp;
import com.samyak.account.repositoryImpl.ReceiptRepositoryImpl;
import com.samyak.account.service.FinanceLedgerService;
import com.samyak.account.service.MasterCurrencyService;

@Service
public class ReceiptService {

	@Autowired
	private FinanceLedgerService financeLedgerService;
	
	@Autowired
	private MultipleColRepositoryImp masterCurrencyService;
	
	@Autowired
	private VoucherRepository voucherRepository;
	
	@Autowired
	private ReceiptRepositoryImpl receiptRepositoryImpl;
	
	@Autowired
	private FinancialTransactionRepository financialTransactionRepository;
	
	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	
	private int i;
	
	public List<Map<String, Object>> GetLedgersList(String type){
		
		List<Map<String, Object>> listResult=new ArrayList<Map<String,Object>>();
		
		switch(type) {
			case "Debtors":
				listResult=financeLedgerService.GetIdValueFromTable("financeLedgerId", "financeLedgerName", true, "Debtors");
				break;
			case "CashBank":
				listResult=financeLedgerService.GetIdValueFromTable("financeLedgerId", "financeLedgerName", true, "CashBank");
				break;
			case "Others":
				listResult=financeLedgerService.GetIdValueFromTable("financeLedgerId", "financeLedgerName", true, "Others");
				break;
		}
		
		return listResult;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> GetCurrencyList(String type){
		
		List<Map<String,Object>> listResult = new ArrayList<Map<String, Object>>();
		listResult =  masterCurrencyService.GetMultiValueFromTable(true);
		return listResult;
	}
	
	public Map<String, Object> GetReceiptSettlementDetail(String ledgerId, String currencyId) {
		
		Map<String, Object> map=receiptRepositoryImpl.GetReceiptSettlementHeader(ledgerId);
		
		Map<String,Object> mapOnAccBal=receiptRepositoryImpl.DataForCalcOnAccountBalance(ledgerId,map.get("CompanyPartyId").toString());
		
		//-------------For Local Amount Calculations
		Double saleLocalTotalDr=Double.parseDouble(mapOnAccBal.get("SaleLocalTotalDr").toString());
		Double ftLocalAmountDr=Double.parseDouble(mapOnAccBal.get("FtLocalAmountDr").toString());
		Double openingLocalBalance=Double.parseDouble(map.get("openingLocalBalance").toString());
		Double saleReturnLocalTotalCr=Double.parseDouble(mapOnAccBal.get("SaleReturnLocalTotalCr").toString());
		Double ftLocalAmountCr=Double.parseDouble(mapOnAccBal.get("FtLocalAmountCr").toString());
		Double pdReceivedAmtLocal=Double.parseDouble(mapOnAccBal.get("PdReceivedAmtLocal").toString());
		
		Double finalClosingBalanceLocal=((saleLocalTotalDr+ftLocalAmountDr+openingLocalBalance)-(saleReturnLocalTotalCr+ftLocalAmountCr));
		
		Double pendingSaleLocalAmt=(saleLocalTotalDr-pdReceivedAmtLocal);
		
		BigDecimal pendingOnAccountLocal=new BigDecimal((pendingSaleLocalAmt-finalClosingBalanceLocal)).setScale(2, RoundingMode.HALF_UP);
		
		//------------------For Dollar Amount Calculations
		Double SaleDollarTotalDr=Double.parseDouble(mapOnAccBal.get("SaleDollarTotalDr").toString());
		Double FtDollarAmountDr=Double.parseDouble(mapOnAccBal.get("FtDollarAmountDr").toString());
		Double openingBalance=Double.parseDouble(map.get("openingBalance").toString());
		Double SaleReturnDollarTotalCr=Double.parseDouble(mapOnAccBal.get("SaleReturnDollarTotalCr").toString());
		Double FtDollarAmountCr=Double.parseDouble(mapOnAccBal.get("FtDollarAmountCr").toString());
		Double PdReceivedAmtDollar=Double.parseDouble(mapOnAccBal.get("PdReceivedAmtDollar").toString());
		
		Double FinalClosingBalanceDollar=((SaleDollarTotalDr+FtDollarAmountDr+openingBalance)-(SaleReturnDollarTotalCr+FtDollarAmountCr));
		
		Double PendingSaleDollarAmt=(SaleDollarTotalDr-PdReceivedAmtDollar);
		
		BigDecimal PendingOnAccountDollar=new BigDecimal((PendingSaleDollarAmt-FinalClosingBalanceDollar)).setScale(2, RoundingMode.HALF_UP);
		
		
		map.put("PendingOnAccountLocal",pendingOnAccountLocal.doubleValue());
		map.put("PendingOnAccountDollar",PendingOnAccountDollar.doubleValue());
		
		/*
		 * System.out.println("==================Map Result=================");
		 * 
		 * System.out.println(map);
		 */
		
		List<Map<String,Object>> rowDetail=receiptRepositoryImpl.GetSettlementRowDetailDebtors(map.get("CompanyPartyId").toString(),currencyId);
		
		map.put("RowDetail",rowDetail);
		
		return map;
		
	}
	
	public Double getExchangeRateById(Long id) {
		
		return receiptRepositoryImpl.getExchangeRateById(id);
		
	}
	
	
	public synchronized Map<String,String> saveORUpdateReceipt(Voucher voucher, List<FinancialTransaction> listFinancialTransaction, List<PaymentDetails> listPaymentDetails) {
		
		//Generate VoucherId, VoucherNo
		//Generate FinancialTransactionId
		//Generate PaymentDetailsId
		Map<String, Object> voucherIdNo=voucherRepository.getNextIdVoucherNo();
		
		Long voucherId=Long.parseLong(voucherIdNo.get("LastVoucherId").toString());
		String voucherNo=voucherIdNo.get("LastVoucherNo").toString();
		
		String getNextVoucherNo=voucherNo.split("-")[0]+"-"+(Long.parseLong(voucherNo.split("-")[1])+1);
		voucher.setVoucherId(voucherId+1);
		voucher.setVoucherNo(getNextVoucherNo);
		
		Map<String, Object> finanTransLastId=financialTransactionRepository.getLastId();
		
		Long transactionId=Long.parseLong(finanTransLastId.get("LastId").toString());
		
		i=1;
		
		listFinancialTransaction.forEach(ft->{
			ft.setVoucherId(voucherId+1);
			ft.setTranasactionId(transactionId+i);
			i++;
		});
		
		Map<String, Object> paymentDetailLastId=paymentDetailsRepository.getLastId();
		
		Long paymentId=Long.parseLong(paymentDetailLastId.get("LastId").toString());
		i=1;
		listPaymentDetails.forEach(pd->{
			pd.setVoucherId(voucherId+1);
			pd.setPaymentNo(getNextVoucherNo);
			pd.setPaymentId(paymentId+i);
			i++;
		});
		
		String result=receiptRepositoryImpl.saveOrUpdateReceipt(voucher, listFinancialTransaction, listPaymentDetails);
		
		Map<String, String> resultMap=new HashMap<String, String>();
		if(result.equalsIgnoreCase("Success")) {
			resultMap.put("Result", result);
			resultMap.put("NewVoucherNo", getNextVoucherNo);
		}else {
			resultMap.put("Result", result);
		}
		return resultMap;
	}
	public synchronized Map<String,String> updateReceipt(Voucher voucher, List<FinancialTransaction> listFinancialTransaction, List<PaymentDetails> listPaymentDetails){
		
		String voucherNo = voucher.getVoucherNo().toString();
		String result=receiptRepositoryImpl.UpdateReceipt(voucher, listFinancialTransaction, listPaymentDetails);
		
		Map<String, String> resultMap=new HashMap<String, String>();
		if(result.equalsIgnoreCase("Success")) {
			resultMap.put("Result", result);
			resultMap.put("NewVoucherNo", voucherNo);
		}else {
			resultMap.put("Result", result);
		}
		return resultMap;
	}
	
}
