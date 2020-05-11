# Change Data Capture (CDC) with Embedded Debezium and SpringBoot

Based on: https://medium.com/@sohan_ganapathy/change-data-capture-cdc-with-embedded-debezium-and-springboot-6f10cd33d8ec

Differences:
- Docker compose setup with standard PostgreSQL container and
- Usage of `pgoutput` instead of `decoderbufs`
- Uses newer Debezium API
- Student table with *UUID* based ID and an additional *ZonedDateTime* attribute

## Prerequisites
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Installing required tools

Once the prerequisites are installed, run the command.

```shell
docker-compose up -d
```

## Student table

```sql

-- for uuid generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- DROP TABLE public.student;

CREATE TABLE public.student
(
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255),
    email character varying(255),    
    modified TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

ALTER TABLE public.student REPLICA IDENTITY FULL;
```

## Starting the SpringBoot application

Adapt [application.properties](src/main/resources/application.properties) for the OFFSEt storage path
and database properties,

Then run the command

```shell
mvn spring-boot:run
```

## Scripts to Insert, Update and Delete a record on Postgres

```sql
INSERT INTO public.student(ID, NAME, EMAIL) VALUES('00000001-dead-cafe-ffff-111111111111','Jack','jack@gmail.com');
INSERT INTO public.student(ID, NAME, EMAIL) VALUES('00000002-dead-cafe-ffff-111111111111','Jim','jim@gmail.com');
INSERT INTO public.student(ID, NAME, EMAIL) VALUES('00000003-dead-cafe-ffff-111111111111','Peter','peter@gmail.com');

UPDATE public.student SET EMAIL='jill@gmail.com', NAME='Jill' WHERE ID = '00000002-dead-cafe-ffff-111111111111'; 

DELETE FROM public.student WHERE ID = '00000003-dead-cafe-ffff-111111111111';

INSERT INTO public.student(ID, NAME, EMAIL) VALUES('00000004-dead-cafe-ffff-111111111111','Hans','hans@gmail.com');
INSERT INTO public.student(ID, NAME, EMAIL) VALUES('00000003-dead-cafe-ffff-111111111111','Peter','peter@gmail.com');
```

## Elasticsearch commands to test if CDC worked !

```shell
curl -X GET http://localhost:9200/student/student/2?pretty=true
curl -X GET http://localhost:9200/student/student/3?pretty=true
```
