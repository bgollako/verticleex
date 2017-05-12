package com.library.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class DBWriteVerticle extends AbstractVerticle {
	@Override
	public void start(Future<Void> future) throws Exception {
		vertx.eventBus().consumer("com.library.book.add", message -> {
			String isbn=message.body().toString();
			System.out.println(isbn);
			JsonObject config = new JsonObject();
			config.put("db_name", "cmad");
			config.put("connection_string", "mongodb://mongo:27017");
			MongoClient client = MongoClient.createShared(vertx, config);
			JsonObject object=new JsonObject(message.body().toString());
			client.insert("books", object, res -> {
				if (res.succeeded()) {
					message.reply("Book inserted with id " + res.result());
				}
				else {
					message.fail(500, "Insertion failed");
				}
			});
		});
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}
}