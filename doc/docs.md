# Документация к проекту

## Запуск контейнера с PostgreSQL:

```shell
# Скачивание образа
docker pull postgres
# Запуск контейнера
docker run --name some-postgres -p 5433:5432  -e POSTGRES_PASSWORD=root -d postgres
```

====================================
------------------------------------

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

====================================
------------------------------------

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

## Жизненный цикл Entity

![hibernate_entity_lifecycle.jpeg](../img/hibernate_entity_lifecycle.jpeg)

1. `New instance of entity`<br>
2. `Transient State`<br>
3. `Persistent State`<br>
4. `Detached State`<br>
5. `Removed State`<br>

### Комментарии к изображению

Жизненный цикл Hibernate

Hibernate — это Java-фреймворк с открытым исходным кодом, который предоставляет
возможности объектно-реляционного отображения для реляционных баз данных.
Жизненный цикл Hibernate относится к различным состояниям, через которые может
проходить сущность в сеансе Hibernate.

1. Переходное состояние(Transient state):<br>
   Когда объект впервые создается с использованием ключевого слова new, он
   находится в переходном состоянии. Это означает, что объект не связан с
   сеансом Hibernate и не отслеживается Hibernate.

2. Постоянное состояние(Persistent state):<br>
   Когда объект сохраняется или сохраняется с использованием метода
   `session.save()` или `session.persist()`, он переходит в постоянное
   состояние. Это означает, что объект теперь связан с сеансом Hibernate, и
   любые изменения, внесенные в объект, будут отслеживаться Hibernate.

3. Отсоединенное состояние(Detached state):<br>
   Когда объект удаляется из сеанса Hibernate с использованием метода
   `session.evict()/detach()`, он переходит в отсоединенное состояние. Это
   означает, что объект больше не связан с сеансом Hibernate, и любые изменения,
   внесенные в объект, не будут отслеживаться Hibernate.

4. Состояние «Удален»(Removed state):<br>
   Когда объект удаляется из базы данных с помощью метода `session.delete()`, он
   переходит в состояние «Удален». Это означает, что объект помечен для удаления
   и будет удален из базы данных после завершения транзакции.

Важно понимать жизненный цикл Hibernate, чтобы правильно управлять состоянием
сущностей в приложении Hibernate и обеспечивать согласованность и целостность
данных в базе данных.

### Подробно:

Жизненный цикл сущности (entity) в Hibernate описывает состояния, через которые
проходит объект сущности в процессе взаимодействия с базой данных. Hibernate
управляет этими состояниями, чтобы синхронизировать данные между приложением и
базой данных. Жизненный цикл включает четыре основных состояния: **Transient**,
**Persistent**, **Detached** и **Removed**. Рассмотрим каждое состояние и
переходы между ними.

### 1. **Transient (Временное состояние)**

- **Описание**: Объект создан в приложении (с помощью `new`), но ещё не связан с
  сессией Hibernate и не сохранён в базе данных. У него нет идентификатора (ID),
  связанного с записью в базе.
- **Характеристики**:
    - Не отслеживается Hibernate.
    - Не имеет связи с базой данных.
    - Изменения объекта не влияют на базу данных.
- **Пример**:

```java
void demo() {
    user.setName("John");
    User user = new User();
}

  ```

Здесь объект `user` находится в состоянии **Transient**, так как он не связан
с сессией и не сохранён.

- **Переходы**:
    - В **Persistent**: Вызов методов `session.save()`, `session.persist()` или
      `session.saveOrUpdate()` связывает объект с сессией и базой данных.
    - В **Detached**: Если объект не сохраняется, он остаётся временным, пока не
      будет собран сборщиком мусора.

### 2. **Persistent (Управляемое состояние)**

- **Описание**: Объект связан с текущей сессией Hibernate и соответствует записи
  в базе данных. Он имеет идентификатор (ID), и любые изменения объекта
  автоматически синхронизируются с базой данных при коммите транзакции (механизм
  *dirty checking*).
