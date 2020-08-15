package ru.dlobanov.service

import ru.dlobanov.api.greeter.{GreeterGrpc, HelloReply, HelloRequest}

import scala.concurrent.Future

class GreeterImpl extends GreeterGrpc.Greeter {
  override def sayHello(req: HelloRequest) = {
    val reply = HelloReply(message = s"Hello, ${req.name}!")
    Future.successful(reply)
  }
}
