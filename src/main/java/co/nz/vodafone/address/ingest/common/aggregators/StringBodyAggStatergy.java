package co.nz.vodafone.address.ingest.common.aggregators;


import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class StringBodyAggStatergy implements AggregationStrategy {
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (null == oldExchange) {
			oldExchange = newExchange;
			return oldExchange;
		}
		if (null != newExchange) {
			String existingBody = oldExchange.getIn().getBody(String.class);
			if (null != newExchange.getIn().getBody(String.class)) {
				existingBody = existingBody.concat(System.lineSeparator())
						.concat(newExchange.getIn().getBody(String.class));
				oldExchange.getIn().setBody(existingBody);
				oldExchange.getIn().getHeaders().putAll(newExchange.getIn().getHeaders());
				oldExchange.getAllProperties().putAll(newExchange.getAllProperties());
			}
		}
		return oldExchange;
	}

}