- **Характеристики**:
    - Отслеживается Hibernate в пределах сессии.
    - Изменения объекта автоматически сохраняются в базу при вызове
      `session.flush()` или коммите транзакции.
    - Объект связан с определённой записью в базе данных.
- **Пример**:

```java
  void demo() {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    User user = new User();
    user.setName("John");
    session.save(user); // Теперь user в состоянии Persistent
    user.setName("Jane"); // Изменение отследится Hibernate
    tx.commit(); // Изменения сохранятся в базе
    session.close();
}

```

- **Переходы**:
    - В **Detached**: Закрытие сессии (`session.close()`) или очистка объекта из
      сессии (`session.evict(user)`) делает объект отсоединённым.
    - В **Removed**: Вызов `session.delete(user)` переводит объект в состояние
      удаления.
    - clear() - очищаются все сущности
    - Остаётся **Persistent**, пока сессия открыта и объект не удалён.

### 3. **Detached (Отсоединённое состояние)**

- **Описание**: Объект больше не связан с сессией Hibernate, но имеет
  идентификатор, соответствующий записи в базе данных. Изменения объекта не
  синхронизируются с базой автоматически.
- **Характеристики**:
    - Не отслеживается Hibernate.
    - Имеет идентификатор, связанный с базой данных.
    - Для синхронизации изменений нужно повторно ассоциировать объект с сессией.
- **Пример**:

```java

void demo() {
    Session session = sessionFactory.openSession();
    User user = session.get(User.class, 1L); // Persistent
    session.close(); // Теперь user в состоянии Detached
    user.setName("Alice"); // Изменения не сохраняются в базе
}

```

- **Переходы**:
    - В **Persistent**: Повторное связывание с сессией через `session.update()`,
      `session.merge()` или `session.saveOrUpdate()`.
    - В **Transient**: Если удалить идентификатор объекта (например,
      `user.setId(null)`), он становится временным, но это редкий случай.
    - В **Removed**: Если объект повторно ассоциировать с сессией и вызвать
      `session.delete()`.

### 4. **Removed (Удалённое состояние)**

- **Описание**: Объект помечен для удаления из базы данных, но всё ещё связан с
  сессией. После коммита транзакции запись удаляется из базы, и объект переходит
  в **Transient** состояние.
- **Характеристики**:
    - Отслеживается Hibernate в пределах сессии.
    - Помечен для удаления из базы данных.
    - После коммита транзакции запись удаляется.
- **Пример**:

```java

void demo() {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    User user = session.get(User.class, 1L); // Persistent
    session.delete(user); // Теперь user в состоянии Removed
    tx.commit(); // Запись удаляется из базы
    session.close(); // user становится Transient
}

  ```

- **Переходы**:
    - В **Transient**: После коммита транзакции и удаления записи из базы
      данных.
    - В **Persistent**: Если отменить удаление до коммита, например, повторно
      вызвав `session.save()` или `session.merge()`.

### Основные методы Hibernate, влияющие на жизненный цикл

- `session.save()`: Сохраняет объект, переводя его из **Transient** в *
  *Persistent**. Генерирует ID, если он отсутствует.
- `session.persist()`: Аналог `save()`, но не возвращает ID и не гарантирует
  немедленного сохранения.
- `session.update()`: Привязывает **Detached** объект к сессии, переводя его в *
  *Persistent**.
- `session.merge()`: Копирует состояние **Detached** объекта в **Persistent**
  объект, синхронизируя изменения.
- `session.delete()`: Переводит объект из **Persistent** в **Removed**.
- `session.evict()`: Отсоединяет объект от сессии, переводя его из **Persistent
  ** в **Detached**.
- `session.flush()`: Синхронизирует изменения **Persistent** объектов с базой
  данных.
- `session.refresh()`: Перезагружает состояние объекта из базы данных, обновляя
  **Persistent** объект.

### Пример полного жизненного цикла

