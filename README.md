#  SDG 6: Clean Water and Sanitation  
### Empowering Communities Through Smart Water Monitoring

---

##  Project Overview

The app acts as an **early-warning and data-collection tool** that contributes to improving water quality.  
It empowers individuals or community groups to monitor local water bodies (rivers, lakes, streams) in real time.  
Users can detect sudden drops in quality, which may indicate a pollution event.  
The app can log this data with a timestamp and (optionally) a GPS location, creating a record that can be reported to local authorities or environmental organizations.  

This directly supports the **“reducing pollution”** and **“minimizing release of hazardous chemicals”** targets under **SDG 6.3**.

---

##  Goal

The goal is to improve water quality by reducing pollution, eliminating dumping and minimizing release of hazardous chemicals and materials, halving the proportion of untreated wastewater and substantially increasing recycling and safe reuse globally. 
---

##  System Requirements

**At least 5 modules** and **3 functional requirements** are required.  
Include **2 non-functional requirements** with justifications.

---

##  Module 1: User Authentication and Profile Management 

**Functional Requirements:**
1. The user shall be able to register an account using an email address and password. 

2. The user shall be able to log in and log out of the application securely. 

3. The user shall be able to view and update personal details such as name, and  	ccontact.   

---

##  Module 2:Bluetooth Data Collection 
**Functional Requirements:**
1. The user shall be able to pair and connect the app with the water monitoring device using Bluetooth. 

2. The app shall automatically receive and display sensor readings such as pH level, and temperature in real time. 

3. The app shall notify the user if the Bluetooth connection is lost or if data transmission fails. 

---

##  Module 3:Monitoring Dashboard 

**Functional Requirements:**
1. The app shall visualize current and past water quality data through charts and color-coded indicators. 

2. The app shall allow users to view detailed statistics for each recorded session, including timestamp and location. 

3. The app shall classify water quality status (e.g., Safe, Moderate, Polluted) based on predefined thresholds. 

---

##  Module 4: Alert and Reporting System

**Functional Requirements:**
1. The app shall generate an alert when water quality parameters exceed safe levels. 

2. The user shall be able to submit a pollution report with description and optional photos. 

3. The app shall store all reports in the database and allow viewing of report history. 

---

##  Module 5:Real-Time Chat & Community Platform

**Functional Requirements:**
1. Users from different locations shall be able to chat or post updates about local water quality in real time, like a social feed. 

2. Users shall be able to attach photos, short text, or water sensor readings in their posts to report local conditions. 

3. The app shall allow experts or authorized users to reply to community posts and provide verified guidance or recommendations. 

---

##  Non-Functional Requirements

###  Performance
The system shall display updated water quality readings within **__ seconds** after receiving data from the Bluetooth device.

###  Reliability / Security
The app shall ensure that user data and GPS location are securely stored and transmitted using encryption methods to protect user privacy.

---

 *This document outlines the functional and non-functional requirements for the Smart Water Monitoring App aligned with SDG 6: Clean Water and Sanitation.*


