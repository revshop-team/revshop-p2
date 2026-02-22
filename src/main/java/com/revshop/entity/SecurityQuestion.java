package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "security_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sec_qn_seq_gen")
    @SequenceGenerator(name = "sec_qn_seq_gen", sequenceName = "security_questions_seq", allocationSize = 1)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, length = 200)
    private String questionText;
}