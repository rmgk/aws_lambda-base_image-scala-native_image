package lambdatest

import scribe.Logger

import java.net.URI
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import scala.util.Random

object Main {

  def main(args: Array[String]): Unit = {
    val apiurl = System.getenv("AWS_LAMBDA_RUNTIME_API")
    val client = HttpClient.newHttpClient()

    val vmID = Random.nextLong()

    import scribe.format._

    val normalFormatter: Formatter =
      formatter"${string(vmID.toString)} $time:$message$mdc [${className.abbreviate(maxLength = 15, padded = false)}]"
    Logger.root.clearHandlers().withHandler(
      formatter = normalFormatter,
      minimumLevel = Some(scribe.Level.Info)
    ).replace()

    scribe.info(s"started")

    while (true) {
      scribe.info("accepting request")
      val req = HttpRequest.newBuilder().uri(
        URI.create(s"http://${apiurl}/2018-06-01/runtime/invocation/next")
      ).build()

      val res       = client.send(req, BodyHandlers.ofString())
      val reqId     = res.headers().firstValue("Lambda-Runtime-Aws-Request-Id").get()
      val eventData = res.body()

      scribe.info(s"sending response for $reqId")
      val req2 = HttpRequest.newBuilder().uri(
        URI.create(s"http://${apiurl}/2018-06-01/runtime/invocation/$reqId/response")
      ).method("POST", BodyPublishers.ofString(eventData.reverse)).build()

      client.send(req2, BodyHandlers.discarding())
    }
  }

}
