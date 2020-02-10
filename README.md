# Performance Modeling of Computer Systems and Networks - A.A. 2018/2019



### Table of contents
1. [Introduction](#introduction)
2. [Installation](#installation)
    1. [Optional](#installation-optional)
3. [Usage](#usage)



## 1. Introduction <a name="introduction"></a>
This is a project for the course of Performance Modeling of Computer Systems and Networks (A.A. 2018/2019) 
at the university of Rome Tor Vergata.

Project's requirements are in `doc/` folder.



## 2. Installation <a name="installation"></a>
Compile source:
```bash
cd /project_root/
mvn clean
mvn package
```

Executable file can be found in `target/next-event-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### 2.1 Optional <a name="installation-optional"></a>
Debug information can be enabled, before build process, editing file `src/main/resources/log4j2.xml`: just change 
`<AppenderRef level="INFO" />` for desired `<Logger></Logger>`.



## 3. Usage <a name="usage"></a>
Usage:
```bash
# Execute jar file
java -jar target/next-event-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar

# Execute jar file with configuration file
java -jar target/next-event-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar custom_config.properties
```

Configuration file example:
```bash
# Max number of server on cloudlet
#N = 20
N = 3
# Cloudlet threshold for trigger class 2 tasks on cloud
#S = 20
S = 2



# Tasks class 1 arrival parameter for exponential distribution
lambda-1 = 6.0
# Tasks class 2 arrival parameter for exponential distribution
lambda-2 = 6.25



# Service time class 1 parameter for exponential distribution on cloudlet
mu-1-cloudlet = 0.45
# Service time class 2 parameter for exponential distribution on cloudlet
mu-2-cloudlet = 0.27
# Service time class 1 parameter for exponential distribution on cloud
mu-1-cloud = 0.25
# Service time class 2 parameter for exponential distribution on cloud
mu-2-cloud = 0.22
# Mean setup time for exponential distribution for cloud to manage interrupted tasks
mean-setup = 0.8



# Start simulation time
start = 0.0
# Stop simulation time
stop = 10000.0


# Cloudlet access control algorithm
#cloudlet-ac = Algorithm1
cloudlet-ac = Algorithm2
# Algorithm for selecting class 2 task to send on cloud once
#   threshold S is reached (only if cloudlet-ac = Algorithm2)
#cloudlet-ti = Min_Arrival_Time
#cloudlet-ti = Max_Arrival_Time
cloudlet-ti = Min_Completion_Time
#cloudlet-ti = Max_Completion_Time



# Seed for random number generator
rngs-seed = 123456789
#rngs-seed = 385362677



# Disable statistics computation using batch means method
#batch-means = false
# Batch size
batch-size = 1000
# Level of confidence
loc = 0.95
```
