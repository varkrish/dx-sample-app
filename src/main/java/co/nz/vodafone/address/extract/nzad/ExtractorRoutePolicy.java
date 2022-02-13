package co.nz.organizationfone.address.extract.nzad;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.spi.RoutePolicy;

public class ExtractorRoutePolicy implements RoutePolicy {

    @Override
    public void onInit(Route route) {

    }

    @Override
    public void onRemove(Route route) {
    }

    @Override
    public void onStart(Route route) {
    }

    @Override
    public void onStop(Route route) {

    }

    @Override
    public void onSuspend(Route route) {
    }

    @Override
    public void onResume(Route route) {
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        var producerTemplate = exchange.getContext().createProducerTemplate();
        try {
            try (producerTemplate) {
                var ex = producerTemplate.request("direct:pingES", null).getException();
                if (null != ex)
                    throw ex;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            exchange.setException(e);
        }
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        // TODO Auto-generated method stub

    }

}
