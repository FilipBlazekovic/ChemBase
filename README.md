# ChemBase
Simple database for small chemistry labs (web version)

![Screenshot](https://raw.github.com/wiki/FilipBlazekovic/ChemBase/Images/1.png)

![Screenshot](https://raw.github.com/wiki/FilipBlazekovic/ChemBase/Images/2.png)

![Screenshot](https://raw.github.com/wiki/FilipBlazekovic/ChemBase/Images/3.png)

![Screenshot](https://raw.github.com/wiki/FilipBlazekovic/ChemBase/Images/4.png)

![Screenshot](https://raw.github.com/wiki/FilipBlazekovic/ChemBase/Images/5.png)


**Setup instructions:**

Create the database (and optionally populate it with test values) by using the scripts in sql directory:

```
psql -U postgres -f db_create.sql
psql -U chembase_admin -d chembase -f db_init.sql
psql -U chembase_admin -d chembase -f db_populate.sql
```
Create the directories for xml configuration files, and copy the configuration files located in xml directory into them.
Default paths used for configuration files are:

```
/etc/development/chembase/ChemBase.xml
/etc/development/chembase/ChemBase-logback.xml
```

In case diferent paths are used set the following system properties:

```
ChemBaseConfigFile
ChemBaseLogbackFile
```

Default path for log files is:

```
/var/development/chembase/logs/
```

In case a different path should be used edit the `ChemBase-logback.xml` configuration file.
To set-up a different user for database connections edit `ChemBase.xml` configuration file.

Copy `ChemBase.war` located in target folder to the tomcat webapps folder, and restart tomcat if necessary.
Tested under Tomcat 8.5 on Ubuntu.
