package com.github.ivatmn.navigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "view_definition")
@Getter
@Setter
public class ViewDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GUID (уникальный идентификатор, например UUID)
    @Column(nullable = false, unique = true, length = 100)
    private String guid;

    // Наименование для человека (например, "Справочник пользователей")
    @Column(nullable = false)
    private String name;

    // Системный код (например, "users_dict")
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    // SQL-запрос для получения данных
    @Column(columnDefinition = "TEXT")
    private String sql;

    // Ссылка на родительское представление (для вложенности/связей)
    // Аналог поля c_parent_view_id в вашей старой таблице kernel#views
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_view_id")
    private ViewDefinition parentView;
}