#COMP 3005 B - Final Project

## Author

Brianna Pratt      
101254262

## Source Files
project-root/      
    docs/      
        README.md     
    sql/     
        DDL.sql     
        DML.sql     
    app/      
        PostgreSQLJDBCConnection.java        
        postgresql-42.7.8.jar    <-JDBC driver downloaded from:https://jdbc.postgresql.org/download           
 

Functions used in PostgreSQLJDBCConnection.java:      
mainMenu();     
Member functions:      
    memberMenu();      
    registerMember();      
    updateMemberProfile();     
    addHealthMetric();      
    viewMemberDashboard();      
Trainer functions:      
    trainerMenu();        
    setTrainerAvailability();     
    viewTrainerSchedule();      
Admin functions:      
    adminMenu();     
    bookPtSession();     
    viewRoomSchedule();     

## Video Link
https://youtu.be/dqH1dOX6Ck8       

## Launching and Installiation Instructions     

Creating Database:       
Copy and paste code from DDL.sql and then from DML.sql      

Compile and Execute:       
javac PostgreSQLJDBCConnection.java           
java -cp .:postgresql-42.7.8.jar PostgreSQLJDBCConnection       