```java
void demo() {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();

// Transient
    User user = new User();
    user.setName("John");

// Persistent
    session.save(user); // Сохраняем в базе, объект становится Persistent
    user.setName("Jane"); // Изменение отследится

// Detached
    session.evict(user); // Отсоединяем объект
    user.setName("Alice"); // Изменения не сохраняются

// Persistent (снова)
    session.merge(user); // Повторно ассоциируем, изменения синхронизируются
    user.setName("Bob");

// Removed
    session.delete(user); // Помечаем для удаления
    tx.commit(); // Сохраняем все изменения, удаляем запись
    session.close(); // Объект становится Transient    
}

```

### Ключевые моменты

- **Dirty Checking**: Hibernate автоматически отслеживает изменения в *
  *Persistent** объектах и синхронизирует их с базой при коммите.
- **Сессия**: Сессия является ключевым элементом управления жизненным циклом.
  Без сессии объект не может быть **Persistent** или **Removed**.
- **Каскадирование**: Настройка `cascade` в аннотациях (например,
  `@OneToMany(cascade = CascadeType.ALL)`) может автоматически распространять
  операции жизненного цикла на связанные сущности.
- **Lazy vs Eager Loading**: Загрузка связанных данных влияет на
  производительность, но не на сам жизненный цикл сущности.

## Code: работа с состояниями и примеры запросов

```java

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");

        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        Session session = sessionFactory.openSession();

        // -> Сущности переходят в состояние Transient, т.к. создаются через new
        Student student1 = new Student("Vasya", 22);
        Student student2 = new Student("Pasha", 20);

        // ========== -> Сохранение ========== //
        session.beginTransaction();

        // -> Сущность переходит в состояние Persist - отслеживается Hibernate
        session.persist(student1);  // -> метод переводит из Transient в Persistent
        session.persist(student2);
        session.getTransaction().commit();

        session.close(); // -> Detach - сессия больше не следит


        // ======= Detach -> Persistent ========= //
        // -> Новая сессия для примера перевода сущностей в разные состояния
        session = sessionFactory.openSession();
        // -> Из Detach в Persist.
        // Копирует состояние сущности из базы в состояние Persist
        // student1 - состояние отслеживаемое сессией
        student1 = session.merge(student1);
        session.beginTransaction();
        student1.setName("Dima");

        // -> Persistent -> Detach
        session.detach(student1);
        student1.setAge(32);


        session.getTransaction().commit();
        session.close();


//        // ====== -> Поиск -> Возвращается из кеша ======== //
//        Student studentById1 = session.get(Student.class, 1L);
//        System.out.println("Student 1: " + studentById1.toString());
//
//        // -> JPQL: Запрос из базы
//        var studentById2 = session
//                .createQuery("SELECT s FROM Student s WHERE s.id = :id", Student.class)
//                .setParameter("id", 2L).getSingleResult();
//        System.out.println("Student 2: " + studentById2.toString());
//
//        // ====== -> Обновление ======= //
//        session.beginTransaction();
//        Student studentForUpdate = session.get(Student.class, 1L);
//        studentForUpdate.setAge(30);
//        studentForUpdate.setName("Dima");
//        session.getTransaction().commit();
//
//        // ===== -> Удаление ======= //
//        session.beginTransaction();
//
//        Student studentForDelete = session.get(Student.class, 2L);
//        session.remove(studentForDelete);
//
//        session.createQuery("DELETE FROM Student s where s.id = 1").executeUpdate();
//        session
//                .createNativeQuery("delete from students s where s.id = 2")
//                .executeUpdate();
//        session.getTransaction().commit();
//
//        List<Student> allStudent = session
//                .createQuery("select s from Student s", Student.class).list();
//
//        Student studentByName = session
//                .createQuery("SELECT s FROM Student s WHERE s.name = :name", Student.class)
//                .setParameter("name", "Pasha")
//                .getSingleResult();
//        System.out.println("Student name: " + studentByName.toString());
//
//        session.beginTransaction();
//        Student student3 = new Student("Pasha", 20);
//        session.persist(student3);
//        session.getTransaction().commit();
//
//        session.close();
    }
}
```

