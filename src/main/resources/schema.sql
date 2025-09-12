create sequence if not exists pt_lobs_seq start with 100 increment by 1;

create table if not exists pt_lobs (
  id bigint primary key,
  code varchar(128) not null unique,
  name varchar(512) not null,
  lob jsonb not null,
  isDeleted boolean not null default false
);

create sequence if not exists pt_products_seq start with 1000 increment by 1;

create table if not exists pt_products (
  id bigint primary key,
  lob varchar(30) not null,
  code varchar(30) not null unique,
  name varchar(250) not null,
  prod_version_no integer,
  dev_version_no integer,
  isDeleted boolean not null default false
);

create table if not exists pt_product_versions (
  pk bigserial primary key,
  product_id bigint not null,
  version_no integer not null,
  product jsonb not null
);

create unique index if not exists pt_product_versions_uk on pt_product_versions(product_id, version_no);

create sequence if not exists pt_files_seq start with 1 increment by 1;

create table if not exists pt_files (
  id bigint primary key,
  file_type varchar(30) not null,
  file_desc varchar(300) not null,
  product_code varchar(30) not null,
  file_body bytea,
  is_deleted boolean not null default false
);


create table if not exists pt_number_generators (
  id bigserial primary key,
  product_code varchar(100) not null,
  mask varchar(255) not null,
  reset_policy varchar(20) not null,
  max_value int not null default 999999,
  last_reset date not null default CURRENT_DATE,
  current_value int not null default 0
);


