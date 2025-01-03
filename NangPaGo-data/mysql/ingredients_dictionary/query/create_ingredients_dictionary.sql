DROP TABLE IF EXISTS refrigerator;
DROP TABLE IF EXISTS ingredients_dictionary;

CREATE TABLE ingredients_dictionary (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) DEFAULT NULL
);

CREATE TABLE refrigerator (
  `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` bigint NOT NULL,
  `ingredient_id` bigint NOT NULL,
  UNIQUE KEY `unique_user_ingredient` (`user_id`,`ingredient_id`),
  CONSTRAINT `refrigeratortoingredient` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients_dictionary` (`id`),
  CONSTRAINT `refrigeratortouser` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);
