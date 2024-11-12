package io.linkedlogics.example;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.exit;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition.BaseLogicBuilder;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import lombok.Data;

public class OfferActivateLogics {
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;
	public static final String GET_SUBSCRIBER_INFO = "GET_SUBSCRIBER_INFO";
	public static final String SEND_FAIL_NOTIFICATION = "SEND_FAIL_NOTIFICATION";
	public static final String SEND_SUCCESS_NOTIFICATION = "SEND_SUCCESS_NOTIFICATION";
	public static final String ACTIVATE_OFFER = "ACTIVATE_OFFER";
	public static final String DEACTIVATE_OFFER = "DEACTIVATE_OFFER";
	public static final String NGBSS_ADAPTER = "ngbss_adapter";
	
	public static final String STATUS = "ACTIVE";
	public static final String TYPE = "PREPAID";
	public static final Integer HOUR = 19; //LocalDateTime.now().getHour();
	
	public static ProcessDefinition offerActivate() {
		return createProcess(ACTIVATE_OFFER, 0)
				.add(getSubscriberInfo())
				.add(branch(checkNotActive(), sendFailNotification("NOT_ACTIVE")))
				.add(branch(checkNotPrepaid(), sendFailNotification("NOT_PREPAID")))
				.add(branch(checkNotTime(), sendFailNotification("NOT_TIME")))
				.add(activateOffer("OFFER_1")
						.compensate(deactivateOffer("OFFER_1"))
						.handle(error().using(sendFailNotification("NO_BALANCE"))))
				.add(sendSuccessNotification("SUCCESS"))
				.add(verify(expr("false")).disabled())
				.add(activateOffer("OFFER_2").delayed(5))
				.build();
	}
	
	private static SingleLogicBuilder getSubscriberInfo() {
		return logic(GET_SUBSCRIBER_INFO).application(NGBSS_ADAPTER);
	}
	
	private static GroupLogicBuilder sendFailNotification(String template) {
		return group(logic(SEND_FAIL_NOTIFICATION).input("msisdn", expr("msisdn")).input("template", template),
				exit());
	}
	
	private static SingleLogicBuilder sendSuccessNotification(String template) {
		return logic(SEND_SUCCESS_NOTIFICATION).input("msisdn", expr("msisdn")).input("template", template);
	}
	
	private static SingleLogicBuilder activateOffer(String offerId) {
		return logic(ACTIVATE_OFFER).input("msisdn", expr("msisdn")).input("offer", offerId);
	}
	
	private static SingleLogicBuilder deactivateOffer(String offerId) {
		return logic(DEACTIVATE_OFFER).input("msisdn", expr("msisdn")).input("offer", offerId);
	}
	
	private static ExpressionLogicDefinition checkNotActive() {
		return expr("subscriber.status != 'ACTIVE'");
	}
	
	private static ExpressionLogicDefinition checkNotPrepaid() {
		return expr("subscriber.type != 'PREPAID'");
	}
	
	private static ExpressionLogicDefinition checkNotTime() {
		return expr("hour < 18 || hour > 22");
	}
	
	@Logic(id = GET_SUBSCRIBER_INFO, returnAs = "subscriber")
	public static Subscriber getSubscriberInfo(@Input("msisdn") String msisdn) {
		Subscriber subscriber = new Subscriber();
		subscriber.setMsisdn(msisdn);
		subscriber.setStatus(STATUS);
		subscriber.setType(TYPE);
		return subscriber;
	}
	
	@Logic(id = SEND_FAIL_NOTIFICATION)
	public static void sendFailNotification(@Input("msisdn") String msisdn, @Input("template") String template) {
		System.out.println(String.format("%s notifying failure %s with template %s", getDate(), msisdn, template));
	}
	
	@Logic(id = SEND_SUCCESS_NOTIFICATION)
	public static void sendSuccessNotification(@Input("msisdn") String msisdn, @Input("template") String template) {
		System.out.println(String.format("%s notifying success %s with template %s", getDate(), msisdn, template));
	}
	
	@Logic(id = ACTIVATE_OFFER)
	public static void activateOffer(@Input("msisdn") String msisdn, @Input("offer") String offer) {
		System.out.println(String.format("%s activating offer %s for %s", getDate(), offer, msisdn));
	}
	
	@Logic(id = DEACTIVATE_OFFER)
	public static void deactivateOffer(@Input("msisdn") String msisdn, @Input("offer") String offer) {
		System.out.println(String.format("%s deactivating offer %s for %s", getDate(), offer, msisdn));
	}

	private static String getDate() {
		return formatter.format(LocalDateTime.now()).substring(0, 8);
	}
	
	@Data
	public static class Subscriber {
		private String status;
		private String type;
		private String msisdn;
	}
}
