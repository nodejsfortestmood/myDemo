package com.strategy.model;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Concept {
    private Long id;
    private String conceptName;
    private String conceptCode;
    private String conceptType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
