/*
 * 		ModifiedBy                Modified On           Remark      
		AbhishekK                 21-05-2022            RollBack all the changes for Dual Currency.

 * 
 * 
 */

package com.samyak.account.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samyak.account.dal.OtherLedgerServices;
import com.samyak.account.dal.ReceiptService;
import com.samyak.account.dto.LedgerFormDto;
import com.samyak.account.dto.ReceiptDto;
import com.samyak.account.dto.ReceiptFinanceTransDto;
import com.samyak.account.dto.ReceiptPaymentDetailDto;
import com.samyak.account.model.FinanceGroup;
import com.samyak.account.model.FinanceLedger;
import com.samyak.account.model.FinancialTransaction;
import com.samyak.account.model.MasterCurrency;
import com.samyak.account.model.PaymentDetails;
import com.samyak.account.model.Voucher;
import com.samyak.account.service.FinanceGroupService;
import com.samyak.account.service.FinanceLedgerService;
import com.samyak.account.service.MasterCurrencyService;

@RestController
@RequestMapping("/api/account/receipt")
public class ReceiptController {
	
	@Autowired
	private ReceiptService receiptService;
	
	@Autowired
	private FinanceLedgerService financeLedgerService;
	

	@GetMapping("/GetDebtorsLedgers")
	public List<Map<String, Object>> getDebtorsLedgers(HttpServletRequest request){
		
		return receiptService.GetLedgersList("Debtors");
	}
	
	@GetMapping("/GetCashBankLedgers")
	public List<Map<String, Object>> getCashBankLedgers(HttpServletRequest request){
		
		return receiptService.GetLedgersList("CashBank");
	}
	
	@GetMapping("/GetOthersLedgers")
	public List<Map<String, Object>> GetOthersLedgers(HttpServletRequest request){
		
		return receiptService.GetLedgersList("Others");
	}
	
	@GetMapping("/GetCurrencyLedgers")
	public List<Map<String, Object>> GetCurrenyLedger(HttpServletRequest request){
		return receiptService.GetCurrencyList("DualCurrency");
		
	}
	

	@GetMapping("/GetSettlementDeatil/{ledgerId}/{currencyId}")
	public Map<String,Object> GetSettlementDetail(HttpServletRequest request,
			@PathVariable("ledgerId")String ledgerId,
			@PathVariable("currencyId")String currencyId
			){
		
				/*
				 * Map<String,Object> mapDetail=new HashMap<String, Object>();
				 * 
				 * Map<String,Object>
				 * headerDetail=receiptService.GetReceiptSettlementDetail(ledgerId);
				 * 
				 * mapDetail.put("HeaderValue", headerDetail);
				 */
		 System.out.println("currencyId "+currencyId);
		 
		return receiptService.GetReceiptSettlementDetail(ledgerId, currencyId);
		
		
	}
	
