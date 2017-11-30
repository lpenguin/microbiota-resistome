# Microbiome resistome model
...

## Dependencies
* Java >= 1.7
* Maven
* R - for reports

## Building
* Jar
```
mvn clean compile assembly:single
```
Output will be in *target* folder

* Package
```
./build.sh
```
Output will be in *builds* folder

## Running model
```
./runVERA.sh -t <ticks> -o <outputDir> -c <configPropertiesFile> [-q]
```
Example:
```
./runVERA.sh -t 200 -o run1 -c config.properties
```

