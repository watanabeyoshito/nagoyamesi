package com.example.nagoyamesi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Subscription;
import com.example.nagoyamesi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	private UserService userService;
	
	@Value("${stripe.api-key}")
	private String stripeApiKey;
	
	public String createStripeSession(HttpServletRequest httpServletRequest, User user) {
	    Stripe.apiKey = stripeApiKey;
	    
	    // ベースURLを取得
	    String requestUrl = httpServletRequest.getRequestURL().toString();
	    String baseUrl = requestUrl.replace(httpServletRequest.getRequestURI(), "");

	    SessionCreateParams params = SessionCreateParams.builder()
	            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
	            .addLineItem(
	                    SessionCreateParams.LineItem.builder()
	                            .setPriceData(
	                                    SessionCreateParams.LineItem.PriceData.builder()
	                                            .setProductData(
	                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
	                                                            .setName("有料プラン (月額)")
	                                                            .build())
	                                            .setUnitAmount(300L)
	                                            .setCurrency("jpy")
	                                            .setRecurring(
	                                                    SessionCreateParams.LineItem.PriceData.Recurring.builder()
	                                                            .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
	                                                            .build())
	                                            .build())
	                            .setQuantity(1L)
	                            .build())
	            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)

	            // 決済成功後にトップページにリダイレクト
	            .setSuccessUrl(baseUrl + "/?reserved")

	            // 決済キャンセル時のURL（必要に応じて設定）
	            .setCancelUrl(baseUrl + "/subscription/delete")
	            .putMetadata("userId", String.valueOf(user.getId()))
	            .build();
	    
	    try {
	        Session session = Session.create(params);
	        return session.getUrl();  // URLを返す
	    } catch (StripeException e) {
	        System.err.println("Stripeエラー: " + e.getMessage());
	        e.printStackTrace();
	        return "";
	    }
	}
	
	public void processSessionCompleted(Event event) {
		Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
		String stripeCustomerId = session.getCustomer();
		Subscription subscription = userService.findSubscriptionByStripeCustomerId(stripeCustomerId);
		session.getCustomer();
		User user = subscription.getUser();
		userService.upgradeRole(user.getId());

	}
}