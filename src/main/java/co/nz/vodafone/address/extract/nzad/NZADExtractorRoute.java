package co.nz.vodafone.address.extract.nzad;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchComponent;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestParamType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ApplicationScoped
public class NZADExtractorRoute extends RouteBuilder {
    @ConfigProperty(name = "quarkus.elasticsearch.hosts", defaultValue = "127.0.0.1:9200")
    String esHostPort;
    @ConfigProperty(name = "quarkus.elasticsearch.username", defaultValue = "elastic")
    String esUser;
    @ConfigProperty(name = "quarkus.elasticsearch.password")
    String esPassword;
    @ConfigProperty(name = "quarkus.elasticsearch.protocol", defaultValue = "http")
    String esProtocol;
    @ConfigProperty(name = "ingest.extract.url")
    String extractURL;
    @ConfigProperty(name = "ingest.extract.path", defaultValue = "/tmp/input")
    String path;

    public void configure() throws Exception {
        ElasticsearchComponent elasticsearchComponent = new ElasticsearchComponent();
        elasticsearchComponent.setHostAddresses(esHostPort);
        elasticsearchComponent.setUser(esUser);
        elasticsearchComponent.setPassword(esPassword);
        elasticsearchComponent.setEnableSSL(esProtocol.equals("https"));
        getContext().addComponent("elasticsearch-rest-auth", elasticsearchComponent);
        JacksonDataFormat jsonDf = new JacksonDataFormat();
        jsonDf.setPrettyPrint(true);
        jsonDf.disableFeature(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonDf.disableFeature(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        jsonDf.disableFeature(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
        jsonDf.addModule(new JavaTimeModule());

        // @formatter:off

        from("timer://extract_trigger?repeatCount=1").autoStartup(true)
                .setHeader("enrichEndPoint")
                .constant(
                        extractURL + "?" + "httpClient.handleRedirects=true&httpClient.maxRedirects=3&disableStreamCache=true")
                .to("seda:extractfiles");

        rest().get("/download")
                .param().type(RestParamType.query).name("download_link").required(true).endParam()
                .route()
                .log("${headers.download_link}")
                .setHeader("enrichEndPoint").simple("${headers.download_link}")
                .to("seda:extractfiles")
                .setBody().simple("<html>Downloading file ${headers.download_link}</html>");
        ;

        from("seda:extractfiles")
                .setHeader("Accept-Encoding").constant("gzip, deflate, br")
                .toD("micrometer:timer:nzadextract.downloadtimer.elapsedtime?action=start")
                .removeHeaders("CamelHttp*")
                .log("______________${headers.enrichEndPoint}?httpClient.handleRedirects=true&httpClient.maxRedirects=3&disableStreamCache=true")
                .setHeader(Exchange.HTTP_URL).simple("${headers.enrichEndPoint}")
                .enrich().simple("${headers.enrichEndPoint}")
                //Content-Disposition=attachment;filename="standard_NZAD_extract_inc_wgs84_09_06_2021.zip";filename*=UTF-8''standard_NZAD_extract_inc_wgs84_09_06_2021.zip
                .log("${headers}")
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {

                        String dispositionHeader = exchange.getIn().getHeader("Content-Disposition", String.class);
                        if (null == dispositionHeader)
                            throw new Exception("Disposition Header Not found");
                        String fileName = "";
                        String[] values = dispositionHeader.split(";");
                        for (String keyval : values) {
                            if (keyval.contains("filename")) {
                                fileName = keyval.split("=")[1].replace("\"", "");
                                exchange.getIn().setHeader("CamelFileName", fileName);
                                break;
                            }
                        }

                    }
                })
                .toF("file://%s?", path)
                .toD("micrometer:timer:nzadextract.downloadtimer.elapsedtime?action=stop")
                .log("Completed ${headers}");

    }

}
