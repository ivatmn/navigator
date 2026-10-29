package com.github.ivatmn.navigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "method_definition")
@Getter
@Setter
public class MethodDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Ссылка на представление, к которому относится метод
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id", nullable = false)
    private ViewDefinition viewDefinition;

    // Тип метода (например, "FORM", "FILTER", "ACTION")
    @Column(name = "method_type")
    private String methodType;

    // URL или имя обработчика, который вызывается
    @Column(name = "handler")
    private String handler;

    // SQL-запрос для валидации (аналог c_validate_proc)
    @Column(columnDefinition = "TEXT")
    private String validateSql;
}