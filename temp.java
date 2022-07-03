package com.samyak.account.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Financial_Transaction")
public class FinancialTransaction
{	
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="Tranasaction_Id")
	private Long tranasactionId;
	
	@Column(name="Company_Id")
	private Long companyId;
	
	@Column(name="Voucher_Id")
	private Long voucherId;
	
	@Column(name="Sr_No")
	private Long srNo;
	
	@Column(name="For_Head")
	private Long forHead;
	
	@Column(name="For_Headid")
	private Long forHeadId;
	
	@Column(name="Description")
	private String description;
	
	@Column(name="Transaction_Type")
	private boolean transactionType;
	
	@Column(name="Amount")
	private Double amount;
	
	@Column(name="Local_Amount")
	private Double localAmount;
	
	@Column(name="Dollar_Amount")
	private Double dollarAmount;
	
	@Column(name="Modified_On")
	private Date modifiedOn;
	
	@Column(name="Modified_By")
	private Long modifiedBy;
	
	@Column(name="Modified_Machinename")
	private String modifiedMachineName;
	
	@Column(name="Ledger_Id")
	private Long ledgerId;
	
	@Column(name="Active")
	private boolean active;
	
	@Column(name="Payment_Mode")
	private boolean paymentMode;
	
	@Column(name="Cheque_No")
	private String chequeNo;
	
	@Column(name="Cheque_Date")
	private Date chequeDate;
	
	@Column(name="Bank_Name")
	private String bankName;
	
	@Column(name="Account_Id")
	private Long accountId;
	
	@Column(name="Exchange_Rate")
	private Double exchangeRate;
	
	@Column(name="Transaction_Date")
	private Date transactionDate;
	
	@Column(name="Tranasaction_No")
	private String tranasactionNo;
	
	@Column(name="Receive_Id")
	private Long receiveId;
	
	@Column(name="Receivefrom_Ledgerid")
	private Long receiveFromLedgerId;
	
	@Column(name="Costheadgroup_Id")
	private Long costHeadGroupId;
	
	@Column(name="Costheadsubgroup_Id")
	private Long costHeadSubGroupId;
	
	@Column(name="Yearend_Id")
	private Long yearEndId;

	public FinancialTransaction() {
		super();
		

	}
	
	public FinancialTransaction(Long tranasactionId,Long forHeadId,Long srNo, Double Amount)
	{
		super();
		this.tranasactionId=tranasactionId;
		this.forHeadId=forHeadId;
		this.srNo=srNo;
		this.amount=Amount;
	}

	public FinancialTransaction(Double localAmount, Double dollarAmount) {
		super();
		this.localAmount = localAmount;
		this.dollarAmount = dollarAmount;
	}



	public FinancialTransaction(Long tranasactionId) {
		super();
		this.tranasactionId = tranasactionId;
	}



	public Long getTranasactionId() {
		return tranasactionId;
	}

	public void setTranasactionId(Long tranasactionId) {
		this.tranasactionId = tranasactionId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public Long getSrNo() {
		return srNo;
	}

	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}

	public Long getForHead() {
		return forHead;
	}

	public void setForHead(Long forHead) {
		this.forHead = forHead;
	}

	public Long getForHeadId() {
		return forHeadId;
	}

	public void setForHeadId(Long forHeadId) {
		this.forHeadId = forHeadId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isTransactionType() {
		return transactionType;
	}

	public void setTransactionType(boolean transactionType) {
		this.transactionType = transactionType;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getLocalAmount() {
		return localAmount;
	}

	public void setLocalAmount(Double localAmount) {
		this.localAmount = localAmount;
	}

	public Double getDollarAmount() {
		return dollarAmount;
	}

	public void setDollarAmount(Double dollarAmount) {
		this.dollarAmount = dollarAmount;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedMachineName() {
		return modifiedMachineName;
	}

	public void setModifiedMachineName(String modifiedMachineName) {
		this.modifiedMachineName = modifiedMachineName;
	}

	public Long getLedgerId() {
		return ledgerId;
	}

	public void setLedgerId(Long ledgerId) {
		this.ledgerId = ledgerId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(boolean paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTranasactionNo() {
		return tranasactionNo;
	}

	public void setTranasactionNo(String tranasactionNo) {
		this.tranasactionNo = tranasactionNo;
	}

	public Long getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(Long receiveId) {
		this.receiveId = receiveId;
	}

	public Long getReceiveFromLedgerId() {
		return receiveFromLedgerId;
	}

	public void setReceiveFromLedgerId(Long receiveFromLedgerId) {
		this.receiveFromLedgerId = receiveFromLedgerId;
	}

	public Long getCostHeadGroupId() {
		return costHeadGroupId;
	}

	public void setCostHeadGroupId(Long costHeadGroupId) {
		this.costHeadGroupId = costHeadGroupId;
	}

	public Long getCostHeadSubGroupId() {
		return costHeadSubGroupId;
	}

	public void setCostHeadSubGroupId(Long costHeadSubGroupId) {
		this.costHeadSubGroupId = costHeadSubGroupId;
	}

	public Long getYearEndId() {
		return yearEndId;
	}

	public void setYearEndId(Long yearEndId) {
		this.yearEndId = yearEndId;
	}

	@Override
	public String toString() {
		return "FinancialTransaction [tranasactionId=" + tranasactionId + ", companyId=" + companyId + ", voucherId="
				+ voucherId + ", srNo=" + srNo + ", forHead=" + forHead + ", forHeadId=" + forHeadId + ", description="
				+ description + ", transactionType=" + transactionType + ", amount=" + amount + ", localAmount="
				+ localAmount + ", dollarAmount=" + dollarAmount + ", modifiedOn=" + modifiedOn + ", modifiedBy="
				+ modifiedBy + ", modifiedMachineName=" + modifiedMachineName + ", ledgerId=" + ledgerId + ", active="
				+ active + ", paymentMode=" + paymentMode + ", chequeNo=" + chequeNo + ", chequeDate=" + chequeDate
				+ ", bankName=" + bankName + ", accountId=" + accountId + ", exchangeRate=" + exchangeRate
				+ ", transactionDate=" + transactionDate + ", tranasactionNo=" + tranasactionNo + ", receiveId="
				+ receiveId + ", receiveFromLedgerId=" + receiveFromLedgerId + ", costHeadGroupId=" + costHeadGroupId
				+ ", costHeadSubGroupId=" + costHeadSubGroupId + ", yearEndId=" + yearEndId + "]";
	}
	
	

}
