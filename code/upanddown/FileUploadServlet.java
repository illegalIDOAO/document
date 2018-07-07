package com.kaishengit.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@WebServlet("/upload")
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/upload.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //������enctype����ֵ����Ϊmultipart/form-data֮��
        //�ᵼ��request.getParameter()�����޷���ȡ��Ԫ�ص�ֵ

        //1.�ļ��ϴ����ŵ�·��
        File saveDir = new File("D:/upload");
        if(!saveDir.exists()) {
            saveDir.mkdir();
        }
        //2.�����ļ��ϴ�����ʱ·��
        File tempDir = new File("D:/tempdir");
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }

        //3.�жϱ��Ƿ�����enctype����
        if(ServletFileUpload.isMultipartContent(req)) {

            DiskFileItemFactory itemFactory = new DiskFileItemFactory();
            itemFactory.setSizeThreshold(1024); //��������С
            itemFactory.setRepository(tempDir); //��ʱ�ļ�·��

            ServletFileUpload servletFileUpload = new ServletFileUpload(itemFactory);
            servletFileUpload.setSizeMax(1024 * 1024 * 10); //�ϴ��ļ�������

            try {
                //��ȡ���е����б�Ԫ��(��ͨԪ��+�ļ�Ԫ��)����װ��FileItem����
                List<FileItem> fileItemList = servletFileUpload.parseRequest(req);

                for(FileItem item : fileItemList) {
                    if(item.isFormField()) {
                        //��ͨԪ��
                        System.out.println("FieldName:" + item.getFieldName());
                        //System.out.println("getString:" + item.getString());
                        System.out.println("getString:" + item.getString("UTF-8"));
                    } else {
                        //�ļ�Ԫ��
                        System.out.println("FieldName:" + item.getFieldName()); //��ȡ����name���Ե�ֵ
                        System.out.println("Name:" + item.getName()); //��ȡ�ϴ��ļ���ԭʼ����(�ļ���)

                        //��ȡ�ļ���������
                        InputStream inputStream = item.getInputStream();

                        String fileName = item.getName(); //1.jpg
                        String newFileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));

                        FileOutputStream outputStream = new FileOutputStream(new File(saveDir,newFileName)); // D:/upload/2.txt

                        IOUtils.copy(inputStream,outputStream);

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        /*
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while((len  = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer,0,len);
                        }
                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();*/

                        System.out.println(item.getName() + "  -> �ļ��ϴ��ɹ���");


                    }
                }


            } catch (FileUploadException e) {
                e.printStackTrace();
            }


        } else {
            throw new RuntimeException("form��enctype���������쳣");
        }




    }
}