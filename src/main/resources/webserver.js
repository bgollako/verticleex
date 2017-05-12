var Router = require("vertx-web-js/router");
var StaticHandler = require("vertx-web-js/static_handler");
var BodyHandler = require("vertx-web-js/body_handler");
vertx.deployVerticle("com.library.verticle.DBReadVerticle");
vertx.deployVerticle("com.library.verticle.DBWriteVerticle");
var router = Router.router(vertx);
router.route("/library/book").handler(BodyHandler.create().handle);
router.get("/library/book/:isbn").handler(
		function(rctx) {
			vertx.eventBus().send("com.library.book.read",rctx.request().getParam("isbn"),function(reply, err) {
						rctx.response().setStatusCode(200).putHeader("Content-Type", "application/plain").end(reply.body());
			});
		});
router.post("/library/book/").handler(
		function(rctx) {
			vertx.eventBus().send("com.library.book.add",rctx.getBodyAsJson(),function(reply, err) {
						rctx.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(reply.body());
			});
		});
var server = vertx.createHttpServer();
server.requestHandler(router.accept).listen(8090);
