package fr.lasrybeskiwin.ledgercore.account;

import fr.lasrybeskiwin.ledgercore.ledger.Ledger;
import fr.lasrybeskiwin.ledgercore.ledger.LedgerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class AccountRepositoryTests {

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private LedgerRepository ledgerRepository;

        @Autowired
        private EntityManager entityManager;

        @Test
        void savesAndReloadsAccountBelongingToLedger() {
                Ledger ledger = ledgerRepository.saveAndFlush(
                                new Ledger(
                                                "Main EUR Ledger",
                                                Currency.getInstance("EUR")));

                UUID ledgerId = ledger.getId();

                Account account = new Account(
                                ledger,
                                "CASH",
                                "Cash");

                Account saved = accountRepository.saveAndFlush(account);

                UUID accountId = saved.getId();

                assertNotNull(accountId);
                assertEquals(7, accountId.version());

                entityManager.clear();

                Account reloaded = accountRepository
                                .findById(accountId)
                                .orElseThrow();

                assertEquals("CASH", reloaded.getCode());
                assertEquals("Cash", reloaded.getName());
                assertEquals(ledgerId, reloaded.getLedger().getId());
                assertNotNull(reloaded.getCreatedAt());
        }

        @Test
        void rejectsDuplicateAccountCodeWithinSameLedger() {
                Ledger ledger = ledgerRepository.saveAndFlush(
                                new Ledger(
                                                "Main EUR Ledger",
                                                Currency.getInstance("EUR")));

                accountRepository.saveAndFlush(
                                new Account(
                                                ledger,
                                                "CASH",
                                                "Primary Cash"));

                Account duplicate = new Account(
                                ledger,
                                "CASH",
                                "Secondary Cash");

                assertThrows(
                                DataIntegrityViolationException.class,
                                () -> accountRepository.saveAndFlush(duplicate));
        }

        @Test
        void allowsSameAccountCodeInDifferentLedgers() {
                Ledger euroLedger = ledgerRepository.saveAndFlush(
                                new Ledger(
                                                "EUR Ledger",
                                                Currency.getInstance("EUR")));

                Ledger usdLedger = ledgerRepository.saveAndFlush(
                                new Ledger(
                                                "USD Ledger",
                                                Currency.getInstance("USD")));

                Account euroCash = accountRepository.saveAndFlush(
                                new Account(
                                                euroLedger,
                                                "CASH",
                                                "EUR Cash"));

                Account usdCash = accountRepository.saveAndFlush(
                                new Account(
                                                usdLedger,
                                                "CASH",
                                                "USD Cash"));

                assertNotNull(euroCash.getId());
                assertNotNull(usdCash.getId());

                assertEquals("CASH", euroCash.getCode());
                assertEquals("CASH", usdCash.getCode());
        }
}
