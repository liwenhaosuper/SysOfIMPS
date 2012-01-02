SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `imps` ;
CREATE SCHEMA IF NOT EXISTS `imps` DEFAULT CHARACTER SET utf8 ;
USE `imps` ;

-- -----------------------------------------------------
-- Table `imps`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `imps`.`user` ;

CREATE  TABLE IF NOT EXISTS `imps`.`user` (
  `userid` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(20) NOT NULL ,
  `pwd` VARCHAR(20) NOT NULL ,
  `gender` INT(11) NULL DEFAULT '0' ,
  `email` VARCHAR(30) NULL DEFAULT NULL ,
  PRIMARY KEY (`userid`) )
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `imps`.`friend`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `imps`.`friend` ;

CREATE  TABLE IF NOT EXISTS `imps`.`friend` (
  `userid` BIGINT(20) NOT NULL ,
  `use_userid` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`userid`, `use_userid`) ,
  INDEX `FK_friend2` (`use_userid` ASC) ,
  CONSTRAINT `FK_friend`
    FOREIGN KEY (`userid` )
    REFERENCES `imps`.`user` (`userid` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_friend2`
    FOREIGN KEY (`use_userid` )
    REFERENCES `imps`.`user` (`userid` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `imps`.`message`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `imps`.`message` ;

CREATE  TABLE IF NOT EXISTS `imps`.`message` (
  `m_id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `use_userid` BIGINT(20) NOT NULL ,
  `userid` BIGINT(20) NOT NULL ,
  `m_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `message` VARCHAR(500) NULL DEFAULT NULL ,
  PRIMARY KEY (`m_id`) ,
  INDEX `FK_message` (`use_userid` ASC) ,
  INDEX `FK_message2` (`userid` ASC) ,
  CONSTRAINT `FK_message`
    FOREIGN KEY (`use_userid` )
    REFERENCES `imps`.`user` (`userid` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_message2`
    FOREIGN KEY (`userid` )
    REFERENCES `imps`.`user` (`userid` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 305
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `imps`.`position`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `imps`.`position` ;

CREATE  TABLE IF NOT EXISTS `imps`.`position` (
  `userid` BIGINT(20) NOT NULL ,
  `p_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `x_location` FLOAT NULL DEFAULT '121' ,
  `p_id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `y_location` FLOAT NULL DEFAULT '31' ,
  PRIMARY KEY (`p_id`) ,
  INDEX `FK_userlocation` (`userid` ASC) ,
  CONSTRAINT `FK_userlocation`
    FOREIGN KEY (`userid` )
    REFERENCES `imps`.`user` (`userid` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 14003
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
