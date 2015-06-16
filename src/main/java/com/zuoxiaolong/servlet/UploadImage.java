package com.zuoxiaolong.servlet;

import com.zuoxiaolong.config.Configuration;
import com.zuoxiaolong.dao.UserDao;
import com.zuoxiaolong.util.IOUtil;
import com.zuoxiaolong.util.ImageUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author 左潇龙
 * @since 2015年6月16日 上午12:05:05
 */
public class UploadImage extends AbstractServlet {

	@Override
	protected void service() throws ServletException, IOException {
		if (!isLogin()) {
			throw new RuntimeException();
		}
		HttpServletRequest request = getRequest();
		String path = null;
		DiskFileItemFactory factory = new DiskFileItemFactory(5 * 1024, new File(Configuration.getContextPath("temp")));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(1024 * 1024);
		try {
			List<FileItem> items = upload.parseRequest(request);
			if (items != null && items.size() > 0) {
				FileItem fileItem = items.get(0);
				path = ImageUtil.generatePath(fileItem.getName());
				IOUtil.copy(fileItem.getInputStream(), Configuration.getContextPath(path));
				fileItem.delete();
			}
		} catch (FileUploadException e) {
			throw new RuntimeException(e);
		}
		UserDao.uploadImage(getUsername(), Configuration.getSiteUrl(path));
		getResponse().sendRedirect(Configuration.getSiteUrl("/common/user_center.ftl"));
	}

}