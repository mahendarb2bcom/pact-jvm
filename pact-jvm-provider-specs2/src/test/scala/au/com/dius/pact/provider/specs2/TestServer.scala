package au.com.dius.pact.provider.specs2

import unfiltered.netty.{ServerErrorResponse, Server}
import unfiltered.netty.cycle.{SynchronousExecution, Plan}
import unfiltered.response.ResponseString
import au.com.dius.pact.model.{PactSpecVersion, PactConfig, MockProviderConfig}


/**
 * This is not really part of the example, it's just a fake server instead of building a real provider
 */
case class TestServer(state: String) {
  def run[T](code: String => T):T = {
    val config = MockProviderConfig.createDefault(PactConfig(PactSpecVersion.V2))
    val server = Server.http(config.port, config.hostname).handler(new Plan with SynchronousExecution with ServerErrorResponse {
      def intent: Plan.Intent = {
        case req => {
          ResponseString("[\"All Done\"]")
        }
      }
    })

    server.start()
    try {
      code(config.url)
    } finally {
      server.stop()
    }
  }
}
