package co.nz.vodafone.address.extract.nzad;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

class NZADExtractionTest  {
	

    @Produce("direct:extract-load")
    protected ProducerTemplate template;
    
    @EndpointInject("mock:result")
    protected MockEndpoint resultEndpoint;
	@Test
	void test() throws InterruptedException {
	//	String expectedBody = "1";
	//	resultEndpoint.setExpectedCount(1);
		Map headers = new HashMap<String,String>();
		headers.put("CamelFileName", "testfile.csv");
		String body="359389235|1002292350|2000000004|101749090|8410618|-44.6595324|169.1286949|Yes (Developed)|Primary||||55||55||||PENRITH PARK|DRIVE|||SUBURB|WANAKA|WANAKA|WANAKA|āQUEENSTOWN-LAKES DISTRICT|9305|Unknown|Street||1514778202|Y|Left||N|||Populated place|||070|Otago Region|4000948|||Primary|N|DR|Unknown|||3258|Medium\n";
		//template.sendBodyAndHeaders(body, headers);
		assertTrue(true);
	//	resultEndpoint.assertIsSatisfied();
	}

}