================================================================================
--------------------------------------------------------------------------------

## Связи сущностей

### OneToOne

К Profile привязан Student через поле в Profile. Запрос к профилю.

```java

@Entity
@Table(name = "profiles")
public class Profile {

    // .......

    @OneToOne
    @JoinColumn(
            name = "student_id", // Поле в текущей таблицы 
            referencedColumnName = "id" // Поле в связанно й таблицы
    )
    public Student student;
}
```

Для запроса к Student, чтобы получить и Profile:

```java

@Entity
@Table(name = "student")
public class Student {

    @OneToOne(mappedBy = "student")
    public Profile profile;
}
```

Запрос в бд:

```sql
select s1_0.id,
       s1_0.student_age,
       s1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students s1_0
         left join profiles p1_0 on s1_0.id = p1_0.student_id
where s1_0.id = ?
```

При удалении связанных сущностей - сначала Profile, потом Student.

#### Каскадные операции

Определяют что делать с дочерними сущностями, если вызвано состояние
родительской сущности из списка.

Например, если Student переводится в состояние Persist - нужно ли
переводить Profile в это же состояние.

```java

@Entity
@Table(name = "students")
public class Student {

    @OneToOne(mappedBy = "student", cascade = CascadeType.PERSIST)
    private Profile profile;

}
```

Если Student в состоянии Persist - то и Profile в Persist

#### Типы каскадных операций:

    /** Cascade all operations */
    ALL, 

    /** Cascade persist operation */
    PERSIST, 

    /** Cascade merge operation */
    MERGE, 

    /** Cascade remove operation */
    REMOVE,

    /** Cascade refresh operation */
    REFRESH,

    /**
     * Cascade detach operation
     *
     * @since 2.0
     * 
     */   
    DETACH

### OneToMany

```java

Student {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

}

Group {
    @OneToMany(mappedBy = "group", fetch = FetchType.{TYPE})
    private List<Student> studentList;

}


```

**Для одной группы:**

```java
void demo() {

    // Запрос одной группы
    var session = sessionFactory.openSession();
    group1 = session.get(Group.class, 1L);
    session.close();

    // Запрос связанной сущности
    List<Student> studentList = group1.getStudentList();
    studentList.forEach(System.out::println);
}

```

Вернет:

```sql 
-- Для LAZY

-- Ленивая загрузка одной сущности: 
-- var session = sessionFactory.openSession();
-- group1 = session.get(Group.class, 1L);

select g1_0.id,
       g1_0.grad_year,
       g1_0.number
from student_group g1_0
where g1_0.id = ?;

-- Если запросить связанную сущность    
-- List<Student> studentList = group1.getStudentList();
-- studentList.forEach(System.out::println);

-- то выполнится еще один запрос

select sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students sl1_0
         left join profiles p1_0 on sl1_0.id = p1_0.student_id
where sl1_0.group_id = ?;

-->  Student{id=1, name='Vasya', age=22}
-->  Student{id=2, name='Pasha', age=20}


-- Для EAGER

-- При:
-- var session = sessionFactory.openSession();
-- group1 = session.get(Group.class, 1L);

select g1_0.id,
       g1_0.grad_year,
       g1_0.number,
       sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time 
from student_group g1_0 
    left join students sl1_0 on g1_0.id=sl1_0.group_id 
    left join profiles p1_0 on sl1_0.id=p1_0.student_id 
where g1_0.id=?;

-->  Student{id=1, name='Vasya', age=22}
-->  Student{id=2, name='Pasha', age=20}

-- При запросе связанных сущностей все равно выполняется этот же запрос:

select g1_0.id,
       g1_0.grad_year,
       g1_0.number,
       sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time 
from student_group g1_0 
    left join students sl1_0 on g1_0.id=sl1_0.group_id 
    left join profiles p1_0 on sl1_0.id=p1_0.student_id 
where g1_0.id=?


```

