package com.mars.admin.domain.user.sort;

import org.springframework.data.domain.Sort;

    public enum SortType {
        ID_ASC("id", Sort.Direction.ASC),
        ID_DESC("id", Sort.Direction.DESC),
        NICKNAME_ASC("nickname", Sort.Direction.ASC),
        NICKNAME_DESC("nickname", Sort.Direction.DESC);

        private final String field;
        private final Sort.Direction direction;

        SortType(String field, Sort.Direction direction) {
            this.field = field;
            this.direction = direction;
        }

        public String getField() {
            return field;
        }

        public Sort.Direction getDirection() {
            return direction;
        }
    }
