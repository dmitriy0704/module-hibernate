# Документация к проекту

## Запуск контейнера:

```shell
# Скачивание образа
docker pull postgres
# Запуск контейнера
docker run --name some-postgres -p 5433:5432  -e POSTGRES_PASSWORD=root -d postgres
```

## Конфигурация приложения

### Зависимости

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.2.7</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.6.17.Final</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.6</version>
    </dependency>
</dependencies>
```

### Базовые настройки hibernate

```java
@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addAnnotatedClass(Student.class)
                .addPackage("dev.folomkin")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5433/postgres")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop"); //-> Создание новой/удаление старой таблиц при старте


        return configuration.buildSessionFactory();
    }
}

```