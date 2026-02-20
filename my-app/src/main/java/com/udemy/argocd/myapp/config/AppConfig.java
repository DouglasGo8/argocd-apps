package com.udemy.argocd.myapp.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class AppConfig {

  private static JsonObject config;

  public static Future<JsonObject> loadVertx(Vertx vertx) {
    log.info("Loading configurations frm file");

    if (config != null) {
      return Future.succeededFuture(config);
    }

    // Configuration stores
    var envStore = new ConfigStoreOptions()
            .setType("env")
            .setConfig(new JsonObject().put("raw-data", true));

    var fileStore = new ConfigStoreOptions()
            .setType("file")
            .setFormat("json")
            .setConfig(new JsonObject().put("path", "application.json"));

    var options = new ConfigRetrieverOptions()
            .addStore(fileStore)
            .addStore(envStore);

    return ConfigRetriever.create(vertx, options)
            .getConfig()
            .onSuccess(cfg -> config = cfg)
            .onFailure(Throwable::printStackTrace);
  }

  public static JsonObject get() {
    if (config == null) {
      throw new IllegalStateException("Configuration not loaded");
    }
    return config;
  }
}
