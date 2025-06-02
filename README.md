# Проект изучения Hibernate

Запуск контейнера:

```shell
docker pull postgres

docker run --name some-postgres -p 5433:5432  -e POSTGRES_PASSWORD=root -d postgres
```