package com.udemy.argocd.myapp.service;

import com.udemy.argocd.myapp.model.Greeting;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
@NoArgsConstructor
public class UserService {

  @SneakyThrows
  public Future<JsonObject> userGreeting() {
    final var greeting = new Greeting("Hello User, Vert.x 5.x and ArgoCD at ".concat(InetAddress.getLocalHost().getHostName()));
    final var json = new JsonObject().put("message", greeting.message());
    return Future.succeededFuture(json);
  }

}
