package fr.lasrybeskiwin.ledgercore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LedgerSchemaMigrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void coreLedgerTablesExistAfterFlywayMigration() {
        List<String> tables = jdbcTemplate.queryForList("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN (
                      'ledgers',
                      'accounts',
                      'ledger_transactions',
                      'entries'
                  )
                """,
                String.class
        );

        assertEquals(
                Set.of(
                        "ledgers",
                        "accounts",
                        "ledger_transactions",
                        "entries"
                ),
                Set.copyOf(tables)
        );
    }
}
