package data.assess
import org.apache.spark.{SparkConf, SparkContext}

object StatService{

	case class Service(
		keyword: String, 
		market: String, 
		location: String, 
		device: String, 
		crawldate: String, 
		rank: String, 
		url: String)

	def main(args:Array[String]){

		val conf = new SparkConf().setAppName("StatService")
		val sc = new SparkContext(conf)
		val sqlContext = new org.apache.spark.sql.SQLContext(sc)

		import sqlContext.implicits._

		//Loading data
		val statFile = sc.textFile("/tmp/stat/getstat_com_serp_report_201707.csv")
		
		//Filtering header			
		val header = statFile.first()
		val statData = statFile.filter(s => s != header)
		
		//Creating temp table
		val sData = statData.map(_.split(",")).map(s => Service(s(0),s(1),s(2),s(3),s(4),s(5),s(6)))
		val statService = sData.toDF()
		statService.registerTempTable("StatData")

		//Which​ ​URL​ ​has​ ​the​ ​most​ ​ranks​ ​below​ ​10​ ​across​ ​all​ ​keywords​ ​over​ ​the​ ​period?
		val mostRanks = sqlContext.sql("SELECT url, count(url) as most FROM StatData WHERE rank <= 10 GROUP BY url ORDER BY most DESC")
		val mostRanksRDD = mostRanks.rdd		
		mostRanksRDD.saveAsTextFile("/tmp/stat/MostRanks")			
		
		//keywords​ ​where​ ​the​ ​rank​ ​1​ ​URL​ ​changes​ ​the​ ​most​ ​over the​ ​period.​ A ​given​ ​keyword's​ ​rank​ ​1​ ​URL​ ​is different​ ​from​ ​the​ ​previous​ ​day's​ ​URL
		val keywordUrl = sqlContext.sql("SELECT distinct(keyword), url FROM StatData WHERE rank = 1 GROUP BY keyword, url ORDER BY keyword, url")
		val keywordChangePair = keywordUrl.map(k => (k(0), 1)).reduceByKey(_ + _)
		keywordChangePair.saveAsTextFile("/tmp/stat/KeywordChanges")

		//For​ ​keywords,​ ​markets,​ ​and​ ​locations​ ​that​ ​have​ ​data​ ​for both​ ​desktop​ ​and​ ​smartphone​ ​devices,​ ​devise​ ​a​ ​measure​ ​of​ ​difference​ ​to​ ​indicate​ ​how
		//similar​ ​these​ ​datasets​ ​are,​ ​and​ ​please​ ​use​ ​this​ ​to​ ​show​ ​how​ ​the​ ​mobile​ ​and​ ​desktop​ ​results​ ​in​ ​our provided​ ​data​ ​set​ ​converge​ ​or​ ​diverge​ ​over​ ​time
		val bothDevices = sqlContext.sql("SELECT keyword, market, location, device FROM StatData WHERE device = 'smartphone' GROUP BY keyword, market, location, device UNION ALL SELECT keyword, market, location, device FROM StatData WHERE device = 'desktop' GROUP BY keyword, market, location, device")	
		val bothDevicesRDD = bothDevices.rdd		
		bothDevicesRDD.saveAsTextFile("/tmp/stat/deviceDifference")
	}
}
