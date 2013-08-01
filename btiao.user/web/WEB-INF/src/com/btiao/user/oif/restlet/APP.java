package com.btiao.user.oif.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.btiao.base.oif.restlet.ResBTBase;

public class APP extends Application {
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/{userId}", ResBTBase.class);
		return router;
	}
}
