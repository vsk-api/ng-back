-- Initial database schema for PT application
-- This migration creates all the base tables and sequences

-- Sequences
create sequence if not exists pt_seq start with 1 increment by 1;

create sequence if not exists pt_lobs_seq start with 100 increment by 1;

create sequence if not exists pt_products_seq start with 1000 increment by 1;

create sequence if not exists pt_files_seq start with 1 increment by 1;

create sequence if not exists pt_calculators_seq start with 1 increment by 1;

create sequence if not exists coefficient_data_seq start with 1 increment by 1;

-- Tables
create table if not exists pt_lobs (
  id bigint primary key,
  code varchar(128) not null,
  name varchar(512) not null,
  lob jsonb not null,
  isDeleted boolean not null default false
);

create table if not exists pt_products (
  id bigint primary key,
  lob varchar(30) not null,
  code varchar(30) not null,
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

-- Calculator storage
create table if not exists pt_calculators (
  id bigserial primary key,
  product_id bigint not null,
  product_code varchar(30) not null,
  version_no int not null,
  package_no int not null,
  calculator jsonb not null
);

create unique index if not exists pt_calculators_uk on pt_calculators(product_id, version_no, package_no);

-- Coefficients storage
create table if not exists coefficient_data (
  id bigserial primary key,
  calculator_id bigint not null,
  coefficient_code varchar(128) not null,
  col0 varchar(255), col1 varchar(255), col2 varchar(255), col3 varchar(255), col4 varchar(255),
  col5 varchar(255), col6 varchar(255), col7 varchar(255), col8 varchar(255), col9 varchar(255), col10 varchar(255),
  result_value numeric
);

create index if not exists coefficient_data_calc_code_idx on coefficient_data(calculator_id, coefficient_code);
