package com.github.ivatmn.navigator.config;

import lombok.Data;
import java.util.List;

@Data
public class MenuConfigDto {
    private List<ViewDto> views;
    private List<MenuItemDto> menu;

    @Data
    public static class ViewDto {
        private String guid;
        private String name;
        private String code;
        private String sql;
    }

    @Data
    public static class MenuItemDto {
        private String name;
        private String menuType;
        private String icon;
        private Integer order;
        private String parentName; // Имя родителя для связи
        private String viewCode;   // Код представления, если type=VIEW
        // private String methodCode; // На будущее
    }
}