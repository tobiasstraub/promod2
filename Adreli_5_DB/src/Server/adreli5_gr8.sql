-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `adreli`
--
CREATE DATABASE `adreli` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `adreli`;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `adreli5_gr8`
--

CREATE TABLE IF NOT EXISTS `adreli5_gr8` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vorname` varchar(32) NOT NULL,
  `nachname` varchar(32) NOT NULL,
  `anrede` varchar(4) NOT NULL,
  `strasse` varchar(32) NOT NULL,
  `postleitzahl` varchar(32) NOT NULL,
  `ort` varchar(32) NOT NULL,
  `telefon` varchar(32) NOT NULL,
  `fax` varchar(32) NOT NULL,
  `bemerkung` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Daten für Tabelle `adreli5_gr8`
--

INSERT INTO `adreli5_gr8` (`id`, `vorname`, `nachname`, `anrede`, `strasse`, `postleitzahl`, `ort`, `telefon`, `fax`, `bemerkung`) VALUES
(1, 'Hans', 'Xong', 'Herr', 'Gelber Weg', 'D-12345', 'Fudbud', '012345', '012345', 'Hallo'),
(2, 'Karl', 'Xing', 'Herr', 'Bubbleweg 4', 'D-35353', 'Lidad', '0134897', '0123897', 'Servus'),
(3, 'Max', 'Mustermann', 'Herr', 'Musterstrasse', 'D-84579', 'Musterstadt', '0123456', '0123456', 'Was geht'),
(4, 'Eric', 'Cartmann', 'Herr', 'Southpark 4', 'D-45612', 'Southpark', '0123456', '0123456', 'Cartman :-)'),
(5, 'Jessica', 'Burger', 'Frau', 'Pinker Weg', 'D-45612', 'Wunderstadt', '012398', '013689', '----'),
(6, 'Jasmin', 'Ruf', 'Frau', 'Stadtwald 4', 'D-79215', 'Elzach', '012365', '012365', '---'),
(7, 'Alfred', 'Ruf', 'Herr', 'Illenberg 5', 'D-79215', 'Biederbach', '0147852', '0147852', 'Nachbar'),
(8, 'Herbert', 'Braun', 'Herr', 'Brauner Weg 9', 'D-78945', 'Freiburg', '0123654', '0123654', 'Lehrer'),
(9, 'Lena', 'Zeng', 'Frau', 'Am Berg 6', 'D-89632', 'Emmendingen', '0112364', '0112365', '---'),
(10, 'Sebastian', 'Steiert', 'Herr', 'Industriepark 7', 'D-62149', 'Bahlingen', '0123698', '0123698', '---'),
(11, 'Maximilian', 'Brugger', 'Herr', 'Dorf 9', 'D-79215', 'Biederbach', '0123654', '0123654', 'Musikverein'),
(12, 'Arnold', 'Schwarzer', 'Herr', 'Hockenheimring 12', 'D-57321', 'Hockenheim', '01236987', '01236987', '---');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
