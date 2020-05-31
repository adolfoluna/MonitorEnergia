# MonitorEnergia
Author Adolfo Luna

Project to monitor the availability of electricity service of ATM's at hard to reach remote rural areas.
This is done by a server connected to several modems boards https://www.mikroe.com/gsm-4-click that connects
to the cellphone network

The server manages all modems and programs the modems to read incoming text masseges with statuses
indicating if the electricity is running and if the ups backup battery is running.
When a message is received the server reads it trough AT commands and updates status on database

THIS PROJECT WAS DEVELOPED TO DO A FUNCTIONAL DEMONSTRATION FOR A PRIVATE COMPANY

Server components
 -Mysql database
 -Wildfly 18.0.0.Final
 -This project produces 2 ears, one for all AT commands and interactions with modems and one that interacs with
 database

every modem board is connected trough USB and the server opens up a serial port.

 File modelo.mwb can be opened with mysql workbench
 All other projects can be opened with Eclipse or CodereadyStudio 12 or older

