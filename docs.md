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

## Базовые вызовы:

```java
public static void main(String[] args) {

        // -> Создание контекста
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");

        // -> Создание фабрики сессий для получения сессии
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        Session session = sessionFactory.openSession();

        // -> Тестовые объекты
        Student student1 = new Student("Vasya", 22);
        Student student2 = new Student("Pasha", 20);

        // -> Сохранение
        session.beginTransaction();
        session.persist(student1);
        session.persist(student2);
        session.getTransaction().commit();

        // -> Поиск
        // -> Сущность возвращается из кеша
        Student studentById1 = session.get(Student.class, 1L);
        System.out.println("Student 1: " + studentById1.toString());

        // -> JPQL запрос выполняется в базу
        Student studentById2 = session
                .createQuery("SELECT s FROM Student s WHERE s.id = :id", Student.class)
                .setParameter("id", 2L).getSingleResult();
        System.out.println("Student 2: " + studentById2.toString());

        // -> Обновление сущности
        session.beginTransaction();
        Student studentForUpdate = session.get(Student.class, 1L);
        studentForUpdate.setAge(30);
        studentForUpdate.setName("Dima");
        session.getTransaction().commit();

        
          // -> Удаление сущности
        session.beginTransaction();
        Student studentForDelete = session.get(Student.class, 2L);
        session.remove(studentForDelete);
        // JPQL
        session.createQuery("DELETE FROM Student s where s.id = 1").executeUpdate();
          // Нативный запрос
        session.createNativeQuery("delete from students s where s.id = 2").executeUpdate();
        session.getTransaction().commit();

        // -> Получение листа сущностей
        List<Student> allStudent = session.createQuery("select s from Student s", Student.class).list();

        // -> Поиск по полю name
        Student studentByName = session
                .createQuery("SELECT s FROM Student s WHERE s.name = :name", Student.class)
                .setParameter("name", "Pasha")
                .getSingleResult();
        System.out.println("Student name: " + studentByName.toString());

        session.beginTransaction();
        Student student3 = new Student("Pasha", 20); // -> Поле name помечено как уникальное, для проверки добавляется сущность с таким же именем
        session.persist(student3);
        session.getTransaction().commit();

        session.close();
    }
```