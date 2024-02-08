package org.shiloh;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.shiloh.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Hibernate 单元测试
 *
 * @author shiloh
 * @date 2024/2/7 16:58
 */
public class HibernateTests {
    private SessionFactory sessionFactory;

    /**
     * 初始化
     *
     * @author shiloh
     * @date 2024/2/7 16:59
     */
    @Before
    public void setup() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .build();

        try {
            this.sessionFactory = new MetadataSources(registry)
                    // 注册实体类
                    .addAnnotatedClass(Event.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 测试新增数据
     *
     * @author shiloh
     * @date 2024/2/7 17:01
     */
    @Test
    public void save() {
        this.sessionFactory.inTransaction(session -> {
            Assert.assertNotNull(session);
            session.persist(new Event("这是我们的第一个事件！", LocalDateTime.now()));
            session.persist(new Event("这是我们的第二" +
                    "个事件！", LocalDateTime.now()));
        });
    }

    /**
     * 测试根据 ID 删除事件数据
     *
     * @author shiloh
     * @date 2024/2/7 17:29
     */
    @Test
    public void deleteById() {
        this.sessionFactory.inTransaction(session -> {
            session.remove(session.find(Event.class, 1L));
            Assert.assertNull(session.find(Event.class, 1L));
        });
    }

    /**
     * 测试根据 ID 更新事件数据
     *
     * @author shiloh
     * @date 2024/2/7 17:31
     */
    @Test
    public void updateById() {
        this.sessionFactory.inTransaction(session -> {
            final Event event = session.find(Event.class, 3L);
            Assert.assertNotNull(event);
            event.setTitle("update by id 123");
            session.merge(event);
        });
    }

    /**
     * 测试查询所有数据
     *
     * @author shiloh
     * @date 2024/2/7 17:03
     */
    @Test
    public void findAll() {
        this.sessionFactory.inTransaction(session -> {
            final List<Event> events = session.createSelectionQuery("from Event", Event.class)
                    .getResultList();
            Assert.assertFalse(events.isEmpty());
            events.forEach(System.out::println);
        });
    }
}
