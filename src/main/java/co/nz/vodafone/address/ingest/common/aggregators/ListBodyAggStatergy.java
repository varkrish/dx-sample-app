package co.nz.vodafone.address.ingest.common.aggregators;

import java.util.ArrayList;
import java.util.Map;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class ListBodyAggStatergy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (null == oldExchange) {
			oldExchange = newExchange;
			return oldExchange;
		}
		if (null != newExchange) {
			@SuppressWarnings("unchecked")
			ArrayList<Map<String, Object>> newBody = newExchange.getIn().getBody(ArrayList.class);
			if (null != newBody) {
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> existingBody = oldExchange.getIn().getBody(ArrayList.class);
				existingBody.add(newBody.get(0));
				oldExchange.getIn().getHeaders().putAll(newExchange.getIn().getHeaders());
				oldExchange.getAllProperties().putAll(newExchange.getAllProperties());
			}
		}
		return oldExchange;
	}

}
