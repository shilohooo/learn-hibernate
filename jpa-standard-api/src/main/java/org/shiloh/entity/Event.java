package org.shiloh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 事件实体
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
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