**Но если запросить все группы:**

```java
   public List<Group> findAll() {
    try (Session session = sessionFactory.openSession()) {
        return session.createQuery(
                "SELECT g FROM Group AS g", Group.class
        ).list();
    }
}
```

Вернется N+1:

```sql

-- Для LAZY:

-- Список всех групп
select g1_0.id, g1_0.grad_year, g1_0.number
from student_group g1_0;


-- Для EAGER:

-- Список всех групп

select g1_0.id, g1_0.grad_year, g1_0.number
from student_group g1_0;

-- Список студентов привязанных к группе:

select sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students sl1_0
         left join profiles p1_0 on sl1_0.id = p1_0.student_id
where sl1_0.group_id = ?;

select sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students sl1_0
         left join profiles p1_0 on sl1_0.id = p1_0.student_id
where sl1_0.group_id = ?;

select sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students sl1_0
         left join profiles p1_0 on sl1_0.id = p1_0.student_id
where sl1_0.group_id = ?;

```


**Решение**

```java
 public List<Group> findAll() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    """
                            SELECT g FROM Group AS g
                            LEFT JOIN FETCH g.studentList s 
                            LEFT JOIN FETCH s.profile
                            """, Group.class
            ).list();
        }
    }
```

Тогда:

```sql
select g1_0.id,
       g1_0.grad_year,
       g1_0.number,
       sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time 
from student_group g1_0 
    left join students sl1_0 on g1_0.id=sl1_0.group_id 
    left join profiles p1_0 on sl1_0.id=p1_0.student_id
```

## ManyToMany


```java
Stident {
    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id")
    )
    List<Course> courseList = new ArrayList<>();
}

Course {
    @ManyToMany(mappedBy = "courseList")
    private List<Student> stringList;
}
```

Добавление курса к студенту 

```java
  public void enrollStudentToCourse(Long courseId, Long studentId) {
        transactionHelper.executeInTransaction(session -> {
          var student = session.get(Student.class, studentId);
          var course = session.get(Course.class, courseId);
          student.getCourseList().add(course);
        });
    }
```

Запросы в базу:

```sql
select s1_0.id,
       s1_0.student_age,
       g1_0.id,
       g1_0.grad_year,
       g1_0.number,
       s1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students s1_0
         left join student_group g1_0 on g1_0.id = s1_0.group_id
         left join profiles p1_0 on s1_0.id = p1_0.student_id
where s1_0.id = ?;

select sl1_0.group_id,
       sl1_0.id,
       sl1_0.student_age,
       sl1_0.name,
       p1_0.id,
       p1_0.bio,
       p1_0.last_seen_time
from students sl1_0
         left join profiles p1_0 on sl1_0.id = p1_0.student_id
where sl1_0.group_id = ?;

select c1_0.id, c1_0.name, c1_0.type
from courses c1_0
where c1_0.id = ?;

select cl1_0.student_id, cl1_1.id, cl1_1.name, cl1_1.type
from student_courses cl1_0
         join courses cl1_1 on cl1_1.id = cl1_0.course_id
where cl1_0.student_id = ?;


insert into student_courses (student_id, course_id)
values (?, ?);

insert into student_courses (student_id, course_id)
values (?, ?);
 
 insert into student_courses (student_id, course_id)
values (?, ?);
```

Решение:

```java
  public void enrollStudentToCourse(Long courseId, Long studentId) {
        transactionHelper.executeInTransaction(session -> {
            String sql = """
                    INSERT INTO student_courses (student_id, course_id)
                    VALUES (:studentId, :courseId);
                    """;

            session.createNativeQuery(sql, Void.class)
                    .setParameter("studentId", studentId)
                    .setParameter("courseId", courseId)
            .executeUpdate();
         });
    }
```
Тогда:

```sql
INSERT INTO student_courses (student_id, course_id) VALUES (?, ?);
```