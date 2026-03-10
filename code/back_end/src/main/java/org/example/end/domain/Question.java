package org.example.end.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.end.domain.enums.QuestionType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "idx_questions_assignment", columnList = "assignment_id")
        }
)
// 问题
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionType type;
    // 提示
    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;
    // 选项
    @Column(columnDefinition = "TEXT")
    private String optionsJson;
    // 答案
    @Column(columnDefinition = "TEXT")
    private String answerKeyJson;
    // 分数
    @Column(nullable = false)
    private Integer score;
}


