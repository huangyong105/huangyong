package tech.huit.uuc.entity.user;

import tech.huit.entity.AbstractEntity;
import java.util.Date; 
public class AppUser extends AbstractEntity { 
	/** 用户名 **/
	private String username;

	/** 用户密码 **/
	private String password;

	/** 显示昵称 **/
	private String nickname;

	/** 手机 **/
	private String phone;

	/** 自动登录token **/
	private String token;

	/** 创建时间 **/
	private Date createTime;

	public String getUsername(){
		return this.username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getPassword(){
		return this.password;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public String getNickname(){
		return this.nickname;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	public String getPhone(){
		return this.phone;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public String getToken(){
		return this.token;
	}
	public void setToken(String token){
		this.token = token;
	}
	public Date getCreateTime(){
		return this.createTime;
	}
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
}