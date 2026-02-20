package com.udemy.argocd.myapp.api;

import com.udemy.argocd.myapp.service.UserService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.micrometer.PrometheusScrapingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CoreApi {
  //

  final UserService userService;

  public static void createRouters(Router router, UserService userService) {
    //
    log.info("Creating routers");
    var coreApi = new CoreApi(userService);
    //
    router.get("/my-app/metrics").handler(PrometheusScrapingHandler.create());
    //

    router.get("/my-app/user/greetings").handler(coreApi::userGreeting);
  }

  private void userGreeting(RoutingContext rCtx) {
    this.userService.userGreeting()
            .onSuccess(result -> {
              rCtx.response()
                      .setStatusCode(200)
                      .putHeader("content-type", "application/json")
                      .end(result.encode());
            }).onFailure(err -> handleError(rCtx, err));
  }

  private void handleError(RoutingContext ctx, Throwable err) {
    ctx.response()
            .setStatusCode(500)
            .end(new JsonObject().put("error", err.getMessage()).encode());
  }

}
