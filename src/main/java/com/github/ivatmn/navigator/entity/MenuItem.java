package com.github.ivatmn.navigator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Иерархия (для подменю)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MenuItem parent;

    // Тип: VIEW (представление) или FORM (форма/метод)
    @Column(name = "menu_type", nullable = false)
    private String menuType = "VIEW";

    // Ссылка на представление
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_id")
    private ViewDefinition viewDefinition;

    // Ссылка на форму/метод (добавлено!)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id")
    private MethodDefinition methodDefinition;

    @Column(name = "item_order")
    private Integer order;

    @Column(name = "new_window")
    private Boolean newWindow = false;

    // Иконка (например, "bi-people", "bi-building")
    @Column(name = "icon", length = 50)
    private String icon;

    // Доступ к пункту меню по ролям
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_item_role",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}