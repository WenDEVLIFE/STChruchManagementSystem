-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 29, 2025 at 04:31 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chruch_managementdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `christening_table`
--

CREATE TABLE `christening_table` (
  `reservation_id` varchar(255) NOT NULL,
  `child_name` varchar(255) NOT NULL,
  `parent_name` varchar(255) NOT NULL,
  `contact_number` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  `time_slot` varchar(255) NOT NULL,
  `user_id` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `christening_table`
--

INSERT INTO `christening_table` (`reservation_id`, `child_name`, `parent_name`, `contact_number`, `date`, `time_slot`, `user_id`) VALUES
('CHR000001', 'Doewew', 'Doedad', '0942323', '2025-04-30', '9:00 AM', 1),
('CHR000002', 'Doewew', 'DoeDad', '0433434', '2025-04-30', '9:00 AM', 1),
('CHR000003', 'DoeSon', 'DoeDad', '098423', '2025-04-30', '9:00 AM', 1),
('CHR000004', 'DoeSon', 'DoeDad', '094343', '2025-04-30', '09:00AM', 1);

-- --------------------------------------------------------

--
-- Table structure for table `funeral_table`
--

CREATE TABLE `funeral_table` (
  `reservation_id` varchar(255) NOT NULL,
  `user_id` int(255) NOT NULL,
  `deceased_name` varchar(255) NOT NULL,
  `family_rep_name` varchar(255) NOT NULL,
  `contact_number` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  `time_slot` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservationtable`
--

CREATE TABLE `reservationtable` (
  `reservation_id` varchar(255) NOT NULL,
  `event` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  `time` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `reason` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservationtable`
--

INSERT INTO `reservationtable` (`reservation_id`, `event`, `date`, `time`, `status`, `reason`) VALUES
('CHR000004', 'Christening', '2025-04-30', '09:00AM', 'Pending', 'n/a');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `middle_name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `contact_number` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`, `first_name`, `last_name`, `middle_name`, `address`, `contact_number`) VALUES
(1, 'DoeUser', 'doe', 'User', 'Doe', 'Doe', 'Doe', 'Davao', '094232'),
(2, 'Admin', 'admin', 'Admin', 'Admin', 'Admin', 'Admin', 'Manila Tondo', '0942323');

-- --------------------------------------------------------

--
-- Table structure for table `wedding_table`
--

CREATE TABLE `wedding_table` (
  `groom_name` varchar(255) NOT NULL,
  `bride_name` varchar(255) NOT NULL,
  `contact_number` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  `time_slot` varchar(255) NOT NULL,
  `reservation_id` varchar(255) NOT NULL,
  `user_id` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
