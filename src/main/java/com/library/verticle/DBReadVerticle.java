package com.library.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class DBReadVerticle extends AbstractVerticle {
	@Override
	public void start(Future<Void> future) throws Exception {
		vertx.eventBus().consumer("com.library.book.read", message -> {
			String isbn=message.body().toString();
			System.out.println(isbn);
			JsonObject config = new JsonObject();
			config.put("db_name", "cmad");
			config.put("connection_string", "mongodb://localhost:27017");
			MongoClient client = MongoClient.createShared(vertx, config);
			client.find("books", new JsonObject().put("isbn", Integer.parseInt(isbn)), res -> {
				if (res.succeeded()) {
					if (res.result().size() == 0)
						message.fail(404, "Book not found");
					else {
						JsonObject book = res.result().get(0);
						String json = Json.encodePrettily(book);
						System.out.println("BHAGI " + json);
						message.reply(json);
					}
				} else {
					res.cause().printStackTrace();
					message.fail(500, "Book not found");
				}
			});
		});
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}
}