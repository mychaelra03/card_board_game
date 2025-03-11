# Galaxy Truckers


<p align="center">
<img src=".github/resources/galaxy_trucker_box.png" width="400" alt="galaxy trucker logo"/>
</p>

<div align="center">
    <a href="https://github.com/Garolini/IS25-LL14/releases/latest">
        <img src="https://img.shields.io/badge/macOS-333333?style=for-the-badge&logo=apple&logoColor=white" />
    </a>
    <a href="https://github.com/Garolini/IS25-LL14/releases/latest">
        <img src="https://img.shields.io/badge/Linux-333333?style=for-the-badge&logo=linux&logoColor=white" />
    </a>
   <a href="https://github.com/Garolini/IS25-LL14/releases/latest">
        <img src="https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white" />
    </a>
</div>

## Project Overview

This project is a Java-based implementation of the table game "**Galaxy Truckers**", developed as the Final Project for
the 2024/2025 Software Engineering course at Politecnico di Milano. The team comprises Garolini Michele, Jafari Michael
and Migliaccio Cesario.

The implementation features both a command-line interface (CLI) and a JavaFX graphical user interface.

<!--Check out our project presentation [here.](https://garolini.github.io/)-->

## Status of the work

| Functionality     | Status                  | 
|-------------------|-------------------------|
| Basic rules       | :construction:      |
| Complete rules    | :construction:      |
| Socket connection | :construction:      |
| RMI connection    | :construction:      |
| CLI               | :construction:      |
| GUI               | :construction:      |
| Multiple games    | :construction:      |
| Persistence (*)   | :construction:      |
| Resilience        | :construction:      |
| Chat              | :construction:      |

(*) *For testing purposes.*

**Legend**

| Symbol | Functionality status |
| --- | --- |
| :white_check_mark: | Completed |
| :ballot_box_with_check: | Planned, not started yet |
| :construction: | Work in progress |
| :x: | Not planned to be implemented |

### Game Description

"Galaxy Trucker" is a fast-paced board game by Vlaada Chvátil where players build and pilot spacecraft to deliver goods
through space hazards. The game features a real-time shipbuilding phase and exciting space travel challenges.
Learn more about the game [here](https://www.craniocreations.it/prodotto/galaxy-trucker).
Rules of the game [here](https://www.craniocreations.it/storage/media/product_downloads/179/2052/Galaxy-Trucker_ITA_Rules_compressed.pdf).
<!--
## How to Compile with Maven

To compile the project using Maven, follow these steps:

1. Ensure you have Maven installed. You can download it
   from [Maven's official website](https://maven.apache.org/download.cgi).
2. Navigate to the root directory of the project where the `pom.xml` file is located.
3. Run the following command to compile the project and package it into a JAR file:

   ```bash
   mvn clean package
   ```

This command will generate two jars: one for the client and one for the server in the `target/jars` folder

## How to Run the Program

You can run the program using the JAR files found in the release section of the repository. Download the JAR files for
the server and client from the [releases](https://github.com/PiervitoCreanza/IS24-AM02/releases) page.

### Running the Server

To run the server, use the following command:

```bash
java -jar server.jar [options]
```

### Running the Client

To run the client, use the following command:

```bash
java -jar client.jar [options]
```

## Server CLI Arguments

| Argument        | Description                                     | Default Value |
|-----------------|-------------------------------------------------|---------------|
| -tp, --tcp_port | TCP ServerApp Port number (default is 12345).   | 12345         |
| -rp, --rmi_port | RMI ServerApp Port number (default is 1099).    | 1099          |
| -ip             | Start the server with the specified IP address. |               |
| -l, --localhost | Start the server with his localhost IP address. |               |
| --lan           | Start the server with his LAN IP address.       |               |
| --debug         | Start the Server in DEBUG mode.                 |               |
| -h, --help      | Print the help message.                         |               |

## Client CLI Arguments

| Argument           | Description                                                     | Default Value |
|--------------------|-----------------------------------------------------------------|---------------|
| --l                | Start the client using a localhost connection.                  |               |
| --rmi, --rmi_mode  | Start the client using an RMI connection.                       |               |
| -s, --server_ip    | Server IP address.                                              | 161.35.162.219|
| -ip, --client_ip   | Client IP address.                                              |               |
| -sp, --server_port | Server port number (default is 12345 for TCP and 1099 for RMI). |               |
| -cp                | Client port number (default is server port number + 1).         |               |
| --lan              | Start the client in LAN mode.                                   |               |
| --tui, --tui_mode  | Start the client in TUI mode.                                   |               |
| --debug            | Start the client in debug mode.                                 |               |
| -h, --help         | Print the help message.                                         |               |

-->
## Future Scope



## Legal 🇬🇧

Galaxy Truckers is a board game developed and published by Cranio Creations S.R.L.
The graphic content of this project attributable to the tabletop publishing product is used with prior
approval of Cranio Creations S.R.L. for educational purposes only. It is prohibited to distribute, copy or reproduce
of the contents and images in any form outside the project, as well as the redistribution and publication
of the contents and images for purposes other than the aforementioned. Commercial use of said content is also prohibited.

## Legal 🇮🇹

Galaxy Trucker è un gioco da tavolo sviluppato ed edito da Cranio Creations S.R.L.
I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa
approvazione di Cranio Creations S.R.L. a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione
dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione
dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti
contenuti.