package org.icreated.portal.controller;

import java.util.List;

import org.icreated.portal.bean.Document;
import org.icreated.portal.bean.Invoice;
import org.icreated.portal.bean.SessionUser;
import org.icreated.portal.bean.VOpenItem;
import org.icreated.portal.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
	
	@Autowired
	InvoiceService invoiceService;

	
	@GetMapping("/all")
	public List<Document>  getInvoices(@AuthenticationPrincipal SessionUser user) {
		
		return invoiceService.findBPartnerInvoices(user.getPartnerId());
		
	}
	
	
	@GetMapping("/invoice/{invoiceId}")
	public Invoice  getInvoiceById(@PathVariable int invoiceId, @AuthenticationPrincipal SessionUser user) {
		
		return invoiceService.findInvoiceById(invoiceId, user.getPartnerId());
		
	}
	
	
	@GetMapping("/openitems")
	public List<VOpenItem>  getOpenItems(@AuthenticationPrincipal SessionUser user) {
		
		return invoiceService.findOpenItems(user.getPartnerId());
		
	}

}