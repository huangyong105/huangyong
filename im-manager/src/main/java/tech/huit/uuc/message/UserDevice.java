package tech.huit.uuc.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.huit.socket.cp.message.Msg;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * 用户登录设备
 *
 * @author huitang
 */
public class UserDevice implements Serializable {
    transient SocketChannel socket;//用户链接
    Msg.DeviceType deviceType;//设备类型

    public UserDevice() {
    }

    public UserDevice(SocketChannel userSocket, Msg.DeviceType deviceType) {
        this.socket = userSocket;
        this.deviceType = deviceType;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public String getIp() {
        String ip = null;
        try {
            ip = socket.getRemoteAddress().toString();
            if (ip.length() > 0) {
                ip = ip.substring(1);
            }
        } catch (IOException e) {
        }
        return ip;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public Msg.DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Msg.DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof UserDevice))
            return false;
        UserDevice castOther = (UserDevice) other;

        return new EqualsBuilder().append(deviceType, castOther.deviceType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(deviceType).hashCode();
    }

    @Override
    public String toString() {
        return "UserDevice{" +
                "socket=" + socket +
                ", deviceType=" + deviceType +
                '}';
    }
}
