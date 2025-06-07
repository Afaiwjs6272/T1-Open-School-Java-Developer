INSERT INTO client (id, last_name, first_name, middle_name, client_id) VALUES
    (1, 'Ivanov', 'Ivan', 'Ivanovich', 1001),
    (2, 'Petrov', 'Petr', 'Petrovich', 1002),
    (3, 'Sidorov', 'Sidr', 'Sidorovich', 1003),
    (4, 'Smirnov', 'Alexey', 'Nikolaevich', 1004),
    (5, 'Kuznetsova', 'Anna', 'Sergeevna', 1005)
ON CONFLICT (id) DO NOTHING;

INSERT INTO account (id, client_id, type, balance) VALUES
    (101, 1, 'DEBIT', 10000.50),
    (102, 1, 'CREDIT', 5000.00),
    (103, 2, 'DEBIT', 7500.75),
    (104, 3, 'DEBIT', 12000.00),
    (105, 4, 'CREDIT', 3000.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO "transaction" (id, account_id, transaction_sum, transaction_time) VALUES
    (1001, 101, 250.00, '2025-05-20 10:15:00'),
    (1002, 101, -100.00, '2025-05-20 15:20:00'),
    (1003, 102, 500.00, '2025-05-19 12:00:00'),
    (1004, 103, -200.00, '2025-05-18 09:30:00'),
    (1005, 104, 1000.00, '2025-05-20 16:45:00'),
    (1006, 105, -150.00, '2025-05-20 11:00:00')
ON CONFLICT (id) DO NOTHING;
