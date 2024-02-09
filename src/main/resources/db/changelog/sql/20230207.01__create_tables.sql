CREATE TABLE public.users (
	id int8 NOT NULL,
	date_of_birth date NOT NULL,
	"name" varchar(500) NULL,
	"password" varchar(500) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE public.account (
	id int8 NOT NULL,
	balance numeric(38, 2) NOT NULL,
	user_id int8 NOT NULL,
	CONSTRAINT account_pkey PRIMARY KEY (id),
	CONSTRAINT uk_h6dr47em6vg85yuwt4e2roca4 UNIQUE (user_id),
	CONSTRAINT fkra7xoi9wtlcq07tmoxxe5jrh4 FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.phone (
	id int8 NOT NULL,
	phone varchar(13) NOT NULL,
	user_id int8 NOT NULL,
	CONSTRAINT phone_pkey PRIMARY KEY (id),
	CONSTRAINT uk_jtvcctmq0b3cny6m9puq88nnf UNIQUE (phone),
	CONSTRAINT fkik7a2etdorybvoolvchfcvgkx FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.email (
	id int8 NOT NULL,
	email varchar(200) NOT NULL,
	user_id int8 NOT NULL,
	CONSTRAINT email_pkey PRIMARY KEY (id),
	CONSTRAINT uk_ek29mh30yo2rxnsy4svwbgogh UNIQUE (email),
	CONSTRAINT fkah6v1juek8jb9ycg8cldv15d6 FOREIGN KEY (user_id) REFERENCES public.users(id)
);
