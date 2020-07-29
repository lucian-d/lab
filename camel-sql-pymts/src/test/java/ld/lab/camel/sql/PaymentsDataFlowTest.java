package ld.lab.camel.sql;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;

import org.apache.camel.util.KeyValueHolder;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;

public class PaymentsDataFlowTest extends CamelBlueprintTestSupport {
	
    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/payments-data-flow.xml";
    }

    @Override
    protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {

        org.springframework.jdbc.datasource.DriverManagerDataSource mds = new org.springframework.jdbc.datasource.DriverManagerDataSource();
        mds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        mds.setUrl("jdbc:derby:memory:testdbpymts;create=true");

        Properties dictionary = new Properties();
        dictionary.setProperty("osgi.jndi.service.name","jdbc/ds-payments");
        //dictionary.setProperty("dataSourceName","jdbc/ds-payments");
        services.put("javax.sql.DataSource", asService(mds, dictionary));
    }

    @Test
    public void testProcessPaymentRoute() throws Exception {
        // the route is timer based, so every 5th second a message is send
        // we should then expect at least one message
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);

        // assert expectations
        assertMockEndpointsSatisfied();
    }

}
