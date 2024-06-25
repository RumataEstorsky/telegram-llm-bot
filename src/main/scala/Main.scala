import akka.actor.ActorSystem
import io.cequence.openaiscala.service.{OpenAIChatCompletionService, OpenAIChatCompletionServiceFactory}
import scala.concurrent.ExecutionContext

implicit val system: ActorSystem = ActorSystem()
implicit val executionContext: ExecutionContext = system.dispatcher

val llmClient: OpenAIChatCompletionService = OpenAIChatCompletionServiceFactory(
  coreUrl = sys.env("LLM_URL"),
  authHeaders = Seq(("Authorization", s"Bearer ${sys.env("LLM_TOKEN")}"))
)

val bot = new SimpleBot(sys.env("TELEGRAM_BOT_TOKEN"), llmClient)


@main def hello(): Unit =
  bot.run()


