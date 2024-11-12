package io.linkedlogics.example;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition.BaseLogicBuilder;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;

public class FriendsAndFamilyLogics {
	public static final String FF_STOP = "ff_stop";
	
	public static final String SEND_NOTIFICATION = "send_notification";
	public static final String GET_FF_INFO = "get_ff_info";
	public static final String REMOVE_OFFER = "remove_offer";
	public static final String REMOVE_OFFERS = "remove_offers";
	
	public static final String NGBSS_ADAPTER = "ngbss_adapter";
	
	public static ProcessDefinition ffStop() {
		return createProcess(FF_STOP, 0)
				.add(getRequesterAorB())
				.add(branch(isRequesterAorB(), handleA(), handleB()))
				.build();
	}
	
	private static BaseLogicBuilder<?, ?> handleA() {
		return branch(isStopAll(), removeAll(), removeB());
	}
	
	private static BaseLogicBuilder handleB() {
		return branch(isStopAll(), sendNotification("WRONG_SMS"), removeB());
	}
	
	private static GroupLogicBuilder removeB() {
		return group(removeOffer(expr("request.msisdn_b")),
					 branch(isLastB(), removeA(), sendNotification("TEMPLATE_B")));
	}
	
	private static GroupLogicBuilder removeA() {
		return group(removeOffer(expr("request.msisdn_a")),
				sendNotification("TEMPLATE_A"));
	}
	
	private static GroupLogicBuilder removeAll() {
		return group(removeOffers(), removeA());
	}
	
	private static SingleLogicBuilder getRequesterAorB() {
		return logic(GET_FF_INFO).input("msisdn", expr("request.msisdn")).returnAs("ff").application(NGBSS_ADAPTER);
	}
	
	private static ExpressionLogicDefinition isRequesterAorB() {
		return expr("msisdn == ff.msisdn_a");
	}
	
	private static ExpressionLogicDefinition isLastB() {
		return expr("ff.numbers.length == 1");
	}
	
	private static SingleLogicBuilder removeOffer(ExpressionLogicDefinition removeMsisdn) {
		return logic(REMOVE_OFFER).input("msisdn", removeMsisdn).input("offer_id", "FF_OFFER_ID").application(NGBSS_ADAPTER);
	}
	
	private static SingleLogicBuilder removeOffers() {
		return logic(REMOVE_OFFERS).input("msisdn", expr("request.msisdn_a")).input("offer_id", "FF_OFFER_ID").application(NGBSS_ADAPTER);
	}
	
	private static SingleLogicBuilder sendNotification(String template) {
		return logic(SEND_NOTIFICATION).input("template", template);
	}
	
	private static ExpressionLogicDefinition isStopAll() {
		return expr("request.text == 'STOPALL'");
	}
}
