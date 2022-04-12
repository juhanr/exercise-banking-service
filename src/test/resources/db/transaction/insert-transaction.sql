INSERT INTO transaction(account_id, amount, currency_iso_code, direction, description, account_balance_after)
VALUES (1, 100, 'EUR', 'IN', 'Test', 100);

UPDATE balance SET amount = 100 WHERE account_id = 1 AND currency_iso_code = 'EUR';
