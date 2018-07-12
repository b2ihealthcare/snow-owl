# Software requirements

## Introduction

This document lists the required set of software components necessary to run Snow Owl on the x86_64 platform. Components and the corresponding files to download are listed in the sections below.

## Components

### Operating system

Snow Owl requires a  **CentOS**  or  **Red Hat Enterprise Linux Server (RHEL)**  release  **7**, or  **Ubuntu 14.04**  installation. We recommend starting with a minimal install and adding packages later, as required.

### Database

Terminology contents are persisted using the  **5.7**  series of  **MySQL Community Server**, downloadable from  [MySQL’s yum repository](http://dev.mysql.com/downloads/repo/yum/). Select “Red Hat Enterprise Linux / Oracle Linux 7 (Architecture independent), RPM package” for download.

### JDK

Snow Owl uses version  **8 update 121**  of the  **Java SE Development Kit**; An installable archive can be downloaded from the [JDK8 download page](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). Select the “Linux x64” edition.

### LDAP

Authentication and authorization of browsers, terminology editors, reviewers and administrators is performed through an **OpenLDAP**  version  **2.4.x**  LDAP server.

Browsing and managing ApacheDS instances can be done through the  **Apache Directory Studio**  application. We recommend installing the latest release from the corresponding  [Downloads](http://directory.apache.org/studio/)  page on Apache’s website.