# Agent-based simulation model VERA

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**VERA** is an agent model for simulation a spread of infection considering transmission of antibiotic resistance between human pathogens and gut microbiota. Algorithm of VERA model is following (Fig.1). 
 
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Step 1.** Input values of model parameters in config properties file or use default properties for *Shigella sp*. **Step 2.** Model VERA running. There is simulation of agent transmission between states. As a result we can get 1. an abundance table with agents number in each states and values main model indicators during simulation time, and 2. table with transmissions of each agent between states. **Step 3.** It's optional feature. At this point dynamic visualization of simulation processes, statistical analysis are available. As advanced feature we offer to estimate an optimal observation intensity. 
<img align="center" src="https://github.com/lpenguin/microbiota-resistome/blob/master/pictures/pipline.png" alt="Developed using Browsersyncrrrrrrrr" title="Browsersyncrrrrrrr" hspace="20"/>


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

## Model running

You can run model one time or if you need you can run multiply times with equal parameters set values. At fist step, you should fill up file with config properties. You can use default config properties for *Shigella sp.* (microbiota-resistome/resources/config.properties).

### Simple running

To run model from command line use the following syntax:
```
./runVERA.sh -t <ticks> -o <outputDir> -c <configPropertiesFile> [-q]
```
Argument description
* -t run time, model observation time, model time ( in article we use "tiks");
* -o output directory, which will contain running results with agent number in each state (abundLog) and agents transition between states (transLog).
* -c file with input parameters, config properties.
* -q optional feature when you don't want to call up model states; but it's possible don't use it.

Example:
```
./runVERA.sh -t 100 -o ../out/simulations/ -c ../resources/config.properties
```

### Multiple running

For model multiple running from command line use the following syntax:
```
./multiVERA.sh  -i <iterations> -t <ticks> -o <outputDir> -c <configPropertiesFile>
```
Argument description
* -i run number, how much you want to run model.
* -t run time, model observation time, model time ( in article we use "tiks").
* -o output directory, which will contain running results with agent number in each state (abundTables) and agents transition between states (transTables).
* -c file with input parameters, config properties.

Example:
```
./multiVERA.sh -t 100 -o ../out/simulations/multipleRuns -i 50 -c ../resources/config.properties
```
## Results visualisation
It's additional feature, you can display results in plots as pdf-report or dynamically to watch model states varying in time.

### Pdf - report
For this task you need set up R language, to run model and then run report generating from command line in such manner:

```
./reportRUN.sh  -i <inputDir> -t <title> -c <configPropertiesFile>
```
Argument description
* -i input directory,  which will contain running results with agent number in each state (abundLog) and agents transition between states (transLog).
* -t name of pathogen 
* -c file with input parameters, config properties.

As a result report will be in input diredtory in new folder /inputDir/plots.

Example:
```
./reportRUN.sh -i ../out/simulations/ -t Shigella -c ../resources/config.properties
```
### Dynamic report
Set up [VERA viewer](https://github.com/lpenguin/microbiota-resistome-viewer) from GitHub repository.

## Estimation of optimal observation intensity
It's additional feature too. We prepare estimation approach, which can help to bound optimal intensity of control of pathogen spread to avoid epidemic situation. To build estimation you need set up R language, to make multiple model running and after that execute script from command line:
```
./estimationReportRUN.sh -i <inputDir> -c <configPropertiesFile>
```
Argument description
* -i input directory,  which will contain running results with agent number in each state (abundLog) and agents transition between states (transLog).
* -c file with input parameters, config properties.

As a result report will be in input diredtory in new folder /inputDir/OOIestimation.

Example:
```
./multiVERA.sh -t 100 -o ../out/simulations/estimation/ -i 1000 -c ../resources/config.properties

./estimationReportRUN.sh -i ../out/simulations/estimation/ -c ../resources/config.properties
```
