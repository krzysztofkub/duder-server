-- MySQL Script generated by MySQL Workbench
-- Thu Mar 26 21:53:22 2020
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema duder
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema duder
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `duder` DEFAULT CHARACTER SET latin1 ;
USE `duder` ;

-- -----------------------------------------------------
-- Table `duder`.`channel`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`channel` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `channel_type` VARCHAR(255) NULL DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`event` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`event_hobby`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`event_hobby` (
  `id_event` BIGINT(20) NOT NULL,
  `id_hobby` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_event`, `id_hobby`),
  INDEX `FKpj5j32emfumrlctn47fsopaho` (`id_hobby` ASC),
  CONSTRAINT `FK7r6cbuic6c35nar6brvru06qq`
    FOREIGN KEY (`id_event`)
    REFERENCES `duder`.`event` (`id`),
  CONSTRAINT `FKpj5j32emfumrlctn47fsopaho`
    FOREIGN KEY (`id_hobby`)
    REFERENCES `duder`.`hobby` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`hobby`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`hobby` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_teoriu5hias3bf0t8eqgnhn62` (`name` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `content` VARCHAR(255) NULL DEFAULT NULL,
  `message_type` VARCHAR(255) NULL DEFAULT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKb3y6etti1cfougkdr0qiiemgv` (`user_id` ASC),
  CONSTRAINT `FKb3y6etti1cfougkdr0qiiemgv`
    FOREIGN KEY (`user_id`)
    REFERENCES `duder`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ew1hvam8uwaknuaellwhqchhb` (`login` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`user_channel`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`user_channel` (
  `is_user_admin` BIT(1) NOT NULL,
  `id_user` BIGINT(20) NOT NULL,
  `id_channel` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_channel`, `id_user`),
  INDEX `FKg47a0iyx6vpen4ph7ay3bp2xj` (`id_user` ASC),
  CONSTRAINT `FKg47a0iyx6vpen4ph7ay3bp2xj`
    FOREIGN KEY (`id_user`)
    REFERENCES `duder`.`user` (`id`),
  CONSTRAINT `FKg5gno6h55gmk8iqpssgky358q`
    FOREIGN KEY (`id_channel`)
    REFERENCES `duder`.`channel` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`user_event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`user_event` (
  `is_user_host` BIT(1) NOT NULL,
  `is_user_interested` BIT(1) NOT NULL,
  `is_user_participant` BIT(1) NOT NULL,
  `id_user` BIGINT(20) NOT NULL,
  `id_event` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_event`, `id_user`),
  INDEX `FK2cfllq0heydmp7ahou0vv2422` (`id_user` ASC),
  CONSTRAINT `FK2cfllq0heydmp7ahou0vv2422`
    FOREIGN KEY (`id_user`)
    REFERENCES `duder`.`user` (`id`),
  CONSTRAINT `FKai9nui2utdlhhrjr3lyg55ng1`
    FOREIGN KEY (`id_event`)
    REFERENCES `duder`.`event` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`user_friend`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`user_friend` (
  `id_user` BIGINT(20) NOT NULL,
  `id_friend` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_user`, `id_friend`),
  INDEX `FK4k6mw1lvimxwrdow7ykqqlx0v` (`id_friend` ASC),
  CONSTRAINT `FK4k6mw1lvimxwrdow7ykqqlx0v`
    FOREIGN KEY (`id_friend`)
    REFERENCES `duder`.`user` (`id`),
  CONSTRAINT `FKhown93vaadjbkwdkd0nyt8pm4`
    FOREIGN KEY (`id_user`)
    REFERENCES `duder`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `duder`.`user_hobby`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `duder`.`user_hobby` (
  `id_user` BIGINT(20) NOT NULL,
  `id_hobby` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_user`, `id_hobby`),
  INDEX `FKlxx4ud2tvelhkvpce0x6ekcdh` (`id_hobby` ASC),
  CONSTRAINT `FKlxx4ud2tvelhkvpce0x6ekcdh`
    FOREIGN KEY (`id_hobby`)
    REFERENCES `duder`.`hobby` (`id`),
  CONSTRAINT `FKmha6pkdk8fefbfghejrtrdjtj`
    FOREIGN KEY (`id_user`)
    REFERENCES `duder`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
