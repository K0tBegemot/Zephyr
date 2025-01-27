package dev.kotbegemot.wind.config

import dev.kotbegemot.wind.config.property.DatabasePropertyConfig
import io.r2dbc.spi.ConnectionFactories
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object DatabaseConfig {
    private var dslContext: DSLContext = Nothing;
    fun initDatabase(databasePropertyConfig: DatabasePropertyConfig){
        dslContext = DSL.using(
            ConnectionFactories
                .get(

                ),
            SQLDialect.POSTGRES
        );
    }
}