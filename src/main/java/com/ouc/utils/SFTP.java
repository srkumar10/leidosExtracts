package com.ouc.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;

public class SFTP {

    ChannelSftp channelSftp = null;
    Session session = null;
    Channel channel = null;

    String SFTPHOST = "ouc-test.ifactornotifi.com";
    int SFTPPORT = 22;
    String SFTPUSER = "ouctest";
    String SFTPPASS = "uAUG$@^s*D50";
    String SFTPWORKINGDIR = "/home/ouctest/test";


    public Channel createConnection() {

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();

        } catch (Exception ex) {
            System.out.println("Exception found while tranfer the response.");
            ex.printStackTrace();
        }

        return channel;

    }

    public void send (Channel channel, String fileName) {

        System.out.println("preparing the host information for sftp.");
        try {
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File(fileName);
            channelSftp.put(new FileInputStream(f), f.getName());
            System.out.println("File transfered successfully to host.");
        } catch (Exception ex) {
            System.out.println("Exception found while tranfer the response.");
            ex.printStackTrace();
        }
    }

    public void disconnect(Channel channel) {
        channelSftp = (ChannelSftp)channel;
        channelSftp.exit();
        System.out.println("sftp Channel exited.");
        channel.disconnect();
        System.out.println("Channel disconnected.");
        session.disconnect();
        System.out.println("Host Session disconnected.");

    }
}