	@PostMapping("/save")
	public Map<String, String> SaveReceipt(HttpServletRequest request,@RequestBody ReceiptDto receipt) {
		
		
		String getReceiptDate=receipt.getReceiptDate();
		
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM/dd/yyyy");
		
		DateTimeFormatter sqlFormatter=DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
		
		LocalDate parseDate=LocalDate.parse(getReceiptDate, formatter);
		Date receiptDate=Date.from(parseDate.atStartOfDay(ZoneId.systemDefault()).toInstant());  
		
		Voucher voucher=new Voucher();
		
		voucher.setCompanyId((long)1);
		voucher.setVoucherType((long)8); //SaleReceipt
		voucher.setToByNos((long)(receipt.getFinancialTrans().size()-1)*2);
		voucher.setVoucherCurrency(Long.parseLong(receipt.getReceiptCurrencyId()));
		voucher.setExchangeRate(Double.parseDouble(receipt.getReceiptExchangeRate()));
		voucher.setVoucherTotal(Double.parseDouble(receipt.getVoucherTotal()));
		voucher.setLocalTotal(Double.parseDouble(receipt.getLocalTotal()));
		voucher.setDollarTotal(Double.parseDouble(receipt.getDollarTotal()));
		voucher.setDescription(receipt.getNarration());
		
		voucher.setModifiedBy(Long.parseLong(receipt.getUserId()));
		voucher.setModifiedMachineName(request.getRemoteAddr());
		voucher.setModifiedOn(new Date());
		voucher.setReferanceVoucherId((long)0);
		voucher.setActive(true);
		voucher.setCostHeadGroupId((long)0);
		voucher.setCostHeadSubGroupId((long)0);
		voucher.setYearEndId((long)1);
		voucher.setRefNo(receipt.getRefNo());
		
		voucher.setVoucherDate(receiptDate);
		
//		
		List<ReceiptFinanceTransDto> liRFDto=receipt.getFinancialTrans();
		
		ReceiptFinanceTransDto creditRecord=null;
		
		for(ReceiptFinanceTransDto dto:liRFDto) {
			if(dto.getTransactionType().contains("Cr")) {
				
				creditRecord=dto;
				
				FinanceLedger fl=financeLedgerService.getFinanceLedgerById(Long.parseLong(dto.getFinanceLedgerId()));
				
				creditRecord.setForHeadId(fl.getCompanyPartyId()+"");//getCompnayPartyId
				creditRecord.setForHead(fl.getFinanceGroupId()+"");//getFinanceGroupId
				
				break;
			}
		}
		
		List<FinancialTransaction> listFinancialTransactions=new ArrayList<FinancialTransaction>();

		FinancialTransaction ftDr=null;
		FinancialTransaction ftCr=null;
		
		
		
		for(ReceiptFinanceTransDto dto:liRFDto) {
			
			switch(dto.getTransactionType()) {
				
				case "Dr":
					ftDr=new FinancialTransaction();
					ftCr=new FinancialTransaction();
					
					ftCr.setCompanyId((long)1);
					ftCr.setForHead(Long.parseLong(creditRecord.getForHead()));
					ftCr.setForHeadId(Long.parseLong(creditRecord.getForHeadId()));
					ftCr.setSrNo((long)2);
					ftCr.setDescription("Credit"); // Not used as of now
					ftCr.setTransactionType(true);
					ftCr.setAmount(Double.parseDouble(dto.getAmount()));
					ftCr.setLocalAmount(Double.parseDouble(dto.getLocalAmount()));
					ftCr.setDollarAmount(Double.parseDouble(dto.getDollarAmount()));
					ftCr.setModifiedOn(new Date());
					ftCr.setModifiedBy(Long.parseLong(receipt.getUserId()));
					ftCr.setModifiedMachineName(request.getRemoteHost());
					ftCr.setLedgerId(Long.parseLong(creditRecord.getFinanceLedgerId()));
					ftCr.setPaymentMode(false);
					ftCr.setAccountId((long)0);
					ftCr.setExchangeRate(0.0);
					ftCr.setTransactionDate(receiptDate);
					ftCr.setTranasactionNo("0");
					ftCr.setReceiveId((long)0);
					ftCr.setReceiveFromLedgerId((long)0);
					ftCr.setCostHeadGroupId((long)0);
					ftCr.setCostHeadSubGroupId((long)0);
					ftCr.setYearEndId((long)1);
					ftCr.setActive(true);
					
					FinanceLedger fl=financeLedgerService.getFinanceLedgerById(Long.parseLong(dto.getFinanceLedgerId()));
					
					ftDr.setCompanyId((long)1);
					ftDr.setForHead(fl.getFinanceGroupId());
					ftDr.setForHeadId(Long.parseLong(dto.getFinanceLedgerId()));
					ftDr.setSrNo((long)1);
					ftDr.setDescription("Debit"); // Not used as of now
					ftDr.setTransactionType(false);
					ftDr.setAmount(Double.parseDouble(dto.getAmount()));
					ftDr.setLocalAmount(Double.parseDouble(dto.getLocalAmount()));
					ftDr.setDollarAmount(Double.parseDouble(dto.getDollarAmount()));
					ftDr.setModifiedOn(new Date());
					ftDr.setModifiedBy(Long.parseLong(receipt.getUserId()));
					ftDr.setModifiedMachineName(request.getRemoteHost());
					ftDr.setLedgerId(Long.parseLong(dto.getFinanceLedgerId()));
					ftDr.setPaymentMode(false);
					ftDr.setAccountId((long)0);
					ftDr.setExchangeRate(0.0);
					ftDr.setTransactionDate(receiptDate);
					ftDr.setTranasactionNo("0");
					ftDr.setReceiveId((long)0);
					ftDr.setReceiveFromLedgerId((long)0);
					ftDr.setCostHeadGroupId((long)0);
					ftDr.setCostHeadSubGroupId((long)0);
					ftDr.setYearEndId((long)1);
					ftDr.setActive(true);
					
					listFinancialTransactions.add(ftDr);
					listFinancialTransactions.add(ftCr);
					
					break;
			
			}
		}
		
		List<PaymentDetails> listPaymentDetails=new ArrayList<PaymentDetails>();
		
		PaymentDetails paymentDetails=null;
		
		for(ReceiptPaymentDetailDto obj:receipt.getPaymentDetails()) {
			
			paymentDetails=new PaymentDetails();
			
			paymentDetails.setCompanyId((long)1);
			paymentDetails.setForHead((long)9);
			paymentDetails.setForHeadId(Long.parseLong(obj.getReceiveId()));
			paymentDetails.setTranasactionId((long)0);
			paymentDetails.setTransactionType(false);
			paymentDetails.setTransactionDate(receiptDate);
			paymentDetails.setAmount(Double.parseDouble(obj.getAmount()));
			paymentDetails.setLocalAmount(Double.parseDouble(obj.getLocalAmount()));
			paymentDetails.setDollarAmount(Double.parseDouble(obj.getDollarAmount()));
			paymentDetails.setExchangeRate(Double.parseDouble(obj.getExchnageRate()));
			paymentDetails.setModifiedOn(new Date());
			paymentDetails.setModifiedBy(Long.parseLong(receipt.getUserId()));
			paymentDetails.setModifiedMachineName(request.getRemoteAddr());
			paymentDetails.setActive(true);
			paymentDetails.setTranasactionId((long)0);
			paymentDetails.setFbId((long)0);
			paymentDetails.setYearEndId((long)1);
			
			listPaymentDetails.add(paymentDetails);
		}
		
		//ReceiptService Call SaveOrUpdate Method
		Map<String,String> result=receiptService.saveORUpdateReceipt(voucher, listFinancialTransactions, listPaymentDetails);
		
		return result;
	}
	//Code Started by Pradyumna 
	
