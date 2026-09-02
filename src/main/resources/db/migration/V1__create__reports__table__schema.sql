CREATE TABLE reports(
	id UUID PRIMARY KEY,
	demanded_amount NUMERIC(10,2) NOT NULL,
	paid_amount NUMERIC(10,2) NOT NULL,
	state_id UUID NOT NULL,
	district_id UUID NOT NULL,
	taluk_id UUID NOT NULL,
	location VARCHAR(50) NOT NULL,
	reason VARCHAR(50) NOT NULL,
	department VARCHAR(25) NOT NULL,
	offcial_name VARCHAR(20),
	designation VARCHAR(20),
	proof VARCHAR(200),
	description VARCHAR(200)
);