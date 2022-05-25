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

-- create table  for psychiatrist
create table psychiatrist(
    id int not null auto_increment,
    name varchar(255) not null,
    phone varchar(255) not null,
    primary key(id)
);

-- insert into patient
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Pradyumna', '123 Main St', 'prgayake@gmail.com', '1234567890', '12345678', '', 1);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Aniruddha', '123 Main St', 'aniruddha@gmail.com', '1234567890', '12345678', '', 1);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Atharva', '123 Main St', 'atharva@gmail.com', '1234567890', '12345678', '', 2);
insert into patient(name, address, email, phone, password, photo, psychiatrist_id) values('Mayur', '123 Main St', 'mayur@gmail.com', '1234567890', '12345678', '', 2);

-- insert into psychiatrist
insert into psychiatrist(name, phone) values('Dr.Anuj', '1234567890');
insert into psychiatrist(name, phone) values('Dr.Rohan', '1234567890');
insert into psychiatrist(name, phone) values('Dr.Sneha', '1234567890');