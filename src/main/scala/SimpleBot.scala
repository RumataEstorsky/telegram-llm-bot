import cats.instances.future.*
import cats.syntax.functor.*
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.clients.FutureSttpClient
import com.bot4s.telegram.future.{Polling, TelegramBot}
import com.bot4s.telegram.methods.*
import com.bot4s.telegram.models.*
import io.cequence.openaiscala.domain.NonOpenAIModelId.llama3_70b_8192
import io.cequence.openaiscala.domain.{SystemMessage, UserMessage}
import io.cequence.openaiscala.domain.settings.CreateChatCompletionSettings
import io.cequence.openaiscala.service.OpenAIChatCompletionService
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

class SimpleBot(token: String, llmClient: OpenAIChatCompletionService)
  extends TelegramBot with Polling {
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
  override val client: RequestHandler[Future] = FutureSttpClient(token)

  override def receiveMessage(msg: Message): Future[Unit] =
    msg.text.fold(Future.successful(())) { text =>
      llmClient.createChatCompletion(Seq(
        SystemMessage("answer shortly"),
        UserMessage(text)
      ), CreateChatCompletionSettings(
        model = llama3_70b_8192,
        max_tokens = Some(1500),
        temperature = Some(0.8),
      )).flatMap { completion =>
        request(SendMessage(msg.source, completion.choices.head.message.content, parseMode = Some(ParseMode.Markdown))).void
      }.recover {
        case ex: Exception =>
          println(s"Failed to send message to LLM or to send response to user: ${ex.getMessage}")
          request(SendMessage(msg.source, "An error occurred while processing your request. Please try again later.")).void
      }
    }
}
