package com.example.springbatchdemo.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pay2 {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long amount;
    private String txName;

    @CreatedDate
    private LocalDateTime txDateTime;

    public Pay2(Long amount, String txName) {
        this.amount = amount;
        this.txName = txName;
    }

    @Builder
    public Pay2(Long id, Long amount, String txName) {
        this.id = id;
        this.amount = amount;
        this.txName = txName;
    }
}