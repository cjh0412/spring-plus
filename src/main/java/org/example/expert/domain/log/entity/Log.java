package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
@Table(name = "log")
@Getter
@NoArgsConstructor
public class Log extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long managerId;
    private String action; // 등록, 수정, 삭제 등의 액션 유형

    public Log(Long managerId, String action) {
        this.managerId = managerId;
        this.action = action;
    }
}
