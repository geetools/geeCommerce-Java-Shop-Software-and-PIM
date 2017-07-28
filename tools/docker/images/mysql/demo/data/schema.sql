CREATE DATABASE  IF NOT EXISTS `gc_demo_local` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `gc_demo_local`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gc_demo_local
-- ------------------------------------------------------
-- Server version	5.6.33-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `increment_id`
--

DROP TABLE IF EXISTS `increment_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `increment_id` (
  `name` char(20) NOT NULL,
  `store_id` bigint(20) unsigned NOT NULL,
  `id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `last_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`name`,`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_stock`
--

DROP TABLE IF EXISTS `inventory_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inventory_stock` (
  `_id` bigint(20) unsigned NOT NULL,
  `prd_id` bigint(20) unsigned NOT NULL,
  `store_id` bigint(20) unsigned DEFAULT NULL,
  `req_ctx_id` bigint(20) unsigned DEFAULT NULL,
  `qty` int(11) NOT NULL DEFAULT '0',
  `backorder` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `cr_on` datetime DEFAULT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `IDX_UNIQUE_PRODUCT_STOCK` (`prd_id`,`req_ctx_id`),
  KEY `idx_inventory_stock_pr_id` (`prd_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `price`
--

DROP TABLE IF EXISTS `price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `price` (
  `_id` bigint(20) unsigned NOT NULL,
  `id2` varchar(45) DEFAULT NULL,
  `prd_id` bigint(20) unsigned NOT NULL,
  `store_id` bigint(20) unsigned DEFAULT NULL,
  `cust_id` bigint(20) unsigned DEFAULT NULL,
  `cust_grp_id` bigint(20) unsigned DEFAULT NULL,
  `currency` char(3) DEFAULT NULL,
  `country` char(2) DEFAULT NULL,
  `qty_from` int(11) unsigned DEFAULT '0',
  `type_id` bigint(20) unsigned DEFAULT '0',
  `type_obj_id` bigint(20) unsigned DEFAULT NULL,
  `price` decimal(12,4) unsigned NOT NULL,
  `valid_from` datetime DEFAULT NULL,
  `valid_to` datetime DEFAULT NULL,
  `with_prd_ids` text,
  `cr_on` datetime DEFAULT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  UNIQUE KEY `IDX_UNIQUE_PRODUCT_PRICE` (`prd_id`,`store_id`,`cust_id`,`cust_grp_id`,`currency`,`country`,`qty_from`,`type_id`),
  KEY `idx_price_pr_id` (`prd_id`),
  KEY `idx_product_prd_id_currency` (`prd_id`,`currency`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order`
--

