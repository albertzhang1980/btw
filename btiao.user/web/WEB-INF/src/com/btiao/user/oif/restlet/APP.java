package com.btiao.user.oif.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class APP extends Application {
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/users/{userId}", ResBTUser.class);
		return router;
	}
}
