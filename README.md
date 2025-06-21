## Victoria University Exhibition Registration System (SQLite)

### How To Run The Program
##### Install the SQLite DB Driver
###### For (Ubuntu)
`wget https://github.com/xerial/sqlite-jdbc/releases/download/3.36.0.3/sqlite-jdbc-3.36.0.3.jar
`

##### Compile the Code Along With The SQLite DB Driver
`❯ javac -cp sqlite-jdbc-3.36.0.3.jar src/main/java/Main.java src/main/java/ExhibitionRegistrationSystem.java
`

##### Run the main file along with the driver
`❯ java -cp src/main/java:sqlite-jdbc-3.36.0.3.jar Main
`