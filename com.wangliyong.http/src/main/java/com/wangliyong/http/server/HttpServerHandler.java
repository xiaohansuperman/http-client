package com.wangliyong.http.server;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.wangliyong.tool.date.DateTool;


public class HttpServerHandler extends SimpleChannelUpstreamHandler{
	private static final Log log = LogFactory.getLog(HttpServerHandler.class);
	
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String CONNECTION = "Connection";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CACHE_CONTROL = "Cache-Control";
	//private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String EXPIRES = "Expires";
	private static final String LAST_MODIFIED = "Last-Modified";
	private static final String DATE = "Date";
	private static final String AGE = "Age";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	
	private HttpRequest request;
	private boolean readingChunks; 
	
	private static final DateTool dateTool = DateTool.getInstance();
	
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
		if(!readingChunks){
			HttpRequest request = (HttpRequest) e.getMessage();
			this.readingChunks = request.isChunked();
			
			if(request.getMethod() == HttpMethod.GET){
				onHttpGet(request, ctx, e);
			}else if(request.getMethod() == HttpMethod.POST){
				onHttpPost(request, ctx, e);
			}else if(request.getMethod() == HttpMethod.DELETE){
				onHttpDelete(request, ctx, e);
			}
		}else{
			onHttpChunk(request, ctx, e);
		}
	}
	
	/**
	 * 
	 * CONTENT_DISPOSITION,"attachment;filename=FileName.txt"
	 * 
	 * EXPIRES GMT格式
	 * 
	 * CACHE_CONTROL 单位秒
	 * 
	 * CONNECTION, "keep-alive", 这里是否保持长连接有待考虑
	 * 
	 * EXPIRES 缓存截止时间
	 * 
	 * */
	public void onHttpGet(HttpRequest request,ChannelHandlerContext ctx,MessageEvent e) throws IOException{
		
		String modifiedSince = request.getHeader(IF_MODIFIED_SINCE);
		if(modifiedSince != null){
			
			writeResponse(request, 
					e.getChannel(), 
					HttpResponseStatus.NOT_MODIFIED, 
					null, 
					CONTENT_TYPE, "image/jpeg",
					CONTENT_LENGTH, "0",
					CONNECTION, "keep-alive",
					AGE, "1",
					EXPIRES, dateTool.formatHTTPDateGMT(dateTool.addDays(60)),
					CACHE_CONTROL, "max-age=3600"
			);
		}else{
			
			String url = request.getUri();
			File file = new File("H:/壁纸"+url);
			byte[] bt = Files.readAllBytes(file.toPath());
			
			writeResponse(request, 
					e.getChannel(), 
					HttpResponseStatus.OK, 
					bt, 
					CONTENT_TYPE, "image/jpeg",
					CONTENT_LENGTH, String.valueOf(bt.length),
					CONNECTION, "keep-alive",
					AGE, "1",
					EXPIRES, "Thu, 01 Dec 1994 16:00:00 GMT",
					CACHE_CONTROL, "max-age=3600",
					LAST_MODIFIED, "Thu, 01 Dec 1994 16:00:00 GMT",
					DATE, "Thu, 01 Dec 1994 16:00:00 GMT"
			);
		}
		
	}
	
	public void onHttpPost(HttpRequest request,ChannelHandlerContext ctx,MessageEvent e){
		
	}
	
	public void onHttpDelete(HttpRequest request,ChannelHandlerContext ctx,MessageEvent e){
		
	}
	
	public void onHttpChunk(HttpRequest request,ChannelHandlerContext ctx,MessageEvent e){
		
	}
	
	public static void writeResponse(HttpRequest request, 
			Channel channel, 
			HttpResponseStatus status, 
			byte[] content, 
			String... headers) {
		
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		if(headers != null) {
			for(int i=0; i<headers.length; ) {
				if(headers[i+1]!=null && !headers[i+1].equals("")) {
					response.setHeader(headers[i], headers[i + 1]);
				}
				i += 2;
			}
		}
		if(content != null) {
			response.setContent(ChannelBuffers.copiedBuffer(content));
		}
		ChannelFuture writeFuture = channel.write(response);
		
		//是否是长链接
		boolean close = ("close".equalsIgnoreCase(request.getHeader("Connection")))
				|| ((request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)) && (!"keep-alive".equalsIgnoreCase(request.getHeader("Connection"))));
		if (close && writeFuture != null) {
			writeFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	//当客户端关闭这个channel时,channelgroup自动移除这个channel
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
		HttpServer.allChannels.add(e.getChannel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Channel ch = e.getChannel();
		Throwable ex = e.getCause();
 
		log.error("处理异常!",ex);

		if(ex instanceof ClosedChannelException || ex instanceof IOException) {
			if(ch.isOpen() || ch.isConnected()) {
				ch.close();
			}
		} else {
			ch.close();
			e.getCause().printStackTrace();
		}
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		ctx.getChannel().close();
	}
}
