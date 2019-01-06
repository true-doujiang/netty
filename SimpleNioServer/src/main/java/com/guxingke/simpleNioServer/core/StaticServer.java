package com.guxingke.simpleNioServer.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

public class StaticServer {

	private SocketChannel socketChannel;
	private HttpRequest request;
	private HttpResponse response;
	private Path filePath;
	private String extName;
	private int size;
	private ByteBuffer byteBuffer;

	public StaticServer(HttpRequest request, HttpResponse response) {
		this.request = request;
		this.socketChannel = request.getSocketChannel();
		this.response = response;
		this.filePath = getFileName(request.getRequestUrl());
		this.extName = getExtName(request.getRequestUrl());
	}

	private String getExtName(String requestUrl) {
		return requestUrl.substring(requestUrl.lastIndexOf(".") + 1);
	}

	private Path getFileName(String requestUrl) {
		if ("/favicon.ico".equals(requestUrl) || "robots.txt".equals(requestUrl)) {
			return Paths.get(Options.getResourcesPath() + requestUrl);
		}
		int start = Options.getResourcesFlag().length() + 1;
		return Paths.get(Options.getResourcesPath() + requestUrl.substring(start));
	}

	public boolean isFileExist() {
		return Files.exists(filePath);
	}

	public boolean update() throws IOException {
		int lastTime = (int) Files.getLastModifiedTime(filePath).to(TimeUnit.SECONDS);// 获取最后修改的时间戳
		Object paramTime = this.request.getHeader().get("If-Modified-Since");

		if ((paramTime != null) && (Integer.parseInt((String) paramTime) <= lastTime)) {
			// 处理304
			response.setStatus(304);
			response.setContentType(response.findContentType(this.extName));
			response.addHeader("Last-Modified ", lastTime + "");
			response.addHeader("Cache-Control", "public, max-age=2592000");
			byte[] headerBytes = response.getMsgHeader().getBytes();
			ByteBuffer writerBuffer = ByteBuffer.allocate(headerBytes.length);
			writerBuffer.clear();
			writerBuffer.put(headerBytes);
			writerBuffer.flip();
			while (writerBuffer.hasRemaining()) {
				this.socketChannel.write(writerBuffer);
			}
			writerBuffer.clear();
			this.socketChannel.close();
			return false;
		}
		return true;
	}

	public void read() throws Exception {
		// 处理304
		if (this.update()) {
			// 处理200
			BasicFileAttributeView basicView = Files.getFileAttributeView(this.filePath, BasicFileAttributeView.class);

			BasicFileAttributes basicAttrs = basicView.readAttributes();
			long size = basicAttrs.size();
			if (size > 2097152) {
				// 说明文件大于2M
			}
			this.size = (int) size;
			int lastTime = (int) Files.getLastModifiedTime(filePath).to(TimeUnit.SECONDS);// 获取最后修改的时间戳
			this.byteBuffer = ByteBuffer.allocate((int) size);// 申请缓存空间
			// 开始获取通道，读取文件
			AsynchronousFileChannel afc = AsynchronousFileChannel.open(this.filePath);
			afc.read(byteBuffer, 0, lastTime, new CompletionHandlerImpl(afc));
		}
	}

	/**
	 * 成员类 处理文件完成后的操作
	 */
	private class CompletionHandlerImpl implements CompletionHandler<Integer, Object> {

		AsynchronousFileChannel afc;

		public CompletionHandlerImpl(AsynchronousFileChannel afc) {
			this.afc = afc;
		}

		/**
		 * 处理成功的操作
		 *
		 * @param result
		 * @param attachment
		 */
		public void completed(Integer result, Object attachment) {

			Integer lastModifyInteger = (Integer) attachment;
			int lastModify = lastModifyInteger.intValue();
			// Header不能使用Response
			response.setStatus(200);
			response.setContentType(response.findContentType(extName));
			response.addHeader("Last-Modified ", lastModify + "");
			response.addHeader("Cache-Control", "public, max-age=2592000");
			response.setContentLength(size);

			byte[] headerBytes = response.getMsgHeader().getBytes();
			byte[] contentBytes = byteBuffer.array();
			ByteBuffer writerBuffer = ByteBuffer.allocate(headerBytes.length + contentBytes.length);
			writerBuffer.clear();

			writerBuffer.put(headerBytes);
			writerBuffer.put(contentBytes);
			writerBuffer.flip();
			try {
				while (writerBuffer.hasRemaining()) {
					socketChannel.write(writerBuffer);
				}
				writerBuffer.clear();
				socketChannel.finishConnect();
				socketChannel.socket().close();
				socketChannel.close();
				this.afc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 处理失败的操作
		 *
		 * @param exc
		 * @param attachment
		 */
		public void failed(Throwable exc, Object attachment) {
			System.out.println(exc.getCause());
			// @TODO 处理500错误
			try {
				socketChannel.close();
				this.afc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
