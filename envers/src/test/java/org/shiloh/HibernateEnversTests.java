package org.shiloh;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import junit.framework.TestCase;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.Assert;
import org.shiloh.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * Hibernate Envers 单元测试
 *
 * @author shiloh
 * @date 2024/2/8 21:50
 */
public class HibernateEnversTests extends TestCase {
    private EntityManagerFactory entityManagerFactory;

    /**
     * 初始化
     *
     * @author shiloh
     * @date 2024/2/8 21:51
     */
    @Override
    protected void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("org.shiloh.jpa");
    }

    /**
     * Hibernate Envers 测试
     *
     * @author shiloh
     * @date 2024/2/8 21:53
     */
    public void testBasicUsage() {
        // 保存事件数据
        final String firstEventTitle = "Hibernate Envers first event data:)";
        this.inTransaction(entityManager -> {
            Assert.assertNotNull(entityManager);
            entityManager.persist(new Event(firstEventTitle, LocalDateTime.now()));
            entityManager.persist(new Event("Hibernate Envers second event data:)", LocalDateTime.now()));
        });

        // 查询所有事件数据
        this.inTransaction(entityManager -> {
            final List<Event> events = entityManager.createQuery("from Event ", Event.class)
                    .getResultList();
            Assert.assertFalse(events.isEmpty());
            events.forEach(System.out::println);
        });

        // 使用 Envers
        // 先修改数据
        final String appendMsg = "(rescheduled)";
        this.inTransaction(entityManager -> {
            final Event event = entityManager.find(Event.class, 1L);
            Assert.assertNotNull(event);
            event.setDate(LocalDateTime.now());
            event.setTitle(event.getTitle() + appendMsg);
        });

        // 然后使用 AuditReader 查看修改历史
        this.inTransaction(entityManager -> {
            final Event event = entityManager.find(Event.class, 1L);
            assertNotNull(event);
            assertEquals(firstEventTitle + appendMsg, event.getTitle());

            final AuditReader auditReader = AuditReaderFactory.get(entityManager);
            // 查询修改历史中的第一个版本
            final Event firstRevision = auditReader.find(Event.class, 1L, 1);
            assertNotNull(firstRevision);
            Assert.assertNotEquals(firstRevision.getTitle(), event.getTitle());
            Assert.assertNotEquals(firstRevision.getDate(), event.getDate());

            // 查询修改历史中的第二个版本
            final Event secondRevision = auditReader.find(Event.class, 1L, 3);
            assertNotNull(secondRevision);
            assertEquals(secondRevision.getTitle(), event.getTitle());
            assertEquals(secondRevision.getDate(), event.getDate());
        });
    }


    /**
     * 释放资源
     *
     * @author shiloh
     * @date 2024/2/8 21:52
     */
    @Override
    protected void tearDown() {
        entityManagerFactory.close();
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
    private void inTransaction(Consumer<EntityManager> action) {
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
            } finally {
                // entityManager.close();
            }
        }
    }
}
