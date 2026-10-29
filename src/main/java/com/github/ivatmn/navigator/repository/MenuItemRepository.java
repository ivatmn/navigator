package com.github.ivatmn.navigator.repository;

import com.github.ivatmn.navigator.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    // Получить все корневые пункты меню (без родителей)
    List<MenuItem> findByParentIsNullOrderByOrderAsc();

    // Получить корневые пункты меню, доступные хотя бы одной из ролей пользователя
    @Query("SELECT DISTINCT m FROM MenuItem m JOIN m.roles r WHERE m.parent IS NULL AND r.code IN :roleCodes ORDER BY m.order ASC")
    List<MenuItem> findRootMenuByRoles(@Param("roleCodes") Set<String> roleCodes);

    // Получить дочерние пункты меню, доступные хотя бы одной из ролей пользователя
    @Query("SELECT DISTINCT m FROM MenuItem m JOIN m.roles r WHERE m.parent.id = :parentId AND r.code IN :roleCodes ORDER BY m.order ASC")
    List<MenuItem> findChildMenuByRoles(@Param("parentId") Long parentId, @Param("roleCodes") Set<String> roleCodes);

    // Для корневых пунктов (у которых нет родителя)
    Optional<MenuItem> findByNameAndParentIsNull(String name);

    // Для дочерних (с родителем)
    Optional<MenuItem> findByNameAndParent(String name, MenuItem parent);
}