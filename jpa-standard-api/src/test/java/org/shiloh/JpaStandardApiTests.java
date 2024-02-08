package org.shiloh;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.shiloh.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Hibernate JPA 标准接口单元测试
 *
 * @author shiloh
 * @date 2024/2/7 17:14
 */
public class JpaStandardApiTests {
    private EntityManagerFactory entityManagerFactory;

    /**
     * 初始化
     *
     * @author shiloh
     * @date 2024/2/7 17:15
     */
    @Before
    public void setup() {
        // 该名称应该与 META-INF/persistence.xml 中的名称相同
        final String persistenceUnitName = "org.shiloh.jpa";
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    /**
     * 测试新增事件数据
     *
     * @author shiloh
     * @date 2024/2/7 17:21
     */
    @Test
    public void save() {
        this.inTransaction(entityManager -> {
            Assert.assertNotNull(entityManager);
            entityManager.persist(new Event("saving by jpa entity manager-01", LocalDateTime.now()));
            entityManager.persist(new Event("saving by jpa entity manager-02", LocalDateTime.now()));
        });
    }

    /**
     * 测试根据 ID 删除数据
     *
     * @author shiloh
     * @date 2024/2/7 17:35
     */
    @Test
    public void deleteById() {
        this.inTransaction(entityManager -> {
            final Event event = entityManager.find(Event.class, 3L);
            Assert.assertNotNull(event);
            entityManager.remove(event);
        });
    }

    /**
     * 测试根据 ID 更新数据
     *
     * @author shiloh
     * @date 2024/2/7 17:36
     */
    @Test
    public void updateById() {
        this.inTransaction(entityManager -> {
            final Event event = entityManager.find(Event.class, 5L);
            Assert.assertNotNull(event);
            event.setTitle("update by jpa standard api");
            entityManager.merge(event);
        });
    }

    /**
     * 测试查询所有事件数据
     *
     * @author shiloh
     * @date 2024/2/7 17:26
     */
    @Test
    public void findAll() {
        this.inTransaction(entityManager -> {
            final List<Event> events = entityManager.createQuery("from Event e", Event.class)
                    .getResultList();
            Assert.assertFalse(events.isEmpty());
            events.forEach(System.out::println);
        });
    }

    /**
     * {@link EntityManagerFactory} 没有类似 {@link org.hibernate.SessionFactory#inTransaction(Consumer)} 的方法
     * <p>
     * 这里自己封装一个差不多的
     *
     * @param action 要执行的操作
     * @author shiloh
     * @date 2024/2/7 17:18
     */
    public void inTransaction(Consumer<EntityManager> action) {
        try (final EntityManager entityManager = this.entityManagerFactory.createEntityManager()) {
            final EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                action.accept(entityManager);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(parseNum("10", Integer::parseInt));
        System.out.println(parseNum("10", Short::parseShort));
        execute(System.out::println);
        System.out.println(get(() -> "Hello World").toLowerCase());
        validate(i -> i < 10);
    }

    /**
     * {@link Function} 接收一个参数，并返回一个值
     * <p>
     * 参数类型与返回值类型有泛型决定
     *
     * @param number 字符串数字
     * @param parser 转换器
     * @author shiloh
     * @date 2024/2/7 18:13
     */
    public static <T extends Number> T parseNum(String number, Function<String, T> parser) {
        return parser.apply(number);
    }

    /**
     * {@link Consumer} 消费者：接收一个参数，执行指定操作
     *
     * @param action 需要执行的操作
     * @author shiloh
     * @date 2024/2/7 18:12
     */
    public static void execute(Consumer<String> action) {
        action.accept("hello world".toUpperCase());
    }

    /**
     * {@link Supplier} 提供一个值，具体类型取决于它的泛型
     *
     * @param supplier 提供者
     * @author shiloh
     * @date 2024/2/7 18:11
     */
    public static <T> T get(Supplier<T> supplier) {
        return supplier.get();
    }

    /**
     * {@link Predicate} 谓词，做判断用，接收一个参数，返回一个布尔值
     *
     * @param predicate 判断条件
     * @author shiloh
     * @date 2024/2/7 18:10
     */
    public static void validate(Predicate<Integer> predicate) {
        if (predicate.test(10)) {
            System.out.println("valid:)");
            return;
        }

        System.out.println("invalid:(");
    }
}
