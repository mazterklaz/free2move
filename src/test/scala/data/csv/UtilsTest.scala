package data.csv

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.sql.Timestamp
import java.text.SimpleDateFormat

class UtilsTest extends AnyFlatSpec {

  "Given id with quotes, calling removeQuotes" should "remove quotes from string" in {
    val ExpectedId = "47770eb9100c2d0c44946d9cf07ec65d"

    val cleanId = Utils.removeQuotes("\"47770eb9100c2d0c44946d9cf07ec65d\"")

    cleanId shouldEqual ExpectedId
  }

  "Given valid string timestamp, calling stringToTimestamp" should "create Timestamp" in {
    val ExpectedStr = "2018-08-08 08:38:49"

    val ts: Timestamp = Utils.stringToTimestamp(ExpectedStr)

    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    format.format(ts) shouldEqual ExpectedStr
  }

}
