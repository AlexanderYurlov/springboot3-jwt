-- Test user generation
-- password for test users 12345678
INSERT INTO public.users ("date_of_birth",
                          "name",
                          "password",
                          id)
VALUES ('1285-01-01', 'Vasya', '$2a$10$reWROLTWMEwZCKVdOP/ceOeK9HppwY7XPfgaHZtDJ2mWf486Afzvm', '1'),
       ('1285-01-01', 'Masha', '$2a$10$reWROLTWMEwZCKVdOP/ceOeK9HppwY7XPfgaHZtDJ2mWf486Afzvm', '2'),
       ('1285-01-01', 'Katrin', '$2a$10$reWROLTWMEwZCKVdOP/ceOeK9HppwY7XPfgaHZtDJ2mWf486Afzvm', '3');

INSERT INTO public.account (
                            balance,
                            user_id,
                            id)
VALUES ('100.00', '1','1'),
       ('100.00', '2','2'),
       ('100.00', '3','3');

INSERT INTO public.phone (
                            phone,
                            id,
                            user_id)
VALUES ('79995553777', '1','1'),
       ('79995554567', '2','1'),
       ('79995577777', '3','2'),
       ('79995554568', '4','3'),
       ('79995552789', '5','3');

INSERT INTO public.email (
                            email,
                            id,
                            user_id)
VALUES ('ttddasr@d11sxc1x.q2w', '1','1'),
       ('ttddasr@d11s', '2','1'),
       ('@d11sxc1x.q2w', '3','2'),
       ('ttddasr@d11sxc1x.zx', '4','3'),
       ('ttddasr@d11sxc1x.q2wada', '5','3');