	@PutMapping("/update")
	public Map<String, String> UpdateReceipt(HttpServletRequest request,@RequestBody ReceiptDto receipt) {
		
		
		String getReceiptDate=receipt.getReceiptDate();
		
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern("MM/dd/yyyy");
		
		DateTimeFormatter sqlFormatter=DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
		
		LocalDate parseDate=LocalDate.parse(getReceiptDate, formatter);
		Date receiptDate=Date.from(parseDate.atStartOfDay(ZoneId.systemDefault()).toInstant());  
		
		Voucher voucher=new Voucher();
		
		voucher.setCompanyId((long)1);
		voucher.setVoucherType((long)8); //SaleReceipt
		voucher.setToByNos((long)(receipt.getFinancialTrans().size()-1)*2);
		voucher.setVoucherCurrency(Long.parseLong(receipt.getReceiptCurrencyId()));
		voucher.setExchangeRate(Double.parseDouble(receipt.getReceiptExchangeRate()));
		voucher.setVoucherTotal(Double.parseDouble(receipt.getVoucherTotal()));
		voucher.setLocalTotal(Double.parseDouble(receipt.getLocalTotal()));
		voucher.setDollarTotal(Double.parseDouble(receipt.getDollarTotal()));
		voucher.setDescription(receipt.getNarration());
		
		voucher.setModifiedBy(Long.parseLong(receipt.getUserId()));
		voucher.setModifiedMachineName(request.getRemoteAddr());
		voucher.setModifiedOn(new Date());
		voucher.setReferanceVoucherId((long)0);
		voucher.setActive(true);
		voucher.setCostHeadGroupId((long)0);
		voucher.setCostHeadSubGroupId((long)0);
		voucher.setYearEndId((long)1);
		voucher.setRefNo(receipt.getRefNo());
		
		voucher.setVoucherDate(receiptDate);
		
//		
		List<ReceiptFinanceTransDto> liRFDto=receipt.getFinancialTrans();
		
		ReceiptFinanceTransDto creditRecord=null;
		
		for(ReceiptFinanceTransDto dto:liRFDto) {
			if(dto.getTransactionType().contains("Cr")) {
				
				creditRecord=dto;
				
				FinanceLedger fl=financeLedgerService.getFinanceLedgerById(Long.parseLong(dto.getFinanceLedgerId()));
				
				creditRecord.setForHeadId(fl.getCompanyPartyId()+"");//getCompnayPartyId
				creditRecord.setForHead(fl.getFinanceGroupId()+"");//getFinanceGroupId
				
				break;
			}
		}
		
		List<FinancialTransaction> listFinancialTransactions=new ArrayList<FinancialTransaction>();

		FinancialTransaction ftDr=null;
		FinancialTransaction ftCr=null;
		
		
		
		for(ReceiptFinanceTransDto dto:liRFDto) {
			
			switch(dto.getTransactionType()) {
				
				case "Dr":
					ftDr=new FinancialTransaction();
					ftCr=new FinancialTransaction();
					
					ftCr.setCompanyId((long)1);
					ftCr.setForHead(Long.parseLong(creditRecord.getForHead()));
					ftCr.setForHeadId(Long.parseLong(creditRecord.getForHeadId()));
					ftCr.setSrNo((long)2);
					ftCr.setDescription("Credit"); // Not used as of now
					ftCr.setTransactionType(true);
					ftCr.setAmount(Double.parseDouble(dto.getAmount()));
					ftCr.setLocalAmount(Double.parseDouble(dto.getLocalAmount()));
					ftCr.setDollarAmount(Double.parseDouble(dto.getDollarAmount()));
					ftCr.setModifiedOn(new Date());
					ftCr.setModifiedBy(Long.parseLong(receipt.getUserId()));
					ftCr.setModifiedMachineName(request.getRemoteHost());
					ftCr.setLedgerId(Long.parseLong(creditRecord.getFinanceLedgerId()));
					ftCr.setPaymentMode(false);
					ftCr.setAccountId((long)0);
					ftCr.setExchangeRate(0.0);
					ftCr.setTransactionDate(receiptDate);
					ftCr.setTranasactionNo("0");
					ftCr.setReceiveId((long)0);
					ftCr.setReceiveFromLedgerId((long)0);
					ftCr.setCostHeadGroupId((long)0);
					ftCr.setCostHeadSubGroupId((long)0);
					ftCr.setYearEndId((long)1);
					ftCr.setActive(true);
					
					FinanceLedger fl=financeLedgerService.getFinanceLedgerById(Long.parseLong(dto.getFinanceLedgerId()));
					
					ftDr.setCompanyId((long)1);
					ftDr.setForHead(fl.getFinanceGroupId());
					ftDr.setForHeadId(Long.parseLong(dto.getFinanceLedgerId()));
					ftDr.setSrNo((long)1);
					ftDr.setDescription("Debit"); // Not used as of now
					ftDr.setTransactionType(false);
					ftDr.setAmount(Double.parseDouble(dto.getAmount()));
					ftDr.setLocalAmount(Double.parseDouble(dto.getLocalAmount()));
					ftDr.setDollarAmount(Double.parseDouble(dto.getDollarAmount()));
					ftDr.setModifiedOn(new Date());
					ftDr.setModifiedBy(Long.parseLong(receipt.getUserId()));
					ftDr.setModifiedMachineName(request.getRemoteHost());
					ftDr.setLedgerId(Long.parseLong(dto.getFinanceLedgerId()));
					ftDr.setPaymentMode(false);
					ftDr.setAccountId((long)0);
					ftDr.setExchangeRate(0.0);
					ftDr.setTransactionDate(receiptDate);
					ftDr.setTranasactionNo("0");
					ftDr.setReceiveId((long)0);
					ftDr.setReceiveFromLedgerId((long)0);
					ftDr.setCostHeadGroupId((long)0);
					ftDr.setCostHeadSubGroupId((long)0);
					ftDr.setYearEndId((long)1);
					ftDr.setActive(true);
					
					listFinancialTransactions.add(ftDr);
					listFinancialTransactions.add(ftCr);
					
					break;
			
			}
		}
		
		List<PaymentDetails> listPaymentDetails=new ArrayList<PaymentDetails>();
		
		PaymentDetails paymentDetails=null;
		
		for(ReceiptPaymentDetailDto obj:receipt.getPaymentDetails()) {
			
			paymentDetails=new PaymentDetails();
			
			paymentDetails.setCompanyId((long)1);
			paymentDetails.setForHead((long)9);
			paymentDetails.setForHeadId(Long.parseLong(obj.getReceiveId()));
			paymentDetails.setTranasactionId((long)0);
			paymentDetails.setTransactionType(false);
			paymentDetails.setTransactionDate(receiptDate);
			paymentDetails.setAmount(Double.parseDouble(obj.getAmount()));
			paymentDetails.setLocalAmount(Double.parseDouble(obj.getLocalAmount()));
			paymentDetails.setDollarAmount(Double.parseDouble(obj.getDollarAmount()));
			paymentDetails.setExchangeRate(Double.parseDouble(obj.getExchnageRate()));
			paymentDetails.setModifiedOn(new Date());
			paymentDetails.setModifiedBy(Long.parseLong(receipt.getUserId()));
			paymentDetails.setModifiedMachineName(request.getRemoteAddr());
			paymentDetails.setActive(true);
			paymentDetails.setTranasactionId((long)0);
			paymentDetails.setFbId((long)0);
			paymentDetails.setYearEndId((long)1);
			
			listPaymentDetails.add(paymentDetails);
		}
		
		//ReceiptService Call SaveOrUpdate Method
		Map<String,String> result=receiptService.updateReceipt(voucher, listFinancialTransactions, listPaymentDetails);
		
		return result;
	}

}
