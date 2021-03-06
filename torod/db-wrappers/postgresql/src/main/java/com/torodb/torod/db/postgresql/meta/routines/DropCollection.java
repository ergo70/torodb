
package com.torodb.torod.db.postgresql.meta.routines;

import com.torodb.torod.core.exceptions.ToroImplementationException;
import com.torodb.torod.db.postgresql.meta.CollectionSchema;
import com.torodb.torod.db.postgresql.meta.tables.CollectionsTable;
import com.torodb.torod.db.sql.AutoCloser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.*;
import org.jooq.*;
import org.jooq.impl.DSL;

/**
 *
 */
public class DropCollection {

    @SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
    public static void execute(Configuration jooqConf, CollectionSchema colSchema) {
        
        ConnectionProvider provider = jooqConf.connectionProvider();
        Connection connection = provider.acquire();
        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate("DROP SCHEMA "+colSchema.getName()+" CASCADE");
            
            DSLContext dsl = DSL.using(jooqConf);
            int deleted = dsl.deleteFrom(CollectionsTable.COLLECTIONS)
                    .where(
                            CollectionsTable.COLLECTIONS.NAME
                            .eq(colSchema.getCollection()))
                    .execute();
            assert deleted == 1;
        }
        catch (SQLException ex) {
            throw new ToroImplementationException(ex);
        } finally {
            AutoCloser.close(st);
        }
    }
}
