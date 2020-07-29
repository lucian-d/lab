package ld.lab.camel.sql;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.osgi.service.component.annotations.Reference;

/**
 * Bean that creates payments table in an existing database.
 */
public class TableSetupBean {

    private static final Logger LOG = LoggerFactory.getLogger(TableSetupBean.class);

    //@Reference(target = "(dataSourceName=jdbc/ds-payments)")
    @Reference(target = "(osgi.jndi.service.name=jdbc/ds-payments)")
    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        String sql = "create table payments (\n"
              + "  id integer primary key,\n"
              + "  debit varchar(10),\n"
              + "  credit varchar(10),\n"
              + "  amount integer,\n"
              + "  processed boolean\n"
              + ")";

        try {
            jdbc.execute("drop table payments");
        } catch (Throwable e) {
            // ignore
        }

        jdbc.execute(sql);

        LOG.info("Created table 'payments'.");
    }

    public void destroy() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        try {
            jdbc.execute("drop table payments");
        } catch (Throwable e) {
            // ignore
        }
    }
}
