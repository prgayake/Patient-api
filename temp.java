package com.samyak.account.repositoryImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samyak.account.model.FinancialTransaction;
import com.samyak.account.model.PaymentDetails;
import com.samyak.account.repository.FinancialTransactionRepository;

@Service
public class FinancialTransactionRepositoryImpl implements FinancialTransactionRepository {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private SingleColRepositoryImpl<FinancialTransaction> finanTransData;
	
	@Override
	public Map<String, Object> getLastId() {
		
		Long lastId=((FinancialTransaction)finanTransData.getLastId(new FinancialTransaction(), "tranasactionId", "-1")).getTranasactionId();
		
		Map<String, Object> data1=new HashMap<String, Object>();
		
		data1.put("LastId", null==lastId?"0":lastId);
		
		return data1;
	}

	@Override
	public List<FinancialTransaction> getCashBankDetails(String voucherId) {
		// TODO Auto-generated method stub
		List<FinancialTransaction> ftList = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();

			CriteriaQuery<FinancialTransaction> cq = cb.createQuery(FinancialTransaction.class);
			Root<FinancialTransaction> root = cq.from(FinancialTransaction.class);

			cq.multiselect(root.get("tranasactionId"), root.get("forHeadId"), root.get("srNo"), root.get("amount"))
					.where(cb.equal(root.get("voucherId"), voucherId));

			ftList = em.createQuery(cq).getResultList();
		} catch (IndexOutOfBoundsException er) {

		}

		return ftList;
	}
	
}
