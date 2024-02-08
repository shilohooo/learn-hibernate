package org.shiloh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 事件实体
 * 每个实体都应该提供无参构造器，Hibernate 需要利用反射访问该构造器，以实例化该对象。
 * <p>
 * 无参构造器的访问级别应为：public、package-private、private，以便 Hibernate 能够为该实体
 * 创建代理，以及优化字段访问的代码（getter、setter），例如：在 getter 中实现懒加载
 *
 * @author shiloh
 * @date 2024/2/7 16:55
 */
@Setter
@Getter
@Entity
@Table(name = "tb_event")
@ToString
@NoArgsConstructor
public class Event {
    /**
     * 每个实体都必须有一个唯一标识符
     * <p>
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件标题
     */
    private String title;

    /**
     * 事件发生时间
     */
    @Column(name = "event_date")
    private LocalDateTime date;

    public Event(String title, LocalDateTime date) {
        this.title = title;
        this.date = date;
    }
}
