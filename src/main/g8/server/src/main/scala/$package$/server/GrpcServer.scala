package $package$.server

import com.typesafe.scalalogging.LazyLogging
import io.grpc.{Server, ServerBuilder, ServerInterceptors}
import $package$.api.greeter.GreeterGrpc

import java.util.concurrent.Executor
import scala.concurrent.ExecutionContext
import scala.language.existentials

class GrpcServer(service: GreeterGrpc.Greeter)(implicit ec: ExecutionContext with Executor) extends LazyLogging {

  private var server: Server = _

  def start(port: Int): GrpcServer = {
    val serverBuilder = ServerBuilder.forPort(port).executor(ec)
    val serviceDefinition = ServerInterceptors.intercept(
      GreeterGrpc.bindService(service, ec)
    )
    server = serverBuilder.addService(serviceDefinition).asInstanceOf[ServerBuilder[_]].build().start()
    logger.info(s"GRPC server started, listening on \$port.")

    sys.addShutdownHook {
      logger.info("Shutting down gRPC server since JVM is shutting down.")
      stop()
      logger.info("GRPC server shut down.")
    }
    this
  }

  def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }
}
