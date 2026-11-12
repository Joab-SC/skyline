package com.uniquindio.skyline.infrastructure.persistence.jpa.dialect;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import java.util.Collection;

// H2 dialect that does not add a CHECK (column IN (...)) for columns whose values come from a converter,
// such as the airports (AirportConverter): Hibernate would list the ~115 IATA codes in a CHECK.
// H2 keeps the session that created that CHECK and uses it on every insert; when that connection
// is closed (always in @DataJpaTest, which has no pool), inserts fail with "The database has been closed".
// Enums with @Enumerated(STRING) are not affected: on H2 they use the native enum(...) type.
// Remove this class (and spring.jpa.database-platform) when H2 is no longer used.
public class SkylineH2Dialect extends H2Dialect {

    @Override
    public String getCheckCondition(String columnName, Collection<?> values, JdbcType jdbcType) {
        return null;
    }
}