DROP TABLE IF EXISTS `sale_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order` (
  `_id` bigint(20) NOT NULL,
  `id2` varchar(45) DEFAULT NULL,
  `req_ctx_id` bigint(20) NOT NULL,
  `checkout_id` bigint(20) DEFAULT NULL,
  `customer_fk` bigint(20) DEFAULT NULL,
  `total_amount` decimal(12,4) NOT NULL,
  `coupon_id` bigint(20) DEFAULT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `calc_result` varchar(5000) DEFAULT NULL,
  `order_number` varchar(40) DEFAULT NULL,
  `operator` bigint(20) DEFAULT NULL,
  `archive` tinyint(1) DEFAULT '0',
  `archive_id` varchar(40) DEFAULT NULL,
  `club_member` tinyint(1) DEFAULT NULL,
  `external_number` varchar(40) DEFAULT NULL,
  `customer_id2` varchar(100) DEFAULT NULL,
  `base_currency` varchar(10) DEFAULT NULL,
  `view_currency` varchar(10) DEFAULT NULL,
  `payment_currency` varchar(10) DEFAULT NULL,
  `base_to_view_currency_ratio` decimal(12,4) DEFAULT NULL,
  `base_to_payment_currency_ratio` decimal(12,4) DEFAULT NULL,
  `note` varchar(2000) DEFAULT NULL,
  `note_internal` varchar(2000) DEFAULT NULL,
  `note_seller` varchar(2000) DEFAULT NULL,
  `order_status` int(11) DEFAULT NULL,
  `discount_code` varchar(40) DEFAULT NULL,
  `discount_amount` decimal(12,4) DEFAULT NULL,
  `discount_description` varchar(2000) DEFAULT NULL,
  `gift_description` varchar(2000) DEFAULT NULL,
  `canceled` tinyint(1) DEFAULT NULL,
  `lang` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_customer_id_idx` (`customer_fk`),
  KEY `archive_id_index` (`archive_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_address`
--

DROP TABLE IF EXISTS `sale_order_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_address` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `first_name` varchar(128) DEFAULT NULL,
  `last_name` varchar(128) DEFAULT NULL,
  `zip` varchar(45) DEFAULT NULL,
  `address1` varchar(128) DEFAULT NULL,
  `address2` varchar(128) DEFAULT NULL,
  `city` varchar(128) DEFAULT NULL,
  `country` varchar(2) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `house_number` varchar(45) DEFAULT NULL,
  `district` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `fax` varchar(45) DEFAULT NULL,
  `phone` varchar(45) DEFAULT NULL,
  `salutation` varchar(45) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `salut` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_address__order` (`order_fk`),
  CONSTRAINT `sale_order_address_ibfk_1` FOREIGN KEY (`order_fk`) REFERENCES `sale_order` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_company`
--

DROP TABLE IF EXISTS `sale_order_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_company` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `name` varchar(1000) DEFAULT NULL,
  `no` varchar(40) DEFAULT NULL,
  `tax` varchar(40) DEFAULT NULL,
  `ic_dph` varchar(40) DEFAULT NULL,
  `cr_on` datetime DEFAULT NULL,
  `mod_on` datetime DEFAULT NULL,
  `cr_by` varchar(40) DEFAULT NULL,
  `mod_by` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_item`
--

DROP TABLE IF EXISTS `sale_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_item` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `art_no` varchar(45) NOT NULL,
  `variant_options` varchar(255) DEFAULT NULL,
  `price` decimal(12,4) NOT NULL,
  `price_type_id` bigint(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `total_row_price` decimal(12,4) NOT NULL,
  `tax_rate` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_item__order` (`order_fk`),
  CONSTRAINT `sale_order_item_ibfk_1` FOREIGN KEY (`order_fk`) REFERENCES `sale_order` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_payment`
--

DROP TABLE IF EXISTS `sale_order_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_payment` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `payment_method_code` varchar(45) NOT NULL,
  `payment_status` int(11) DEFAULT NULL,
  `last_payment_status` int(11) DEFAULT NULL,
  `transaction_id` varchar(45) DEFAULT NULL,
  `authorization_id` varchar(45) DEFAULT NULL,
  `is_authorized` bit(1) DEFAULT NULL,
  `currency` char(3) NOT NULL,
  `paid_amount` decimal(12,4) DEFAULT NULL,
  `authorized_amount` decimal(12,4) DEFAULT NULL,
  `refunded_amount` decimal(12,4) DEFAULT NULL,
  `custom` text,
  `paid_on` datetime DEFAULT NULL,
  `authorized_on` datetime DEFAULT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `eshop_transaction_id` varchar(40) DEFAULT NULL,
  `archive_payment_status` varchar(40) DEFAULT NULL,
  `rate_amount` decimal(12,4) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_payment__order` (`order_fk`),
  CONSTRAINT `sale_order_payment_ibfk_1` FOREIGN KEY (`order_fk`) REFERENCES `sale_order` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_payment_event`
--

DROP TABLE IF EXISTS `sale_order_payment_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_payment_event` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `success_message` text,
  `error_message` text,
  `response_text` text,
  `request_text` text,
  `payment_status` int(11) DEFAULT NULL,
  `expected_payment_status` int(11) DEFAULT NULL,
  `cr_on` datetime DEFAULT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `operator` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_payment_event__order` (`order_fk`),
  CONSTRAINT `sale_order_payment_event_ibfk_1` FOREIGN KEY (`order_fk`) REFERENCES `sale_order` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_shipment`
--

DROP TABLE IF EXISTS `sale_order_shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_shipment` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) NOT NULL,
  `carrier` varchar(45) DEFAULT NULL,
  `tracker` varchar(45) DEFAULT NULL,
  `option` varchar(45) DEFAULT NULL,
  `tracking_number` varchar(45) DEFAULT NULL,
  `shipping_amount` decimal(12,4) DEFAULT NULL,
  `shipped_on` datetime DEFAULT NULL,
  `delivered_on` datetime DEFAULT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `free_shipping` tinyint(1) DEFAULT NULL,
  `free_shipping_descr` varchar(2000) DEFAULT NULL,
  `store_id` bigint(20) DEFAULT NULL,
  `delivery_price` decimal(12,4) DEFAULT NULL,
  `postage_and_packing_price` decimal(12,4) DEFAULT NULL,
  `option_name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `fk_sale_order_shipment__order` (`order_fk`),
  CONSTRAINT `sale_order_shipment_ibfk_1` FOREIGN KEY (`order_fk`) REFERENCES `sale_order` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_shipment_item`
--

DROP TABLE IF EXISTS `sale_order_shipment_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_shipment_item` (
  `_id` bigint(20) NOT NULL,
  `order_shipment_fk` bigint(20) NOT NULL,
  `order_item_fk` bigint(20) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  KEY `fk_sale_order_shipment_item__shipment` (`order_shipment_fk`),
  KEY `fk_sale_order_shipment_item__order_item` (`order_item_fk`),
  CONSTRAINT `sale_order_shipment_item_ibfk_1` FOREIGN KEY (`order_shipment_fk`) REFERENCES `sale_order_shipment` (`_id`) ON DELETE CASCADE,
  CONSTRAINT `sale_order_shipment_item_ibfk_2` FOREIGN KEY (`order_item_fk`) REFERENCES `sale_order_item` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_shipment_option`
--

DROP TABLE IF EXISTS `sale_order_shipment_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_shipment_option` (
  `_id` bigint(20) NOT NULL,
  `order_shipment_fk` bigint(20) NOT NULL,
  `cr_on` datetime NOT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mod_by` varchar(45) DEFAULT NULL,
  `amount` decimal(12,4) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `carrier` varchar(100) DEFAULT NULL,
  `option` varchar(100) DEFAULT NULL,
  KEY `fk_sale_order_shipment_item__shipment` (`order_shipment_fk`),
  CONSTRAINT `sale_order_shipment_option_ibfk_1` FOREIGN KEY (`order_shipment_fk`) REFERENCES `sale_order_shipment` (`_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sale_order_status_history`
--

DROP TABLE IF EXISTS `sale_order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sale_order_status_history` (
  `_id` bigint(20) NOT NULL,
  `order_fk` bigint(20) DEFAULT NULL,
  `order_status` int(11) DEFAULT NULL,
  `operator` bigint(20) DEFAULT NULL,
  `cr_on` datetime DEFAULT NULL,
  `mod_on` datetime DEFAULT NULL,
  `cr_by` varchar(45) DEFAULT NULL,
  `mod_by` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'scs_demo_local'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-21  0:30:13
