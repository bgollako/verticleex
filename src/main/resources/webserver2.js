var Router = require("vertx-web-js/router");
var StaticHandler = require("vertx-web-js/static_handler");
var BodyHandler = require("vertx-web-js/body_handler");
vertx.deployVerticle("worker.js");
var router = Router.router(vertx);
router.route("/static/*").handler(
		StaticHandler.create().setWebRoot("web").handle);
router.route("/bank/compute").handler(BodyHandler.create().handle);
router.post("/bank/compute").handler(
		function(rctx) {
			vertx.eventBus().send(
					"com.glarimy.vertx.bank",
					rctx.getBodyAsJson(),
					function(reply, err) {
						rctx.response().setStatusCode(200).putHeader(
								"Content-Type", "application/json").end(
								reply.body());
					});

		});
var server = vertx.createHttpServer();
server.requestHandler(router.accept).listen(8080);