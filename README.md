# StatProject
# Data Assessment Project

## Pre-requisites

 * OS: Linux
 * Spark version min 1.6.0 (if greater must edit it in /StatProject/build.sbt)
 * Scala version min 2.10.5 (if greater must edit it in /StatProject/build.sbt)
 * Sbt version 1.0.2 (if greater must edit it in /StatProject/project/build.properties)
 * **Obs:** If any version is altered a recompile must be done using "sbt package" inside StatProject folder
 
## Installing

1. Clone this repository using "git clone <repo-url>"
1. Download and extract the csv file
1.  Create the following HDFS directory:
	* hadoop fs -mkdir /tmp/stat 
1. Copy the csv data file into the previous directory:
	* hadoop fs -put <local_dir> /tmp/stat
1. Run project with following command:
	* spark-submit --master <Node URL> --class <workspace>/StatProject/src/main/scala/data/assess/StatService target/scala-<version>/data-assessment_<scala-version>-1.0.jar

## Outputs
	
The output will be placed inside 3 following HDFS folders:
	* /tmp/stat/KeywordChanges
	* /tmp/stat/MostRanks
	* /tmp/stat/deviceDifference

## Acknowledgments
	
For the purpose of this project I have adopted an in memory approach to load data considering the sample dataset is not very large. 
In a productive environment I would choose an online distributed database such as DynamoDB to avoid the need to download large datasets. In such structure all data would be analised and stored in the cloud.
 
