create table patient(
    id int not null auto_increment,
    name varchar(255) not null,
    address varchar(255) not null,
    email varchar(255) not null,
    phone varchar(255) not null,
    password varchar(255) not null,
    photo blob not null,
    primary key(id),
    psychiatrist_id int not null,
    unique(email)
);



create table psychiatrist(
    id int not null auto_increment,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    hospital_name varchar(255) not null,
    phone varchar(255) not null,
    pincode varchar(255) not null,
    state varchar(255) not null,
    primary key(id)
);



-- insert into patient
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Pradyumna', '123 Main St', 'prgayake@gmail.com', '1234567890', '12345678', '', 1);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Aniruddha', '123 Main St', 'aniruddha@gmail.com', '1234567890', '12345678', '', 1);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Atharva', '123 Main St', 'atharva@gmail.com', '1234567890', '12345678', '', 2);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Mayur', '123 Main St', 'mayur@gmail.com', '1234567890', '12345678', '', 2);

-- insert into psychiatrist
insert into psychiatrist(first_name, last_name, hospital_name, phone, pincode, state) values('Dr.Pradyumna', 'Gayake', 'Hospital', '1234567890', '12345678', 'Karnataka');
insert into psychiatrist(first_name, last_name, hospital_name, phone, pincode, state) values('Dr.Aniruddha', 'Gayake', 'Hospital', '1234567890', '12345678', 'Maharashtra');

