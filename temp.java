package com.samyak.account.repository;

import java.util.Map;
import java.util.List;

import com.samyak.account.model.FinancialTransaction;

public interface FinancialTransactionRepository {

	public Map<String, Object> getLastId();
	public List<FinancialTransaction> getCashBankDetails(String voucherId);
}
