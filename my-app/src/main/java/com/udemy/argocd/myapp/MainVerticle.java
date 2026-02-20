package com.udemy.argocd.myapp;

import com.udemy.argocd.myapp.api.CoreApi;
import com.udemy.argocd.myapp.config.AppConfig;
import com.udemy.argocd.myapp.service.UserService;
import io.vertx.core.*;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.healthchecks.HealthCheckHandler;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends VerticleBase {
  //
  public static void main(String[] args) {
    System.setProperty(
            "vertx.logger-delegate-factory-class-name",
            "io.vertx.core.logging.SLF4JLogDelegateFactory"
    );
    //

    var options = new VertxOptions()
            .setMetricsOptions(new MicrometerMetricsOptions()
                    .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true).setPublishQuantiles(true))
                    .setEnabled(true)
                    .setJvmMetricsEnabled(true));
    //
    Vertx.vertx(options).deployVerticle(MainVerticle.class.getName(), new DeploymentOptions()
                    // only JDK >= 21
                    .setThreadingModel(ThreadingModel.VIRTUAL_THREAD))
            .onFailure(Throwable::printStackTrace)
            .onSuccess(id -> log.info("Verticle deployed with id: {}", id));
    // -----------------------------------------------------------------------------------------------

  }

  @Override
  public Future<?> start() {

    return AppConfig.loadVertx(super.vertx)
            .compose(config -> {
              log.info("Configuration loaded");
              var router = Router.router(super.vertx);
              var hc = HealthChecks.create(vertx);
              hc.register("my-app", promise -> promise.complete(Status.OK()));
              router.route().handler(BodyHandler.create());
              router.get("/my-app/health").handler(HealthCheckHandler.createWithHealthChecks(hc));
              // -------------------------------------------------------------------
              CoreApi.createRouters(router, new UserService());
              // ---------------------------------------------------------------------------
              return super.vertx.createHttpServer().requestHandler(router)
                      .listen(config.getInteger("http.port", 8080))
                      //.<Void>mapEmpty()
                      .onSuccess(v -> log.info("HTTP server started on port: {} ", config.getInteger("http.port")));
              // -----------------------------------------------------------------------------
            })
            .onComplete(id -> log.info("Verticle started with id: {}", id))
            .onFailure(Throwable::printStackTrace);
  }
}
