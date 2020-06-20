package org.icreated.portal.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.icreated.portal.bean.CreditCard;
import org.icreated.portal.bean.Payment;
import org.icreated.portal.bean.SessionUser;
import org.icreated.portal.bean.VOpenItem;
import org.icreated.portal.service.InvoiceService;
import org.icreated.portal.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.icreated.portal.controller.PaymentController;

@RestController
@RequestMapping(path = "/payments")
public class PaymentController {
	
	CLogger log = CLogger.getCLogger(PaymentController.class);
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	InvoiceService invoiceService;

	
	@GetMapping("/all")
	public List<Payment>  getInvoices(@AuthenticationPrincipal SessionUser sessionUser) {
		
		return paymentService.findPayments(0, sessionUser.getPartnerId());
	}
	
	
	@PostMapping("/pay")
	public void postPaymentCreditCard(@AuthenticationPrincipal SessionUser sessionUser, @RequestBody CreditCard creditCard) {
		
		List<VOpenItem> openItems = invoiceService.findOpenItems(sessionUser.getPartnerId());
		BigDecimal openTotal = openItems.stream().map(VOpenItem::getOpenAmt).reduce(Env.ZERO, (subtotal, value)->subtotal.add(value));
		
		boolean isTotalEqual = openTotal.compareTo(creditCard.getAmt()) == 0;
		if (!isTotalEqual) {
			throw new AdempiereException("Totals from backend & frontend not are equal");
		}
		
		String trxName = Trx.createTrxName("portalPayments");
		Trx trx = Trx.get(trxName, true);
		
		try {
			paymentService.createPayments(sessionUser, openItems, creditCard, trxName);
			trx.commit();
			log.log(Level.INFO, "Transaction Payments Completed");
		} catch(Exception e) {
			log.log(Level.WARNING, "Not proceed. Transaction Payments Aborted");
			trx.rollback();
		} finally {
			trx.close();
			trx = null;
		}


	}

}