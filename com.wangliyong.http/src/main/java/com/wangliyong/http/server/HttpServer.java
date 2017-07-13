package com.wangliyong.http.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
public class HttpServer {
    
	private static final Log log = LogFactory.getLog(HttpServer.class);
	private static final ServerBootstrap bootstrap = new ServerBootstrap();
	
	public static final ChannelGroup allChannels = new DefaultChannelGroup("HttpFileServer");
	
	public void start() throws Exception {
		log.info("======================staring fileserver=======================");
		log.info("staring netty httpserver...");
		
		bootstrap.setFactory(new NioServerSocketChannelFactory(
								Executors.newCachedThreadPool(),
								Executors.newCachedThreadPool(),
								4));
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory());
		bootstrap.setOption("child.tcpNoDelay", Boolean.valueOf(true));
		bootstrap.setOption("child.receiveBufferSize", Integer.valueOf(2097152));
		bootstrap.setOption("child.sendBufferSize", Integer.valueOf(2097152));
		bootstrap.setOption("child.writeTimeoutMillis", Integer.valueOf(2000));
		bootstrap.bind(new InetSocketAddress(8080));
		
		log.info("http server start success !!!");
		log.info("=========================start success========================");
	}
	
	public static void main(String[] args){
		HttpServer serv = new HttpServer();
		try {
			serv.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
