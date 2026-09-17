package fr.lasrybeskiwin.ledgercore.ledger;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class LedgerRepositoryTests {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesAndReloadsLedger() {
        Ledger ledger = new Ledger(
                "Main EUR Ledger",
                Currency.getInstance("EUR")
        );

        Ledger saved = ledgerRepository.saveAndFlush(ledger);

        UUID ledgerId = saved.getId();

        assertNotNull(ledgerId);
	assertEquals(7, ledgerId.version());
	
        entityManager.clear();

        Ledger reloaded = ledgerRepository
                .findById(ledgerId)
                .orElseThrow();

        assertEquals("Main EUR Ledger", reloaded.getName());
        assertEquals(Currency.getInstance("EUR"), reloaded.getCurrency());
        assertNotNull(reloaded.getCreatedAt());
    }
}
